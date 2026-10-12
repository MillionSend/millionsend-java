package com.millionsend.model;

/**
 * The outcome of an inbox send (reply, forward, draft send): the sent result, or
 * the {@link InboxDraft} the inbox's send rules held for a person to approve (the
 * API answers 202 and nothing is sent).
 */
public final class SendResult<T> {

  private final T sent;
  private final InboxDraft draft;
  private final boolean held;

  private SendResult(T sent, InboxDraft draft, boolean held) {
    this.sent = sent;
    this.draft = draft;
    this.held = held;
  }

  public static <T> SendResult<T> sent(T sent) {
    return new SendResult<>(sent, null, false);
  }

  public static <T> SendResult<T> held(InboxDraft draft) {
    return new SendResult<>(null, draft, true);
  }

  /** True when the send rules held the message as a draft ({@code status: pending_approval}). */
  public boolean isDraft() {
    return held;
  }

  /** {@code sent} or {@code draft}. */
  public String getKind() {
    return held ? "draft" : "sent";
  }

  /** The sent result; {@code null} when {@link #isDraft()}. */
  public T getSent() {
    return sent;
  }

  /** The held draft; {@code null} when the message was sent. */
  public InboxDraft getDraft() {
    return draft;
  }
}
