package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Options for {@code inboxes().update(...)}. Only the fields you set are sent;
 * {@code expiresIn(null)} makes the inbox never expire.
 */
public final class UpdateInboxOptions {

  private final Map<String, Object> changes = new LinkedHashMap<>();

  private UpdateInboxOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  /** The snake_case body to PATCH — only the fields the caller set. */
  @JsonValue
  public Map<String, Object> getChanges() {
    return changes;
  }

  public static final class Builder {
    private final UpdateInboxOptions o = new UpdateInboxOptions();

    public Builder name(String name) {
      o.changes.put("name", name);
      return this;
    }

    /** {@code ""} removes the display name. */
    public Builder fromName(String fromName) {
      o.changes.put("from_name", fromName);
      return this;
    }

    /** Like {@code 1h} or {@code 30d}, counted from now; {@code null} for never. */
    public Builder expiresIn(String expiresIn) {
      o.changes.put("expires_in", expiresIn);
      return this;
    }

    public UpdateInboxOptions build() {
      return o;
    }
  }
}
