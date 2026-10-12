package com.millionsend.services;

import static com.millionsend.core.HttpClient.enc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.millionsend.MillionSendException;
import com.millionsend.core.HttpClient;
import com.millionsend.core.RequestOptions;
import com.millionsend.model.CreateDraftOptions;
import com.millionsend.model.CreateInboxOptions;
import com.millionsend.model.DeletedResponse;
import com.millionsend.model.ForwardOptions;
import com.millionsend.model.Inbox;
import com.millionsend.model.InboxDraft;
import com.millionsend.model.InboxLabel;
import com.millionsend.model.InboxLabelOptions;
import com.millionsend.model.InboxSafety;
import com.millionsend.model.InboxSafetyOptions;
import com.millionsend.model.InboxThread;
import com.millionsend.model.LatestCodeOptions;
import com.millionsend.model.ListInboxesOptions;
import com.millionsend.model.ListOptions;
import com.millionsend.model.ListResponse;
import com.millionsend.model.ListThreadsOptions;
import com.millionsend.model.QuarantineRelease;
import com.millionsend.model.QuarantinedEmail;
import com.millionsend.model.ReplyOptions;
import com.millionsend.model.SearchThreadsOptions;
import com.millionsend.model.SendResult;
import com.millionsend.model.ThreadEmail;
import com.millionsend.model.UpdateDraftOptions;
import com.millionsend.model.UpdateInboxOptions;
import com.millionsend.model.UpdateThreadOptions;
import com.millionsend.model.VerificationCode;
import java.util.Collections;

/**
 * The {@code inboxes} resource: addresses that receive mail for an agent or for
 * people, with threads in both directions. Sub-resources: {@link #safety()},
 * {@link #codes()}, {@link #quarantine()}, {@link #labels()}, {@link #threads()}
 * (and {@code threads().emails()}) and {@link #drafts()}.
 */
public final class Inboxes {

  private final HttpClient http;
  private final Safety safety;
  private final Codes codes;
  private final Quarantine quarantine;
  private final Labels labels;
  private final Threads threads;
  private final Drafts drafts;

  public Inboxes(HttpClient http) {
    this.http = http;
    this.safety = new Safety(http);
    this.codes = new Codes(http);
    this.quarantine = new Quarantine(http);
    this.labels = new Labels(http);
    this.threads = new Threads(http);
    this.drafts = new Drafts(http);
  }

  /** Safety rules: who may write in, what the inbox may send (MillionSend extension). */
  public Safety safety() {
    return safety;
  }

  /** Verification codes and sign-in links the inbox received (MillionSend extension). */
  public Codes codes() {
    return codes;
  }

  /** Mail the safety rules held (MillionSend extension). */
  public Quarantine quarantine() {
    return quarantine;
  }

  public Labels labels() {
    return labels;
  }

  public Threads threads() {
    return threads;
  }

  public Drafts drafts() {
    return drafts;
  }

  /** POST /inboxes — 409 when the address is taken, 403 at the plan's inbox limit. */
  public Inbox create(CreateInboxOptions options) throws MillionSendException {
    return http.request("POST", "/inboxes", options, null, null, new TypeReference<Inbox>() {});
  }

  /** GET /inboxes — newest first. */
  public ListResponse<Inbox> list() throws MillionSendException {
    return list(null);
  }

  /** GET /inboxes with pagination and a {@code kind} filter. */
  public ListResponse<Inbox> list(ListInboxesOptions options) throws MillionSendException {
    return http.request(
        "GET", "/inboxes", null, options == null ? null : options.toQuery(), null,
        new TypeReference<ListResponse<Inbox>>() {});
  }

  /** GET /inboxes/{idOrEmail} — by id or by address (case-insensitive). */
  public Inbox get(String idOrEmail) throws MillionSendException {
    return http.request("GET", inbox(idOrEmail), null, null, null, new TypeReference<Inbox>() {});
  }

  /** PATCH /inboxes/{id} — returns the updated inbox. */
  public Inbox update(String id, UpdateInboxOptions options) throws MillionSendException {
    return http.request("PATCH", inbox(id), options, null, null, new TypeReference<Inbox>() {});
  }

  /** DELETE /inboxes/{id} — removes its threads, drafts and labels and revokes its keys. */
  public DeletedResponse remove(String id) throws MillionSendException {
    return http.request(
        "DELETE", inbox(id), null, null, null, new TypeReference<DeletedResponse>() {});
  }

  private static String inbox(String id) {
    return "/inboxes/" + enc(id);
  }

  /** An inbox's safety rules. Inbox-scoped keys can read them but never change them. */
  public static final class Safety {

    private final HttpClient http;

    Safety(HttpClient http) {
      this.http = http;
    }

    /** GET /inboxes/{inboxId}/safety */
    public InboxSafety get(String inboxId) throws MillionSendException {
      return http.request(
          "GET", inbox(inboxId) + "/safety", null, null, null, new TypeReference<InboxSafety>() {});
    }

    /** PATCH /inboxes/{inboxId}/safety — only the rules set on {@code options}; returns them all. */
    public InboxSafety update(String inboxId, InboxSafetyOptions options)
        throws MillionSendException {
      return http.request(
          "PATCH", inbox(inboxId) + "/safety", options, null, null,
          new TypeReference<InboxSafety>() {});
    }
  }

  /** The newest verification code or sign-in link an inbox received. Links are never fetched. */
  public static final class Codes {

    private final HttpClient http;

    Codes(HttpClient http) {
      this.http = http;
    }

    /** GET /inboxes/{inboxId}/codes/latest */
    public VerificationCode latest(String inboxId) throws MillionSendException {
      return latest(inboxId, null);
    }

    /**
     * GET /inboxes/{inboxId}/codes/latest. With {@code waitSeconds} it long-polls for a code to
     * arrive; {@code status} is {@code timeout} when none did.
     */
    public VerificationCode latest(String inboxId, LatestCodeOptions options)
        throws MillionSendException {
      return http.longPoll(
          inbox(inboxId) + "/codes/latest",
          options == null ? null : options.toQuery(),
          options == null ? null : options.getWaitSeconds(),
          new TypeReference<VerificationCode>() {});
    }
  }

  /** Mail an inbox's safety rules held: kept, listed and releasable by a person. */
  public static final class Quarantine {

    private final HttpClient http;

    Quarantine(HttpClient http) {
      this.http = http;
    }

    /** GET /inboxes/{inboxId}/quarantine — newest first. */
    public ListResponse<QuarantinedEmail> list(String inboxId) throws MillionSendException {
      return list(inboxId, null);
    }

    /** GET /inboxes/{inboxId}/quarantine with pagination. */
    public ListResponse<QuarantinedEmail> list(String inboxId, ListOptions options)
        throws MillionSendException {
      return http.request(
          "GET", inbox(inboxId) + "/quarantine", null, options == null ? null : options.toQuery(),
          null, new TypeReference<ListResponse<QuarantinedEmail>>() {});
    }

    /** POST /inboxes/{inboxId}/quarantine/{emailId}/release */
    public QuarantineRelease release(String inboxId, String emailId) throws MillionSendException {
      return release(inboxId, emailId, false);
    }

    /**
     * POST /inboxes/{inboxId}/quarantine/{emailId}/release. {@code trustSender} also adds
     * the sender's address to the inbox's allowlist.
     */
    public QuarantineRelease release(String inboxId, String emailId, boolean trustSender)
        throws MillionSendException {
      return http.request(
          "POST", inbox(inboxId) + "/quarantine/" + enc(emailId) + "/release",
          trustSender ? Collections.singletonMap("trust_sender", true) : Collections.emptyMap(),
          null, null, new TypeReference<QuarantineRelease>() {});
    }
  }

  /** An inbox's labels: at most 100, names unique ignoring case. */
  public static final class Labels {

    private final HttpClient http;

    Labels(HttpClient http) {
      this.http = http;
    }

    /** GET /inboxes/{inboxId}/labels — oldest first, not paginated. */
    public ListResponse<InboxLabel> list(String inboxId) throws MillionSendException {
      return http.request(
          "GET", inbox(inboxId) + "/labels", null, null, null,
          new TypeReference<ListResponse<InboxLabel>>() {});
    }

    /** POST /inboxes/{inboxId}/labels */
    public InboxLabel create(String inboxId, InboxLabelOptions options)
        throws MillionSendException {
      return http.request(
          "POST", inbox(inboxId) + "/labels", options, null, null,
          new TypeReference<InboxLabel>() {});
    }

    /** PATCH /inboxes/{inboxId}/labels/{labelId} */
    public InboxLabel update(String inboxId, String labelId, InboxLabelOptions options)
        throws MillionSendException {
      return http.request(
          "PATCH", inbox(inboxId) + "/labels/" + enc(labelId), options, null, null,
          new TypeReference<InboxLabel>() {});
    }

    /** DELETE /inboxes/{inboxId}/labels/{labelId} — also takes it off every thread. */
    public DeletedResponse remove(String inboxId, String labelId) throws MillionSendException {
      return http.request(
          "DELETE", inbox(inboxId) + "/labels/" + enc(labelId), null, null, null,
          new TypeReference<DeletedResponse>() {});
    }
  }

  /** An inbox's threads. Their emails live under {@link #emails()}. */
  public static final class Threads {

    private final HttpClient http;
    private final ThreadEmails emails;

    Threads(HttpClient http) {
      this.http = http;
      this.emails = new ThreadEmails(http);
    }

    public ThreadEmails emails() {
      return emails;
    }

    /** GET /inboxes/{inboxId}/threads — most recently active first, the {@code inbox} folder. */
    public ListResponse<InboxThread> list(String inboxId) throws MillionSendException {
      return list(inboxId, null);
    }

    /** GET /inboxes/{inboxId}/threads with folder, label and read filters. */
    public ListResponse<InboxThread> list(String inboxId, ListThreadsOptions options)
        throws MillionSendException {
      return http.request(
          "GET", inbox(inboxId) + "/threads", null, options == null ? null : options.toQuery(),
          null, new TypeReference<ListResponse<InboxThread>>() {});
    }

    /** GET /inboxes/{inboxId}/threads/search */
    public ListResponse<InboxThread> search(String inboxId, SearchThreadsOptions options)
        throws MillionSendException {
      return http.request(
          "GET", inbox(inboxId) + "/threads/search", null,
          options == null ? null : options.toQuery(), null,
          new TypeReference<ListResponse<InboxThread>>() {});
    }

    /** GET /inboxes/{inboxId}/threads/{threadId} — the summary, without messages. */
    public InboxThread get(String inboxId, String threadId) throws MillionSendException {
      return http.request(
          "GET", thread(inboxId, threadId), null, null, null, new TypeReference<InboxThread>() {});
    }

    /** PATCH /inboxes/{inboxId}/threads/{threadId} — read state, folder or a label. */
    public InboxThread update(String inboxId, String threadId, UpdateThreadOptions options)
        throws MillionSendException {
      return http.request(
          "PATCH", thread(inboxId, threadId), options, null, null,
          new TypeReference<InboxThread>() {});
    }

    /**
     * DELETE /inboxes/{inboxId}/threads/{threadId} — moves it to trash, purged after 30
     * days; {@code deleted} is false when it was already there.
     */
    public DeletedResponse remove(String inboxId, String threadId) throws MillionSendException {
      return http.request(
          "DELETE", thread(inboxId, threadId), null, null, null,
          new TypeReference<DeletedResponse>() {});
    }
  }

  /** The emails of one thread, both directions. */
  public static final class ThreadEmails {

    private static final TypeReference<ThreadEmail> SENT = new TypeReference<ThreadEmail>() {};

    private final HttpClient http;

    ThreadEmails(HttpClient http) {
      this.http = http;
    }

    /** GET /inboxes/{inboxId}/threads/{threadId}/emails — oldest first; {@code after} pages to newer. */
    public ListResponse<ThreadEmail> list(String inboxId, String threadId)
        throws MillionSendException {
      return list(inboxId, threadId, null);
    }

    /** GET /inboxes/{inboxId}/threads/{threadId}/emails with pagination. */
    public ListResponse<ThreadEmail> list(String inboxId, String threadId, ListOptions options)
        throws MillionSendException {
      return http.request(
          "GET", thread(inboxId, threadId) + "/emails", null,
          options == null ? null : options.toQuery(), null,
          new TypeReference<ListResponse<ThreadEmail>>() {});
    }

    /** GET /inboxes/{inboxId}/threads/{threadId}/emails/{emailId} */
    public ThreadEmail get(String inboxId, String threadId, String emailId)
        throws MillionSendException {
      return http.request(
          "GET", email(inboxId, threadId, emailId), null, null, null,
          new TypeReference<ThreadEmail>() {});
    }

    /** POST …/emails/{emailId}/reply */
    public SendResult<ThreadEmail> reply(
        String inboxId, String threadId, String emailId, ReplyOptions options)
        throws MillionSendException {
      return reply(inboxId, threadId, emailId, options, null);
    }

    /**
     * POST …/emails/{emailId}/reply with an {@code Idempotency-Key}. When the inbox's send
     * rules hold the reply, the result is a draft waiting for approval.
     */
    public SendResult<ThreadEmail> reply(
        String inboxId, String threadId, String emailId, ReplyOptions options,
        RequestOptions requestOptions)
        throws MillionSendException {
      return http.sendOrHold(
          email(inboxId, threadId, emailId) + "/reply", options, requestOptions, SENT);
    }

    /** POST …/emails/{emailId}/forward */
    public SendResult<ThreadEmail> forward(
        String inboxId, String threadId, String emailId, ForwardOptions options)
        throws MillionSendException {
      return forward(inboxId, threadId, emailId, options, null);
    }

    /** POST …/emails/{emailId}/forward with an {@code Idempotency-Key}. */
    public SendResult<ThreadEmail> forward(
        String inboxId, String threadId, String emailId, ForwardOptions options,
        RequestOptions requestOptions)
        throws MillionSendException {
      return http.sendOrHold(
          email(inboxId, threadId, emailId) + "/forward", options, requestOptions, SENT);
    }

    private static String email(String inboxId, String threadId, String emailId) {
      return thread(inboxId, threadId) + "/emails/" + enc(emailId);
    }
  }

  /** An inbox's drafts, including the ones the send rules hold for approval. */
  public static final class Drafts {

    private final HttpClient http;

    Drafts(HttpClient http) {
      this.http = http;
    }

    /** GET /inboxes/{inboxId}/drafts — unsent drafts, most recently updated first. */
    public ListResponse<InboxDraft> list(String inboxId) throws MillionSendException {
      return list(inboxId, null);
    }

    /** GET /inboxes/{inboxId}/drafts with pagination. */
    public ListResponse<InboxDraft> list(String inboxId, ListOptions options)
        throws MillionSendException {
      return http.request(
          "GET", inbox(inboxId) + "/drafts", null, options == null ? null : options.toQuery(),
          null, new TypeReference<ListResponse<InboxDraft>>() {});
    }

    /** POST /inboxes/{inboxId}/drafts — the existing one when a reply draft already targets that email. */
    public InboxDraft create(String inboxId, CreateDraftOptions options)
        throws MillionSendException {
      return http.request(
          "POST", inbox(inboxId) + "/drafts", options, null, null,
          new TypeReference<InboxDraft>() {});
    }

    /** GET /inboxes/{inboxId}/drafts/{draftId} — also after it was sent. */
    public InboxDraft get(String inboxId, String draftId) throws MillionSendException {
      return http.request(
          "GET", draft(inboxId, draftId), null, null, null, new TypeReference<InboxDraft>() {});
    }

    /** PATCH /inboxes/{inboxId}/drafts/{draftId} — 409 once sent or when it changed meanwhile. */
    public InboxDraft update(String inboxId, String draftId, UpdateDraftOptions options)
        throws MillionSendException {
      return http.request(
          "PATCH", draft(inboxId, draftId), options, null, null,
          new TypeReference<InboxDraft>() {});
    }

    /** DELETE /inboxes/{inboxId}/drafts/{draftId} */
    public DeletedResponse remove(String inboxId, String draftId) throws MillionSendException {
      return http.request(
          "DELETE", draft(inboxId, draftId), null, null, null,
          new TypeReference<DeletedResponse>() {});
    }

    /** POST /inboxes/{inboxId}/drafts/{draftId}/send */
    public SendResult<InboxDraft> send(String inboxId, String draftId)
        throws MillionSendException {
      return send(inboxId, draftId, null);
    }

    /**
     * POST /inboxes/{inboxId}/drafts/{draftId}/send with an {@code Idempotency-Key}. Sent,
     * the result carries the draft's {@code emailId} and {@code threadId}; the send rules
     * may hold it for approval instead. An inbox-scoped key gets 403
     * {@code approval_required} on a pending draft.
     */
    public SendResult<InboxDraft> send(String inboxId, String draftId, RequestOptions requestOptions)
        throws MillionSendException {
      return http.sendOrHold(
          draft(inboxId, draftId) + "/send", null, requestOptions,
          new TypeReference<InboxDraft>() {});
    }

    /** POST /inboxes/{inboxId}/drafts/{draftId}/reject — approvers only (MillionSend extension). */
    public InboxDraft reject(String inboxId, String draftId) throws MillionSendException {
      return http.request(
          "POST", draft(inboxId, draftId) + "/reject", null, null, null,
          new TypeReference<InboxDraft>() {});
    }

    private static String draft(String inboxId, String draftId) {
      return inbox(inboxId) + "/drafts/" + enc(draftId);
    }
  }

  private static String thread(String inboxId, String threadId) {
    return inbox(inboxId) + "/threads/" + enc(threadId);
  }
}
