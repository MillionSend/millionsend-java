package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Options for {@code emails().send(...)}. camelCase fields map to the snake_case
 * wire ({@code replyTo} → {@code reply_to}, {@code scheduledAt} → {@code scheduled_at});
 * unset fields are omitted.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateEmailOptions {

  private String from;
  private List<String> to;
  private String subject;
  private String html;
  private String text;
  private List<String> cc;
  private List<String> bcc;
  private List<String> replyTo;
  private String scheduledAt;
  private List<Tag> tags;

  private CreateEmailOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final CreateEmailOptions o = new CreateEmailOptions();

    public Builder from(String from) {
      o.from = from;
      return this;
    }

    public Builder to(String... to) {
      o.to = new ArrayList<>(Arrays.asList(to));
      return this;
    }

    public Builder to(List<String> to) {
      o.to = to;
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

    public Builder replyTo(String... replyTo) {
      o.replyTo = new ArrayList<>(Arrays.asList(replyTo));
      return this;
    }

    public Builder replyTo(List<String> replyTo) {
      o.replyTo = replyTo;
      return this;
    }

    public Builder scheduledAt(String scheduledAt) {
      o.scheduledAt = scheduledAt;
      return this;
    }

    public Builder tags(List<Tag> tags) {
      o.tags = tags;
      return this;
    }

    public Builder tag(Tag tag) {
      if (o.tags == null) {
        o.tags = new ArrayList<>();
      }
      o.tags.add(tag);
      return this;
    }

    public CreateEmailOptions build() {
      return o;
    }
  }
}
