package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Options for {@code inboxes().threads().update(...)}: set at least one. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class UpdateThreadOptions {

  private Boolean read;
  private String folder;
  private String labelId;

  private UpdateThreadOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final UpdateThreadOptions o = new UpdateThreadOptions();

    /** Marks every message in the thread. */
    public Builder read(boolean read) {
      o.read = read;
      return this;
    }

    /** {@code inbox}, {@code archive}, {@code spam} or {@code trash}; out of trash restores it. */
    public Builder folder(String folder) {
      o.folder = folder;
      return this;
    }

    /** Applies this label to the thread. */
    public Builder labelId(String labelId) {
      o.labelId = labelId;
      return this;
    }

    public UpdateThreadOptions build() {
      return o;
    }
  }
}
