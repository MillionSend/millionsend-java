package com.millionsend.model;

/** A matching email a long-poll found held (quarantined or being screened): its id only. */
public final class HeldEmail {

  private String id;
  private String inboxId;
  private String reason;
  private String createdAt;

  public String getId() {
    return id;
  }

  public String getInboxId() {
    return inboxId;
  }

  public String getReason() {
    return reason;
  }

  public String getCreatedAt() {
    return createdAt;
  }
}
