package com.millionsend.model;

import java.util.List;
import java.util.Map;

/**
 * A sending domain. {@code records} (the DNS records to publish) is only
 * populated when a single domain is fetched, created or verified.
 *
 * <p>On an instance with a shared inbox domain, {@code domains().list()} ends with the
 * team's subdomain of it: {@link #isShared()} is true, it is verified, has no DNS records
 * and is read-only, and its id is the {@code domainId} of the team's shared inboxes.
 */
public final class Domain {

  private String object;
  private String id;
  private String name;
  private String status;
  private String createdAt;
  private String region;
  private boolean openTracking;
  private boolean clickTracking;
  private String trackingSubdomain;
  private Map<String, String> capabilities;
  private Boolean receivingCatchAll;
  private boolean shared;
  private String sendingMode;
  private String sendingModeReason;
  private List<DomainRecord> records;

  public String getObject() {
    return object;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  /** {@code pending}, {@code verified}, {@code failed}, … */
  public String getStatus() {
    return status;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getRegion() {
    return region;
  }

  public boolean isOpenTracking() {
    return openTracking;
  }

  public boolean isClickTracking() {
    return clickTracking;
  }

  public String getTrackingSubdomain() {
    return trackingSubdomain;
  }

  /**
   * {@code sending} / {@code receiving} → {@code enabled} / {@code disabled}. The shared row's
   * sending is {@code disabled} under {@code receive_only}.
   */
  public Map<String, String> getCapabilities() {
    return capabilities;
  }

  /**
   * On, mail to addresses without an inbox lands in the received list; off, those
   * addresses are refused at SMTP time.
   */
  public Boolean getReceivingCatchAll() {
    return receivingCatchAll;
  }

  /**
   * True for the team's read-only subdomain of the shared inbox domain, which sends only
   * from its inboxes' own addresses: leave it out when picking a domain to send from.
   * False on the team's own domains, also when a server before v0.14.0 sends no {@code shared}.
   */
  public boolean isShared() {
    return shared;
  }

  /**
   * Shared row only, else {@code null}: what its inboxes may send. {@code open} starts
   * conversations, {@code reply_only} replies inside threads, {@code receive_only} sends
   * nothing. A plain string, so new values never break decoding.
   */
  public String getSendingMode() {
    return sendingMode;
  }

  /**
   * Shared row only: what holds {@link #getSendingMode()} below {@code open}, {@code plan}
   * or {@code instance}; {@code null} under {@code open} and on the team's own domains.
   */
  public String getSendingModeReason() {
    return sendingModeReason;
  }

  public List<DomainRecord> getRecords() {
    return records;
  }
}
