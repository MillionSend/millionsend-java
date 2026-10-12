package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Options for {@code inboxes().drafts().create(...)}. A reply draft sets both
 * {@code threadId} and {@code replyToEmailId}; it does not copy recipients.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateDraftOptions {

  private List<String> to;
  private List<String> cc;
  private List<String> bcc;
  private String subject;
  private String html;
  private String text;
  private String threadId;
  private String replyToEmailId;

  private CreateDraftOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final CreateDraftOptions o = new CreateDraftOptions();

    public Builder to(String... to) {
      o.to = new ArrayList<>(Arrays.asList(to));
      return this;
    }

    public Builder to(List<String> to) {
      o.to = to;
      return this;
    }

    public Builder cc(String... cc) {
      o.cc = new ArrayList<>(Arrays.asList(cc));
      return this;
    }

    public Builder cc(List<String> cc) {
      o.cc = cc;
      return this;
    }

    public Builder bcc(String... bcc) {
      o.bcc = new ArrayList<>(Arrays.asList(bcc));
      return this;
    }

    public Builder bcc(List<String> bcc) {
      o.bcc = bcc;
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

    public Builder threadId(String threadId) {
      o.threadId = threadId;
      return this;
    }

    public Builder replyToEmailId(String replyToEmailId) {
      o.replyToEmailId = replyToEmailId;
      return this;
    }

    public CreateDraftOptions build() {
      return o;
    }
  }
}
