package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Options for {@code inboxes().labels().create(...)} and {@code update(...)}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class InboxLabelOptions {

  private String name;
  private String color;

  private InboxLabelOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final InboxLabelOptions o = new InboxLabelOptions();

    /** 1-64 characters, unique per inbox ignoring case. */
    public Builder name(String name) {
      o.name = name;
      return this;
    }

    /** {@code #RRGGBB}; a random color when omitted on create. */
    public Builder color(String color) {
      o.color = color;
      return this;
    }

    public InboxLabelOptions build() {
      return o;
    }
  }
}
