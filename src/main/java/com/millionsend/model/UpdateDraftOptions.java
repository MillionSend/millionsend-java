package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Options for {@code inboxes().drafts().update(...)}. Only the fields you set are
 * sent; {@code null} clears a field. Setting {@code html} or {@code text} replaces
 * the whole body: the part you leave out becomes null.
 */
public final class UpdateDraftOptions {

  private final Map<String, Object> changes = new LinkedHashMap<>();

  private UpdateDraftOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  /** The snake_case body to PATCH — only the fields the caller set. */
  @JsonValue
  public Map<String, Object> getChanges() {
    return changes;
  }

  public static final class Builder {
    private final UpdateDraftOptions o = new UpdateDraftOptions();

    public Builder to(List<String> to) {
      o.changes.put("to", to);
      return this;
    }

    public Builder cc(List<String> cc) {
      o.changes.put("cc", cc);
      return this;
    }

    public Builder bcc(List<String> bcc) {
      o.changes.put("bcc", bcc);
      return this;
    }

    public Builder subject(String subject) {
      o.changes.put("subject", subject);
      return this;
    }

    public Builder html(String html) {
      o.changes.put("html", html);
      return this;
    }

    public Builder text(String text) {
      o.changes.put("text", text);
      return this;
    }

    public UpdateDraftOptions build() {
      return o;
    }
  }
}
