package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Body of a reply, to a received email or to an email in an inbox thread. Set
 * {@code html} and/or {@code text}. {@code from} and {@code inboxId} apply to
 * {@code receiving().reply(...)} only.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ReplyOptions {

  private String html;
  private String text;
  private String subject;
  private List<String> cc;
  private List<String> bcc;
  private Boolean replyAll;
  private Boolean requiresApproval;
  private String from;
  private String inboxId;

  private ReplyOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final ReplyOptions o = new ReplyOptions();

    public Builder html(String html) {
      o.html = html;
      return this;
    }

    public Builder text(String text) {
      o.text = text;
      return this;
    }

    /** Defaults to {@code Re: } plus the thread subject. */
    public Builder subject(String subject) {
      o.subject = subject;
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

    /** Also copy the message's other recipients. */
    public Builder replyAll(boolean replyAll) {
      o.replyAll = replyAll;
      return this;
    }

    /** Hold the reply as a draft for a person to approve. */
    public Builder requiresApproval(boolean requiresApproval) {
      o.requiresApproval = requiresApproval;
      return this;
    }

    /** An address on a verified team domain; defaults to the inbox address. */
    public Builder from(String from) {
      o.from = from;
      return this;
    }

    /** Which of the email's inboxes replies, when it reached several. */
    public Builder inboxId(String inboxId) {
      o.inboxId = inboxId;
      return this;
    }

    public ReplyOptions build() {
      return o;
    }
  }
}
