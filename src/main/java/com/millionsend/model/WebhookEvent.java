package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** An event type a webhook can subscribe to. */
public enum WebhookEvent {
  /** Accepted with a future {@code scheduledAt}. MillionSend v0.15.1 and later. */
  EMAIL_SCHEDULED("email.scheduled"),
  EMAIL_SENT("email.sent"),
  EMAIL_DELIVERED("email.delivered"),
  EMAIL_DELIVERY_DELAYED("email.delivery_delayed"),
  EMAIL_BOUNCED("email.bounced"),
  EMAIL_COMPLAINED("email.complained"),
  /** Failed for good; {@code data.failed.reason} says why. MillionSend v0.15.1 and later. */
  EMAIL_FAILED("email.failed"),
  /** Won't be sent; {@code data.suppressed} says why. MillionSend v0.15.1 and later. */
  EMAIL_SUPPRESSED("email.suppressed"),
  EMAIL_OPENED("email.opened"),
  EMAIL_CLICKED("email.clicked"),
  /**
   * MillionSend extension, opt-in: delivered only to endpoints that name it. A machine fetched
   * the pixel or followed a link; {@code data.open.reason} or {@code data.click.reason} says which.
   */
  EMAIL_PREFETCHED("email.prefetched"),
  /** Mail received at a receiving domain or inbox. */
  EMAIL_RECEIVED("email.received"),
  /** MillionSend extension. */
  DELIVERABILITY_WARNING("deliverability.warning"),
  /** MillionSend extension. */
  DELIVERABILITY_PAUSED("deliverability.paused"),
  /** MillionSend extension. */
  QUOTA_WARNING("quota.warning"),
  /** MillionSend extension. */
  QUOTA_REACHED("quota.reached"),
  /** MillionSend extension: sends are parked at the quota ceiling until the reset or an upgrade. */
  QUOTA_PAUSED("quota.paused"),
  CONTACT_CREATED("contact.created"),
  CONTACT_UPDATED("contact.updated"),
  CONTACT_DELETED("contact.deleted"),
  /** MillionSend extension. */
  CONTACT_UNSUBSCRIBED("contact.unsubscribed"),
  /** MillionSend extension. */
  CONTACT_RESUBSCRIBED("contact.resubscribed"),
  /** MillionSend extension. */
  CONTACT_TOPIC_OPT_IN("contact.topic_opt_in"),
  /** MillionSend extension. */
  CONTACT_TOPIC_OPT_OUT("contact.topic_opt_out"),
  SUPPRESSION_ADDED("suppression.added"),
  SUPPRESSION_REMOVED("suppression.removed"),
  // Inbox events reach only endpoints that name them; subscribing to all events skips them.
  INBOX_CREATED("inbox.created"),
  INBOX_UPDATED("inbox.updated"),
  INBOX_DELETED("inbox.deleted"),
  /** MillionSend extension. */
  INBOX_EXPIRED("inbox.expired"),
  INBOX_THREAD_CREATED("inbox.thread.created"),
  INBOX_THREAD_FOLDER_UPDATED("inbox.thread.folder.updated"),
  INBOX_THREAD_LABELS_UPDATED("inbox.thread.labels.updated"),
  INBOX_EMAIL_RECEIVED("inbox.email.received"),
  INBOX_EMAIL_SENT("inbox.email.sent"),
  /** MillionSend extension. */
  INBOX_EMAIL_QUARANTINED("inbox.email.quarantined"),
  INBOX_DRAFT_CREATED("inbox.draft.created"),
  INBOX_DRAFT_UPDATED("inbox.draft.updated"),
  INBOX_DRAFT_SENT("inbox.draft.sent"),
  INBOX_DRAFT_DELETED("inbox.draft.deleted");

  private final String value;

  WebhookEvent(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * The server may emit event types this build does not know yet; a
   * subscription to one reads as null instead of failing the whole response.
   */
  @JsonCreator
  public static WebhookEvent fromValue(String value) {
    for (WebhookEvent e : values()) {
      if (e.value.equals(value)) {
        return e;
      }
    }
    return null;
  }
}
