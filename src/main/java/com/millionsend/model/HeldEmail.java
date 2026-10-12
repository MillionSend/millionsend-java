package com.millionsend.model;

/**
 * A matching email a long-poll found held: its id and why only. The reason is {@code pending}
 * (still being screened) or {@code quarantined}, and the quarantine reason says why (null while
 * pending). The inbox id is null for a catch-all copy held for a virus.
 */
public final class HeldEmail {

  private String id;
  private String inboxId;
  private String reason;
  private String quarantineReason;
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

  public String getQuarantineReason() {
    return quarantineReason;
  }

  public String getCreatedAt() {
    return createdAt;
  }
}
