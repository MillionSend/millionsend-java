package com.millionsend.model;

/**
 * The newest verification code or sign-in link an inbox received
 * ({@code inboxes().codes().latest(...)}). The link is never fetched.
 */
public final class VerificationCode {

  private String object;
  private String status;
  private String kind;
  private String code;
  private String url;
  private Boolean linkDomainMatchesSender;
  private String emailId;
  private String from;
  private String receivedAt;
  private String senderTrust;

  public String getObject() {
    return object;
  }

  /** {@code found}, {@code none}, {@code ambiguous} or {@code timeout} (a wait that ended empty). */
  public String getStatus() {
    return status;
  }

  /** {@code otp} or {@code link}; null unless found. */
  public String getKind() {
    return kind;
  }

  public String getCode() {
    return code;
  }

  public String getUrl() {
    return url;
  }

  public Boolean getLinkDomainMatchesSender() {
    return linkDomainMatchesSender;
  }

  public String getEmailId() {
    return emailId;
  }

  public String getFrom() {
    return from;
  }

  public String getReceivedAt() {
    return receivedAt;
  }

  public String getSenderTrust() {
    return senderTrust;
  }
}
