package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Body of a forward, of a received email or of an email in an inbox thread.
 * {@code html}/{@code text} is an optional note above the quoted original.
 * {@code from}, {@code inboxId} and {@code mode} apply to {@code receiving().forward(...)} only.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ForwardOptions {

  private List<String> to;
  private List<String> cc;
  private List<String> bcc;
  private String html;
  private String text;
  private String subject;
  private Boolean requiresApproval;
  private String from;
  private String inboxId;
  private String mode;

  private ForwardOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final ForwardOptions o = new ForwardOptions();

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

    public Builder html(String html) {
      o.html = html;
      return this;
    }

    public Builder text(String text) {
      o.text = text;
      return this;
    }

    /** Defaults to {@code Fwd: } plus the original subject. */
    public Builder subject(String subject) {
      o.subject = subject;
      return this;
    }

    /** Hold the forward as a draft for a person to approve. */
    public Builder requiresApproval(boolean requiresApproval) {
      o.requiresApproval = requiresApproval;
      return this;
    }

    /** An address on a verified team domain; defaults to the inbox address. */
    public Builder from(String from) {
      o.from = from;
      return this;
    }

    /** Which of the email's inboxes forwards, when it reached several. */
    public Builder inboxId(String inboxId) {
      o.inboxId = inboxId;
      return this;
    }

    /**
     * {@code inline} (the default) quotes the original; {@code attachment} attaches it
     * as {@code forwarded_message.eml}.
     */
    public Builder mode(String mode) {
      o.mode = mode;
      return this;
    }

    public ForwardOptions build() {
      return o;
    }
  }
}
