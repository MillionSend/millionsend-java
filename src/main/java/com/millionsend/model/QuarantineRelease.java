package com.millionsend.model;

/** The result of {@code inboxes().quarantine().release(...)}. */
public final class QuarantineRelease {

  private String object;
  private String inboxId;
  private String emailId;
  private String releasedAt;
  private String trustedSender;

  public String getObject() {
    return object;
  }

  public String getInboxId() {
    return inboxId;
  }

  public String getEmailId() {
    return emailId;
  }

  public String getReleasedAt() {
    return releasedAt;
  }

  /** The sender address added to the allowlist; null unless {@code trustSender} was set. */
  public String getTrustedSender() {
    return trustedSender;
  }
}
