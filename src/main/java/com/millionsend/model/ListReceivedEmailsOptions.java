package com.millionsend.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pagination and filters for {@code receiving().list(...)}. {@code waitSeconds} (0-50,
 * the wire's {@code wait}) long-polls: the API answers as soon as a matching email
 * arrives, or with an empty page when the wait ends.
 */
public final class ListReceivedEmailsOptions {

  private Integer limit;
  private String after;
  private String before;
  private Integer wait;
  private String since;
  private String inboxId;
  private String to;
  private String from;
  private String subjectContains;
  private Boolean unread;
  private boolean includeQuarantined;
  private boolean quarantined;

  private ListReceivedEmailsOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public Integer getWaitSeconds() {
    return wait;
  }

  /** Flatten into the wire query map, dropping unset params. */
  public Map<String, String> toQuery() {
    Map<String, String> q = new LinkedHashMap<>();
    Query.put(q, "limit", limit);
    Query.put(q, "after", after);
    Query.put(q, "before", before);
    Query.put(q, "wait", wait);
    Query.put(q, "since", since);
    Query.put(q, "inbox_id", inboxId);
    Query.put(q, "to", to);
    Query.put(q, "from", from);
    Query.put(q, "subject_contains", subjectContains);
    Query.put(q, "unread", unread);
    if (includeQuarantined) {
      q.put("include_quarantined", "true");
    }
    if (quarantined) {
      q.put("quarantined", "true");
    }
    return q;
  }

  public static final class Builder {
    private final ListReceivedEmailsOptions o = new ListReceivedEmailsOptions();

    public Builder limit(int limit) {
      o.limit = limit;
      return this;
    }

    public Builder after(String after) {
      o.after = after;
      return this;
    }

    /** Toward newer mail; with {@code waitSeconds}, waits for mail newer than this id. */
    public Builder before(String before) {
      o.before = before;
      return this;
    }

    /** Seconds to hold the request open, 0-50. */
    public Builder waitSeconds(int seconds) {
      o.wait = seconds;
      return this;
    }

    /** ISO 8601 with an offset, like {@code Instant.toString()}: only mail received after it. */
    public Builder since(String since) {
      o.since = since;
      return this;
    }

    /** An inbox that doesn't exist answers 404 {@code not_found} at once, even with a wait. */
    public Builder inboxId(String inboxId) {
      o.inboxId = inboxId;
      return this;
    }

    /** An address the message was delivered to ({@code receivedFor}). */
    public Builder to(String to) {
      o.to = to;
      return this;
    }

    /** A sender address, or a domain that also covers its subdomains. */
    public Builder from(String from) {
      o.from = from;
      return this;
    }

    public Builder subjectContains(String subjectContains) {
      o.subjectContains = subjectContains;
      return this;
    }

    /** Needs {@link #inboxId(String)}, except with an inbox-scoped key (its own inbox). */
    public Builder unread(boolean unread) {
      o.unread = unread;
      return this;
    }

    /** Also list held mail; needs a full access key that is not scoped to an inbox. */
    public Builder includeQuarantined(boolean includeQuarantined) {
      o.includeQuarantined = includeQuarantined;
      return this;
    }

    /** List held mail only; needs a full access key that is not scoped to an inbox. */
    public Builder quarantined(boolean quarantined) {
      o.quarantined = quarantined;
      return this;
    }

    public ListReceivedEmailsOptions build() {
      return o;
    }
  }
}
