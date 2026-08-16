# millionsend-java

Official Java SDK for [MillionSend](https://github.com/MillionSend/millionsend) — a self-hostable, Resend-compatible email API on AWS SES.

The API is wire-compatible with Resend, and this SDK deliberately mirrors the
shape of [`resend-java`](https://github.com/resend/resend-java), so migrating is
mostly a find-and-replace: swap the dependency, the class name, and point the
base URL at your instance.

## Install

Maven:

```xml
<dependency>
  <groupId>com.millionsend</groupId>
  <artifactId>millionsend-java</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'com.millionsend:millionsend-java:0.1.0'
```

Requires Java 11+.

## Quickstart

```java
import com.millionsend.MillionSend;
import com.millionsend.MillionSendException;
import com.millionsend.model.CreateEmailOptions;
import com.millionsend.model.CreateEmailResponse;

MillionSend ms = new MillionSend("ms_123", "https://mail.acme.dev");

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
new MillionSend(apiKey);                 // baseUrl from MILLIONSEND_BASE_URL or the default
new MillionSend(apiKey, baseUrl);        // explicit base URL
```

- `apiKey` falls back to `MILLIONSEND_API_KEY`. A missing key throws `IllegalArgumentException` at construction.
- `baseUrl` falls back to `MILLIONSEND_BASE_URL`, then `http://localhost:3001`. MillionSend is self-hosted, so **set this to your deployment in production.**

## Error handling

Every call throws a single checked `MillionSendException` on a non-2xx response.
It carries the API's error shape:

- `getName()` — a stable snake_case code you can switch on (`validation_error`, `not_found`, `restricted_api_key`, `sending_paused`, …)
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

## Resources

### Emails

```java
ms.emails().send(options);                 // POST /emails
ms.emails().send(options, idempotencyKey); // with an Idempotency-Key
ms.emails().get(id);                        // GET /emails/{id}
ms.emails().cancel(id);                     // POST /emails/{id}/cancel (scheduled only)
ms.batch().send(List.of(a, b), key);        // POST /emails/batch (up to 100)
```

Send options are camelCase and mapped to the wire: `replyTo` → `reply_to`,
`scheduledAt` → `scheduled_at`. `to`/`cc`/`bcc`/`replyTo` accept one or more
addresses (`.to("a@x.dev")` or `.to(List.of(...))`).

### Audiences & contacts

```java
Audience audience = ms.audiences().create(CreateAudienceOptions.builder().name("Registered users").build());
ms.audiences().list(ListOptions.builder().limit(20).after(cursor).build());
ms.audiences().get(id);
ms.audiences().remove(id);

ms.contacts().create(CreateContactOptions.builder()
    .audienceId(audienceId).email("ada@acme.dev").firstName("Ada")
    .properties(Map.of("plan", "pro")).build());
ms.contacts().get(ContactAddress.builder().audienceId(audienceId).email("ada@acme.dev").build());
ms.contacts().get(contactId);                                       // bare id shorthand
ms.contacts().update(UpdateContactOptions.builder()
    .id(id).unsubscribed(true).firstName(null).build());            // null clears a field
ms.contacts().remove(ContactAddress.email("ada@acme.dev"));
ms.contacts().list(audienceId, ListOptions.builder().limit(50).build());

// Topic subscriptions (granular unsubscribe)
ms.contacts().topics().update(UpdateContactTopicsOptions.builder()
    .email("ada@acme.dev").topic(topicId, Subscription.OPT_OUT).build());
```

Addressing a contact: pass a bare id string, or a `ContactAddress` by id or
email (email wins when both are set), optionally scoped to an audience.

### Topics

```java
ms.topics().create(CreateTopicOptions.builder()
    .name("Product updates").defaultSubscription(Subscription.OPT_IN).build());
ms.topics().get(id);
ms.topics().list();     // bare { data } — topics are unpaginated
ms.topics().remove(id);
```

### Broadcasts

```java
Id b = ms.broadcasts().create(CreateBroadcastOptions.builder()
    .audienceId(audienceId).from("Acme <news@acme.dev>").subject("Launch")
    .html("<p>Hi {{{FIRST_NAME|there}}}</p>").build());
ms.broadcasts().list();
ms.broadcasts().get(id);
ms.broadcasts().update(id, UpdateBroadcastOptions.builder().subject("Launch 🚀").build()); // draft only
ms.broadcasts().send(id, SendBroadcastOptions.builder().scheduledAt("2026-09-01T09:00:00Z").build());
ms.broadcasts().send(id);      // send now
ms.broadcasts().cancel(id);    // scheduled only
ms.broadcasts().remove(id);    // draft only
```

### Segments (MillionSend extension)

Dynamic segments are a saved filter over an audience's contacts — a MillionSend
superset with no Resend equivalent (served under `/segments2`).

```java
ms.segments().create(CreateSegmentOptions.builder()
    .name("Pro plan").audienceId(audienceId)
    .filter(SegmentFilter.builder().match("all")
        .condition(new SegmentCondition("property:plan", "equals", "pro")).build())
    .build());
ms.segments().get(id);   // includes a live contactCount
ms.segments().list();
ms.segments().update(id, UpdateSegmentOptions.builder().name("Pro tier").build());
ms.segments().remove(id);
```

## Migrating from Resend

```diff
- import com.resend.Resend;
- Resend resend = new Resend("re_123");
+ import com.millionsend.MillionSend;
+ MillionSend ms = new MillionSend("ms_123", "https://mail.acme.dev");
```

Accessor and method names match (`resend.emails().send(...)` →
`ms.emails().send(...)`). Notes:

- **Domains and API keys** are managed in the MillionSend dashboard, not via the API, so there are no `.domains()`/`.apiKeys()` resources here.
- Errors throw `MillionSendException` (in place of Resend's `ResendException`) with the same `{ statusCode, name, message }` fields.
- Resend's `.segments()` is an alias of audiences; MillionSend's `.segments()` is the distinct dynamic-filter feature. Use `.audiences()` for a straight port.

## Build & test

```bash
mvn test          # unit tests over a mocked HTTP layer
mvn verify        # + build the jar
```

The integration test in `E2ETest` runs only when `MILLIONSEND_API_KEY` is set
(and `MILLIONSEND_BASE_URL` if not localhost); it is skipped otherwise.

## License

MIT
