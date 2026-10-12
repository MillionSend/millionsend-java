package com.millionsend.model;

/**
 * An inbox: one address that receives mail, read by an agent or by people. List
 * items carry {@code id}, {@code name}, {@code emailAddress}, {@code fromName},
 * {@code unread}, {@code lastReceived}, {@code kind}, {@code shared} and
 * {@code expiresAt}; the other fields are null there.
 */
public final class Inbox {

  private String object;
  private String id;
  private String name;
  private String emailAddress;
  private String domainId;
  private String receivingAddress;
  private String fromName;
  private Integer unread;
  private Integer drafts;
  private String lastReceived;
  private String createdAt;
  private String kind;
  private Boolean shared;
  private String expiresAt;
  private Integer quarantined;
  private Integer pendingApproval;

  public String getObject() {
    return object;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  /** The custom domain's id, or for a shared-domain inbox the team's shared subdomain id. */
  public String getDomainId() {
    return domainId;
  }

  /** Set for {@code forwarding} inboxes: the address to forward mail to. */
  public String getReceivingAddress() {
    return receivingAddress;
  }

  public String getFromName() {
    return fromName;
  }

  /** Threads with unread mail. */
  public Integer getUnread() {
    return unread;
  }

  /** Unsent drafts. */
  public Integer getDrafts() {
    return drafts;
  }

  public String getLastReceived() {
    return lastReceived;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  /** {@code agent} or {@code human}. */
  public String getKind() {
    return kind;
  }

  /** True for an address on the shared domain (no DNS of your own). */
  public Boolean getShared() {
    return shared;
  }

  /** When the inbox stops receiving and is deleted; null for never. */
  public String getExpiresAt() {
    return expiresAt;
  }

  /** Emails held in quarantine, not yet released. */
  public Integer getQuarantined() {
    return quarantined;
  }

  /** Drafts waiting for a person to approve them. */
  public Integer getPendingApproval() {
    return pendingApproval;
  }
}
