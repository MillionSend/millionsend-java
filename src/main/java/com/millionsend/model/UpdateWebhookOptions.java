package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Options for {@code webhooks().update(...)}. Only the fields you set are sent;
 * {@code inboxIds(null)} removes the inbox filter.
 */
public final class UpdateWebhookOptions {

  private final Map<String, Object> changes = new LinkedHashMap<>();

  private UpdateWebhookOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  /** The snake_case body to PATCH — only the fields the caller set. */
  @JsonValue
  public Map<String, Object> getChanges() {
    return changes;
  }

  public static final class Builder {
    private final UpdateWebhookOptions o = new UpdateWebhookOptions();

    public Builder endpoint(String endpoint) {
      o.changes.put("endpoint", endpoint);
      return this;
    }

    public Builder events(WebhookEvent... events) {
      return events(new ArrayList<>(Arrays.asList(events)));
    }

    public Builder events(List<WebhookEvent> events) {
      o.changes.put("events", events);
      return this;
    }

    /** {@code enabled} or {@code disabled}. */
    public Builder status(String status) {
      o.changes.put("status", status);
      return this;
    }

    /** Deliver inbox events and {@code email.received} for these inboxes only; {@code null} for every inbox. */
    public Builder inboxIds(List<String> inboxIds) {
      o.changes.put("inbox_ids", inboxIds);
      return this;
    }

    public UpdateWebhookOptions build() {
      return o;
    }
  }
}
