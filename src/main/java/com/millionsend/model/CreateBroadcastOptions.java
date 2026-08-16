package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Options for {@code broadcasts().create(...)}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateBroadcastOptions {

  private String name;
  private String audienceId;
  private String segmentId;
  private String from;
  private String subject;
  private String html;
  private String text;
  private List<String> replyTo;
  private String topicId;

  private CreateBroadcastOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final CreateBroadcastOptions o = new CreateBroadcastOptions();

    public Builder name(String name) {
      o.name = name;
      return this;
    }

    public Builder audienceId(String audienceId) {
      o.audienceId = audienceId;
      return this;
    }

    public Builder segmentId(String segmentId) {
      o.segmentId = segmentId;
      return this;
    }

    public Builder from(String from) {
      o.from = from;
      return this;
    }

    public Builder subject(String subject) {
      o.subject = subject;
      return this;
    }

    public Builder html(String html) {
      o.html = html;
      return this;
    }

    public Builder text(String text) {
      o.text = text;
      return this;
    }

    public Builder replyTo(String... replyTo) {
      o.replyTo = new ArrayList<>(Arrays.asList(replyTo));
      return this;
    }

    public Builder replyTo(List<String> replyTo) {
      o.replyTo = replyTo;
      return this;
    }

    public Builder topicId(String topicId) {
      o.topicId = topicId;
      return this;
    }

    public CreateBroadcastOptions build() {
      return o;
    }
  }
}
