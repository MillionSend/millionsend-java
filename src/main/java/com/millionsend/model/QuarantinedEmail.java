package com.millionsend.model;

/** An email held in an inbox's quarantine ({@code inboxes().quarantine().list(...)}). */
public final class QuarantinedEmail {

  private String id;
  private String threadId;
  private String from;
  private String subject;
  private String receivedAt;
  private String quarantineReason;
  private String senderTrust;
  private Integer injectionScore;
  private Integer phishingScore;
  private String virusSignature;

  public String getId() {
    return id;
  }

  public String getThreadId() {
    return threadId;
  }

  public String getFrom() {
    return from;
  }

  public String getSubject() {
    return subject;
  }

  public String getReceivedAt() {
    return receivedAt;
  }

  /** e.g. {@code failed_auth}, {@code not_allowlisted}, {@code injection}, {@code virus}. */
  public String getQuarantineReason() {
    return quarantineReason;
  }

  public String getSenderTrust() {
    return senderTrust;
  }

  public Integer getInjectionScore() {
    return injectionScore;
  }

  public Integer getPhishingScore() {
    return phishingScore;
  }

  public String getVirusSignature() {
    return virusSignature;
  }
}
