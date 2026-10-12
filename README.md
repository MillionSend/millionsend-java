# millionsend-java

Official Java SDK for [MillionSend](https://github.com/MillionSend/millionsend) — a self-hostable, Resend-compatible email API on AWS SES.

The API is wire-compatible with Resend, and this SDK deliberately mirrors the
shape of [`resend-java`](https://github.com/resend/resend-java), so migrating is
mostly a find-and-replace: swap the dependency and the class name. MillionSend
Cloud works with just the key; a self-hosted instance also sets its origin.

## Install

Maven:

```xml
<dependency>
  <groupId>com.millionsend</groupId>
  <artifactId>millionsend-java</artifactId>
  <version>0.9.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'com.millionsend:millionsend-java:0.9.0'
```

Requires Java 11+.

## Quickstart

```java
import com.millionsend.MillionSend;
import com.millionsend.MillionSendException;
import com.millionsend.model.CreateEmailOptions;
import com.millionsend.model.CreateEmailResponse;

MillionSend ms = new MillionSend("ms_123");                          // MillionSend Cloud
// MillionSend ms = new MillionSend("ms_123", "https://mail.acme.dev"); // self-hosted

try {
  CreateEmailResponse sent = ms.emails().send(
      CreateEmailOptions.builder()
          .from("Acme <onboarding@acme.dev>")
          .to("delivered@resend.dev")
          .subject("Hello from MillionSend")
          .html("<strong>It works!</strong>")
          .build());
  System.out.println("sent " + sent.getId());
} catch (MillionSendException e) {
  System.err.println(e.getName() + ": " + e.getMessage());
}
```

## Configuration

```java
new MillionSend();                       // apiKey + baseUrl from the environment
new MillionSend(apiKey);                 // baseUrl from MILLIONSEND_BASE_URL or MillionSend Cloud
new MillionSend(apiKey, baseUrl);        // explicit base URL (self-hosted)
new MillionSend(apiKey, baseUrl, true);  // also accept a non-loopback http:// base URL
```

- `apiKey` falls back to `MILLIONSEND_API_KEY`. A missing key throws `IllegalArgumentException` at construction.
- `baseUrl` falls back to `MILLIONSEND_BASE_URL`, then MillionSend Cloud (`https://api.millionsend.com`). Self-hosting? Set it to your instance's origin.
- Plain `http://` is only accepted for loopback hosts (`localhost`, `127.0.0.1`, `::1`); any other `http://` URL throws `IllegalArgumentException` at construction, since the API key is sent as a bearer header. Pass `allowInsecureHttp = true` (third constructor argument) to talk to a non-TLS instance elsewhere (e.g. inside a private network).

## Error handling

Every call throws a single checked `MillionSendException` on a non-2xx response.
It carries the API's error shape:

- `getName()` — a stable snake_case code you can switch on (`validation_error`, `not_found`, `restricted_api_key`, `sending_paused`, `all_recipients_suppressed`, …)
- `getMessage()` — a human-readable description
- `getStatusCode()` — the HTTP status, or `null` when the request never reached the API (a client-side or transport failure)

```java
try {
  ms.emails().get(id);
} catch (MillionSendException e) {
  if ("not_found".equals(e.getName())) {
    // …
  }
}
```

`POST /emails` and `POST /emails/batch` answer `422 all_recipients_suppressed`
("All recipients are suppressed") when every `to` recipient is on the
suppression list or opted out of the send's `topicId`.

## Resources

Every request field you set reaches the wire — nothing is dropped client-side.
Fields the server does not support yet (`template` on send, `from`/`replyTo`/`variables`
on templates) are forwarded as-is and answered with a 422.

### Request options

`emails().send`, `batch().send`, `contacts().batch().create` and the inbox
sends (`receiving().reply`/`forward`, `threads().emails().reply`/`forward`,
`drafts().send`) accept a `RequestOptions` (the same builder shape as `resend-java`):

```java
import com.millionsend.core.RequestOptions;
import com.millionsend.model.BatchValidation;

RequestOptions opts = RequestOptions.builder()
    .idempotencyKey("order-123")                   // Idempotency-Key (setIdempotencyKey also works)
    .batchValidation(BatchValidation.PERMISSIVE)   // x-batch-validation on the batch endpoints
    .add("X-Trace", "abc")                         // any extra header
    .build();
ms.emails().send(options, opts);
ms.emails().send(options, "order-123");            // the plain idempotency-key overload still works
```

### Emails

```java
ms.emails().send(options);                  // POST /emails
ms.emails().send(options, requestOptions);  // with an Idempotency-Key / extra headers
ms.emails().get(id);                        // GET /emails/{id} — includes a nullable score (0-10)
ms.emails().list(ListOptions.builder().limit(50).build()); // GET /emails
ms.emails().update(id, UpdateEmailOptions.builder().scheduledAt("2026-09-01T09:00:00Z").build());
ms.emails().getInsights(id);                // GET /emails/{id}/insights (404 not_found until computed)
ms.emails().cancel(id);                     // POST /emails/{id}/cancel (scheduled only)
ms.emails().remove(id);                     // DELETE /emails/{id} (MillionSend extension)
```

Send options are camelCase and mapped to the wire: `replyTo` → `reply_to`,
`scheduledAt` → `scheduled_at`, `topicId` → `topic_id`. `to`/`cc`/`bcc`/`replyTo`
accept one or more addresses. Also available: `.tag(new Tag(name, value))`,
`.attachment(Attachment.builder().fileName("a.pdf").content(base64).build())`
(or `.path(url)`, plus `contentType`/`contentId`), `.header(name, value)` and
`.template(...)` (forwarded; the server answers 422 — send `html`/`text`).
`resend-java`'s `addTag`/`addAttachment`/`addHeader` spellings work too.

### Batch

```java
CreateBatchEmailsResponse res = ms.batch().send(List.of(a, b),
    RequestOptions.builder().idempotencyKey(key).batchValidation(BatchValidation.PERMISSIVE).build());
res.getData();    // accepted ids
res.getErrors();  // permissive mode: [{ index, message }] for rejected items; null when all accepted
```

Strict mode (the default) rejects the whole batch on the first invalid item.

### Contacts

Contacts are team-global — one list per team, no audiences.

```java
ms.contacts().create(CreateContactOptions.builder()
    .email("ada@acme.dev").firstName("Ada")
    .properties(Map.of("plan", "pro"))
    .segment(segmentId)                              // initial segment membership
    .topic(topicId, Subscription.OPT_OUT)            // initial topic choice
    .build());                                       // 409 validation_error on duplicate email
ms.contacts().get(ContactAddress.email("ada@acme.dev"));
ms.contacts().get(contactId);                        // bare id shorthand
ms.contacts().update(UpdateContactOptions.builder()
    .id(id).unsubscribed(true).firstName(null).build()); // null clears a field
ms.contacts().remove(ContactAddress.email("ada@acme.dev"));   // the contact's emails stay in the log
ms.contacts().remove(RemoveContactOptions.builder()
    .email("ada@acme.dev").erase(true).build());           // ?erase=true — also scrubs the address from history
ms.contacts().list(ListOptions.builder().limit(50).build());
// Bulk read (MillionSend extension): attach properties and topic subscriptions to every item,
// so an audience reads in one request per 100 contacts instead of one per contact
ms.contacts().list(ListContactsOptions.builder().limit(100)
    .include(ContactInclude.PROPERTIES, ContactInclude.TOPICS).build()); // ?include=properties,topics

// Topic subscriptions (granular unsubscribe)
ms.contacts().topics().update(UpdateContactTopicsOptions.builder()
    .email("ada@acme.dev").topic(topicId, Subscription.OPT_OUT).build());
ListResponse<ContactTopic> topics = ms.contacts().topics().list("ada@acme.dev"); // GET /contacts/{idOrEmail}/topics
topics.getData().get(0).getSubscription(); // effective choice: the contact's own, else the topic default
topics.getData().get(0).isExplicit();      // false when it is the topic default
topics.getData().get(0).getVisibility();   // "public" | "private" — the hosted page lists public topics only

// Preference-center link (MillionSend extension): the contact's hosted preference page,
// the same one their emails' unsubscribe links open. No expiry — show it only to the contact.
PreferencesLink link = ms.contacts().preferencesLink(ContactAddress.email("ada@acme.dev")); // POST /contacts/{idOrEmail}/preferences-link
link.getUrl();                                                    // 422 when the instance cannot build hosted links

// Segment membership
ms.contacts().segments().add(contactIdOrEmail, segmentId);     // POST /contacts/{id}/segments/{segmentId}
ms.contacts().segments().remove(contactIdOrEmail, segmentId);  // DELETE …

// Bulk creation (MillionSend extension): up to 1000 per call
CreateBatchContactsResponse res = ms.contacts().batch().create(items,
    ConflictMode.UPSERT,                                        // ?on_conflict=error|skip|upsert
    RequestOptions.builder().batchValidation(BatchValidation.PERMISSIVE).build());
res.getData();     // [{ index, id, status: created|updated|skipped }]
res.getCounts();   // created / updated / skipped / failed
res.getErrors();   // permissive mode: rejected items

// Bulk lookup (MillionSend extension): up to 1000 contacts by id or email in one request,
// in request order; unknown entries are listed, not errors — one request against the rate limit
BatchGetContactsResponse found = ms.contacts().batch().get(
    List.of(ContactAddress.id(contactId), ContactAddress.email(email)), ContactInclude.TOPICS);
found.getData();    // [{ object: "contact", id, email, ..., topics }] — the contacts found
found.getMissing(); // [{ index, email }] — request entries that matched nobody

// Bulk removal (MillionSend extension): up to 1000 per call, by ids or by emails;
// their emails stay in the log unless erase also scrubs the addresses from history
ms.contacts().batch().remove(RemoveContactsOptions.builder().emails(List.of(a, b)).build()); // or .ids(...)
ms.contacts().batch().remove(RemoveContactsOptions.builder().ids(List.of(a)).erase(true).build());
// → getData(): [{ contact, deleted: true }] for the rows actually deleted
```

`Contact.getProperties()` returns `Map<String, ContactPropertyValue>` — each
value carries its declared `type` (`string`/`number`) and the `value`.
`Contact.getTopics()` returns the same rows as `contacts().topics().list(...)`;
on list and batch reads both are `null` unless `include` asked for them.

### Contact properties

```java
ms.contactProperties().create(CreateContactPropertyOptions.builder()
    .key("plan").type("string").fallbackValue("free").build());
ms.contactProperties().get(id);
ms.contactProperties().list();
ms.contactProperties().update(UpdateContactPropertyOptions.builder().id(id).fallbackValue(null).build()); // null clears
ms.contactProperties().remove(id);
```

### Topics

```java
ms.topics().create(CreateTopicOptions.builder()
    .name("Product updates").defaultSubscription(Subscription.OPT_IN).visibility("public").build());
ms.topics().get(id);
ms.topics().list();     // bare { data } — topics are unpaginated
ms.topics().update(id, UpdateTopicOptions.builder().name("Product news").build());
ms.topics().remove(id);
```

### Broadcasts

```java
Id b = ms.broadcasts().create(CreateBroadcastOptions.builder()
    .from("Acme <news@acme.dev>").subject("Launch")
    .segmentId(segmentId)          // optional; also .topicId(...) — neither sends to all contacts
    .previewText("It's here")
    .html("<p>Hi {{{FIRST_NAME|there}}}</p>")
    .send(true).scheduledAt("in 1 hour")   // send/schedule on create instead of saving a draft
    .build());
ms.broadcasts().list();
ms.broadcasts().get(id);
ms.broadcasts().update(id, UpdateBroadcastOptions.builder().subject("Launch 🚀").topicId(null).build()); // draft only; topicId(null) clears
ms.broadcasts().send(id, SendBroadcastOptions.builder().scheduledAt("2026-09-01T09:00:00Z").build());
ms.broadcasts().send(id);      // send now
ms.broadcasts().cancel(id);    // scheduled only
ms.broadcasts().remove(id);    // draft only
```

### Segments (MillionSend extension)

Dynamic segments are a saved filter over your contacts — a MillionSend
superset with no Resend equivalent.

```java
ms.segments().create(CreateSegmentOptions.builder()
    .name("Pro plan")
    .filter(SegmentFilter.builder().match("all")
        .condition(new SegmentCondition("property:plan", "equals", "pro")).build())
    .build());
ms.segments().get(id);   // includes a live contactCount
ms.segments().list();
ms.segments().contacts(id, ListOptions.builder().limit(100).build()); // matching contacts
ms.segments().contacts(id, ListContactsOptions.builder()
    .include(ContactInclude.PROPERTIES).build());                     // + properties per item
ms.segments().update(id, UpdateSegmentOptions.builder().name("Pro tier").build());
ms.segments().remove(id);
```

### Suppressions

```java
ms.suppressions().add(AddSuppressionOptions.builder()
    .email("bounced@example.com").origin(SuppressionOrigin.MANUAL).build());
ms.suppressions().get(idOrEmail);
ms.suppressions().list(ListSuppressionsOptions.builder().origin(SuppressionOrigin.BOUNCE).limit(50).build());
ms.suppressions().remove(idOrEmail);
ms.suppressions().batch().add(AddSuppressionsOptions.builder().emails(List.of(a, b)).build());   // up to 1000
ms.suppressions().batch().remove(RemoveSuppressionsOptions.builder().emails(List.of(a)).build()); // or .ids(...)
```

### Receiving

Mail received at your receiving domains and inboxes. Everything in a received
email was written by its sender, so show its HTML only in a sandbox.

```java
ReceivedEmailListResponse page = ms.receiving().list(ListReceivedEmailsOptions.builder()
    .inboxId(inboxId).from("acme.dev").subjectContains("code").limit(20).build()); // newest first
ReceivedEmail email = ms.receiving().get(id);              // GET /emails/receiving/{id}
ms.receiving().get(id, "cid");                             // leave inline images as cid: references
email.getExtractedText();                                  // the reply without quotes, signature or hidden text
email.getScreening().getSenderTrust();                     // owner, allowlisted, known_thread, external, …
email.getExtracted().getCode();                            // a verification code, when one was found

// Long-poll: returns as soon as matching mail arrives (or an empty page after 30 s)
ReceivedEmailListResponse next = ms.receiving().list(ListReceivedEmailsOptions.builder()
    .inboxId(inboxId).waitSeconds(30).build());
next.getHeld();                                            // matching mail held in quarantine, ids only

SendResult<ThreadEmail> r = ms.receiving().reply(id,
    ReplyOptions.builder().text("Thanks, confirmed.").build(),
    RequestOptions.idempotencyKey("reply-" + id));
if (r.isDraft()) {
  r.getDraft().getApprovalReasons();                       // held for a person to approve (HTTP 202)
} else {
  r.getSent().getThreadId();
}
ms.receiving().forward(id, ForwardOptions.builder().to("ops@acme.dev").mode("attachment").build());
ms.receiving().remove(id);                                 // erase now

ms.receiving().attachments().list(id);                     // signed download links, valid one hour
ms.receiving().attachments().get(id, attachmentId);
```

Held mail is invisible to inbox-scoped keys: a detail read answers 403
`email_quarantined`. For full-access keys, `includeQuarantined(true)` lists it
too and `quarantined(true)` lists only held mail.

### Inboxes

An inbox is one address that receives mail, read by an agent (`kind` `agent`,
the default) or by people (`human`), with threads in both directions. The
paths mirror Resend's Inboxes API; `kind`, `shared`, `expiresAt`, safety,
quarantine and codes are MillionSend extensions.

```java
Inbox inbox = ms.inboxes().create(CreateInboxOptions.builder()
    .name("Signup bot").expiresIn("24h")   // no emailAddress: a random address on the shared domain
    .safety(InboxSafetyOptions.builder().acceptFrom("anyone").build())
    .build());
ms.inboxes().list(ListInboxesOptions.builder().kind("agent").build());
ms.inboxes().get("bot@acme.dev");                          // by id or address
ms.inboxes().update(inbox.getId(), UpdateInboxOptions.builder().expiresIn(null).build()); // null: never expires
ms.inboxes().remove(inbox.getId());

// Verification codes and sign-in links (links are never fetched)
VerificationCode code = ms.inboxes().codes().latest(inboxId,
    LatestCodeOptions.builder().from("github.com").waitSeconds(30).build());
if ("found".equals(code.getStatus())) {
  code.getCode();                                          // or code.getUrl() for a sign-in link
}

// Safety rules (inbox-scoped keys can read them, never change them)
ms.inboxes().safety().get(inboxId);
ms.inboxes().safety().update(inboxId, InboxSafetyOptions.builder()
    .acceptFrom("allowlist").allowlist("*@partner.dev").sendPolicy("reply_only").sendCap(100).build());

// Quarantine: held mail is kept and listed until a person releases it
ms.inboxes().quarantine().list(inboxId);
ms.inboxes().quarantine().release(inboxId, emailId, true); // true also allowlists the sender

// Threads and their emails
ms.inboxes().threads().list(inboxId, ListThreadsOptions.builder().folders("inbox", "archive").read(false).build());
ms.inboxes().threads().search(inboxId, SearchThreadsOptions.builder().query("invoice -draft").from("acme").build());
ms.inboxes().threads().get(inboxId, threadId);
ms.inboxes().threads().update(inboxId, threadId, UpdateThreadOptions.builder().read(true).folder("archive").build());
ms.inboxes().threads().remove(inboxId, threadId);          // to trash, purged after 30 days
ms.inboxes().threads().emails().list(inboxId, threadId);   // oldest first
ms.inboxes().threads().emails().get(inboxId, threadId, emailId);
ms.inboxes().threads().emails().reply(inboxId, threadId, emailId, ReplyOptions.builder().text("On it").build());
ms.inboxes().threads().emails().forward(inboxId, threadId, emailId, ForwardOptions.builder().to("ops@acme.dev").build());

// Labels
InboxLabel label = ms.inboxes().labels().create(inboxId, InboxLabelOptions.builder().name("Billing").build());
ms.inboxes().labels().list(inboxId);
ms.inboxes().labels().update(inboxId, label.getId(), InboxLabelOptions.builder().color("#FF8800").build());
ms.inboxes().labels().remove(inboxId, label.getId());

// Drafts, including the ones the send rules hold for approval
InboxDraft d = ms.inboxes().drafts().create(inboxId, CreateDraftOptions.builder()
    .to("ada@example.com").subject("Hello").text("Hi Ada").build());
ms.inboxes().drafts().list(inboxId);
ms.inboxes().drafts().get(inboxId, d.getId());
ms.inboxes().drafts().update(inboxId, d.getId(), UpdateDraftOptions.builder().subject(null).build()); // null clears
SendResult<InboxDraft> s = ms.inboxes().drafts().send(inboxId, d.getId());
ms.inboxes().drafts().reject(inboxId, d.getId());          // approvers only
ms.inboxes().drafts().remove(inboxId, d.getId());
```

Reply, forward and draft send return a `SendResult`: `isDraft()` is true when
the inbox's send rules held the message for a person (the API answers 202); an
inbox-scoped key sending a pending draft gets 403 `approval_required`. Folder,
verdict, kind and status values are plain strings, so new server values never
break decoding. Hosted agent settings (`/inboxes/{id}/agent`) are not exposed:
MillionSend runs no agent of its own.

### Domains

```java
Domain d = ms.domains().create(CreateDomainOptions.builder()
    .name("acme.dev").region("us-east-1").customReturnPath("send")
    .clickTracking(true).trackingSubdomain("links").build());
d.getRecords();                 // DNS records to publish
ms.domains().get(id);
ms.domains().list();
ms.domains().verify(id);        // re-check DNS
ms.domains().update(UpdateDomainOptions.builder().id(id).openTracking(true).trackingSubdomain(null).build()); // null clears
ms.domains().update(UpdateDomainOptions.builder().id(id).receivingCatchAll(true).build()); // paid plans on Cloud
ms.domains().remove(id);
```

`Domain.getReceivingCatchAll()`: on, mail to addresses without an inbox lands in
`receiving().list()`; off, those addresses are refused at SMTP time.

### Webhooks

```java
CreateWebhookResponse w = ms.webhooks().create(CreateWebhookOptions.builder()
    .endpoint("https://acme.dev/hooks/millionsend")
    .events(WebhookEvent.EMAIL_DELIVERED, WebhookEvent.EMAIL_BOUNCED)
    .build());
w.getSigningSecret();           // verify payload signatures with this
ms.webhooks().get(id);          // also returns the signing secret and previousSecretExpiresAt
ms.webhooks().list();
ms.webhooks().update(id, UpdateWebhookOptions.builder().status("disabled").build());
ms.webhooks().remove(id);

// Rotate the signing secret (MillionSend extension)
RotateWebhookResponse r = ms.webhooks().rotate(id);                    // minted secret, default overlap
ms.webhooks().rotate(id, RotateWebhookOptions.builder()
    .signingSecret("whsec_...")   // bring your own; omit to mint
    .overlapHours(48)             // 0-72 hours the previous secret keeps signing too; 0 drops it at once
    .build());
r.getSigningSecret();             // sign-with from now on
r.getPreviousSecretExpiresAt();   // when the previous secret stops signing; null when none
```

During the overlap every delivery carries both signatures, so a receiver
holding either verifies. `deliverability.*`, `quota.*`, `contact.unsubscribed`,
`contact.resubscribed`, `contact.topic_opt_in`, `contact.topic_opt_out` and
`suppression.*` events are MillionSend extensions.

The `inbox.*` events (`INBOX_CREATED` … `INBOX_DRAFT_DELETED`) are delivered
only to endpoints that name them; an endpoint subscribed to all events skips
them (it still gets `email.received`). `inboxIds(...)` narrows them and
`email.received` to some inboxes (`UpdateWebhookOptions.builder().inboxIds(null)`
removes the filter).
`inbox.expired` and `inbox.email.quarantined` are MillionSend extensions.

### API keys

```java
CreateApiKeyResponse k = ms.apiKeys().create(CreateApiKeyOptions.builder()
    .name("ci").permission("sending_access").domainId(domainId).build());
k.getToken();                   // shown only once
ms.apiKeys().list();
ms.apiKeys().remove(id);

// A key restricted to one inbox: it reads that inbox and sends from its address only
ms.apiKeys().create(CreateApiKeyOptions.builder().name("support-agent").inboxId(inboxId).build());
```

### Templates

Every identifier argument accepts the template id or its alias.

```java
ms.templates().create(CreateTemplateOptions.builder()
    .name("Welcome").alias("welcome").subject("Hi {{{FIRST_NAME|there}}}").html("<p>…</p>").build());
ms.templates().get("welcome");
ms.templates().list();
ms.templates().update("welcome", UpdateTemplateOptions.builder().html("<p>new</p>").subject(null).build()); // null clears subject/text/alias
ms.templates().publish(id);     // no-op kept for compatibility: every save is already live
ms.templates().duplicate(id);
ms.templates().remove(id);
```

### Deliverability (MillionSend extension)

The account deliverability score over the trailing 30 days.

```java
DeliverabilityReport report = ms.deliverability().get();
report.getScore();            // Double, 0-10 — null until there is enough data
report.getBand();             // "excellent" | "good" | "needs_attention" | "at_risk" | null
report.getGuardrailStatus();  // "ok" | "warning" | "paused"
```

Bands, check severities/statuses and the guardrail status are plain strings
(the server may add values over time), and check ids are an open set.

### Usage (MillionSend extension)

```java
UsageReport u = ms.usage().get();
u.getPlan();                        // "free" | "pro" | "scale" | null when self-hosted
u.getLimits().getEmailsPerDay();    // null when unlimited
u.getToday().getEmailsSent();
```

## Migrating from Resend

```diff
- import com.resend.Resend;
- Resend resend = new Resend("re_123");
+ import com.millionsend.MillionSend;
+ MillionSend ms = new MillionSend("ms_123");
```

Self-hosting? Pass your instance's origin as the second argument (or set
`MILLIONSEND_BASE_URL`).

Accessor and method names match (`resend.emails().send(...)` →
`ms.emails().send(...)`, `resend.suppressions().batch().add(...)` →
`ms.suppressions().batch().add(...)`, and so on for `domains()`, `apiKeys()`,
`webhooks()`, `templates()`, `contactProperties()`, `broadcasts()`, `topics()`).
Notes:

- **No audiences**: contacts are team-global, so there is no `.audiences()` resource and no `audienceId` anywhere. The server keeps `/audiences/*` only as a compatibility shim; it is not part of this SDK. `.segments()` is MillionSend's dynamic-filter feature, not Resend's audience alias.
- **Request options** use the same `RequestOptions` builder shape (`setIdempotencyKey`, `add(header, value)`), plus `batchValidation(...)`.
- Errors throw `MillionSendException` (in place of Resend's `ResendException`) with the same `{ statusCode, name, message }` fields.
- **MillionSend extensions** (no Resend counterpart): `segments()`, `deliverability()`, `usage()`, `contacts().batch()`, `contacts().preferencesLink()`, `webhooks().rotate()`, `emails().remove()`, `emails().getInsights()`, `Email.getScore()`, and in `receiving()` and `inboxes()` the long-poll `waitSeconds`, the reply/forward/remove of received mail, inbox `safety()`, `codes()`, `quarantine()`, draft `reject()` and the extension fields.

## Build & test

```bash
mvn test          # unit tests over a mocked HTTP layer
mvn verify        # + build the jar
```

The integration test in `E2ETest` runs only when `MILLIONSEND_API_KEY` is set
(and `MILLIONSEND_BASE_URL` for a self-hosted instance); it is skipped otherwise.

## Releasing

`Release` (workflow_dispatch) publishes the current `main` to Maven Central
through the Central Portal with `mvn -P release deploy` (sources + javadoc
jars, GPG signatures, auto-publish, waits until the version is live). It
needs four repository secrets:

| Secret | Value |
| --- | --- |
| `CENTRAL_TOKEN_USERNAME` / `CENTRAL_TOKEN_PASSWORD` | A [central.sonatype.com](https://central.sonatype.com) user token (Account → Generate User Token) |
| `MAVEN_GPG_PRIVATE_KEY` | ASCII-armored private signing key (`gpg --armor --export-secret-keys <id>`) |
| `MAVEN_GPG_PASSPHRASE` | Its passphrase |

The public half of the signing key must be on a keyserver Central checks:
`gpg --keyserver keyserver.ubuntu.com --send-keys <id>`.

## License

MIT
