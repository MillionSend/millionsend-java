package com.millionsend.model;

import java.util.List;

/**
 * A draft in an inbox: written by an agent or a person, or held by the inbox's send
 * rules ({@code status: pending_approval}) until a person approves it. List items
 * carry a {@code snippet} instead of {@code html}/{@code text}. A draft send's 200
 * sets only {@code object}, {@code id}, {@code threadId} and {@code emailId}.
 */
public final class InboxDraft {

  private String object;
  private String id;
  private String type;
  private List<String> to;
  private List<String> cc;
  private List<String> bcc;
  private String subject;
  private String html;
  private String text;
  private String snippet;
  private String threadId;
  private String replyToEmailId;
  private String emailId;
  private String createdAt;
  private String updatedAt;
  private String status;
  private List<String> approvalReasons;
  private Boolean forward;
  private String decidedBy;
  private String decidedAt;
  private String expiresAt;

  public String getObject() {
    return object;
  }

  public String getId() {
    return id;
  }

  /** {@code standalone} or {@code reply}. */
  public String getType() {
    return type;
  }

  public List<String> getTo() {
    return to;
  }

  public List<String> getCc() {
    return cc;
  }

  public List<String> getBcc() {
    return bcc;
  }

  public String getSubject() {
    return subject;
  }

  public String getHtml() {
    return html;
  }

  public String getText() {
    return text;
  }

  public String getSnippet() {
    return snippet;
  }

  public String getThreadId() {
    return threadId;
  }

  public String getReplyToEmailId() {
    return replyToEmailId;
  }

  /** Set once the draft is sent. */
  public String getEmailId() {
    return emailId;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  /** {@code draft}, {@code pending_approval}, {@code sent}, {@code rejected} or {@code expired}. */
  public String getStatus() {
    return status;
  }

  /**
   * Why the send rules held it, e.g. {@code send_policy}, {@code ramp}, {@code private_code},
   * or {@code reply_to_redirect} for an agent's reply to a Reply-To at another organization.
   */
  public List<String> getApprovalReasons() {
    return approvalReasons;
  }

  /** True for a held forward. */
  public Boolean getForward() {
    return forward;
  }

  public String getDecidedBy() {
    return decidedBy;
  }

  public String getDecidedAt() {
    return decidedAt;
  }

  /** A pending draft expires 7 days after it was held. */
  public String getExpiresAt() {
    return expiresAt;
  }
}
