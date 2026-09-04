package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonValue;

/** An event type a webhook can subscribe to. */
public enum WebhookEvent {
  EMAIL_SENT("email.sent"),
  EMAIL_DELIVERED("email.delivered"),
  EMAIL_DELIVERY_DELAYED("email.delivery_delayed"),
  EMAIL_BOUNCED("email.bounced"),
  EMAIL_COMPLAINED("email.complained"),
  EMAIL_OPENED("email.opened"),
  EMAIL_CLICKED("email.clicked"),
  /** MillionSend extension. */
  DELIVERABILITY_WARNING("deliverability.warning"),
  /** MillionSend extension. */
  DELIVERABILITY_PAUSED("deliverability.paused"),
  /** MillionSend extension. */
  QUOTA_WARNING("quota.warning"),
  /** MillionSend extension. */
  QUOTA_REACHED("quota.reached");

  private final String value;

  WebhookEvent(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
