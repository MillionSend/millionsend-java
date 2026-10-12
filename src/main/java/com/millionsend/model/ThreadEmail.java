package com.millionsend.model;

import java.util.List;

/**
 * One email in an inbox thread, either direction; also the sent email that reply and
 * forward return ({@code emailId}, {@code threadId}, {@code inboxId}). From
 * {@code receiving().reply(...)} and {@code forward(...)} only {@code object},
 * {@code id}, {@code threadId} and {@code inboxId} are set.
 */
public final class ThreadEmail {

  private String object;
  private String id;
  private String direction;
  private String from;
  private List<String> to;
  private List<String> cc;
  private List<String> bcc;
  private List<String> replyTo;
  private String subject;
  private String messageId;
  private String html;
  private String text;
  private List<ReceivedAttachment> attachments;
  private Boolean read;
  private String receivedAt;
  private String extractedText;
  private Screening screening;
  private String spamVerdict;
  private String virusVerdict;
  private String status;
  private String emailId;
  private String threadId;
  private String inboxId;

  public String getObject() {
    return object;
  }

  public String getId() {
    return id;
  }

  /** {@code inbound} or {@code outbound}. */
  public String getDirection() {
    return direction;
  }

  /** With the display name when there is one: {@code Name <address>}. */
  public String getFrom() {
    return from;
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

  public List<String> getReplyTo() {
    return replyTo;
  }

  public String getSubject() {
    return subject;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getHtml() {
    return html;
  }

  /** Inbound text has invisible characters removed. */
  public String getText() {
    return text;
  }

  public List<ReceivedAttachment> getAttachments() {
    return attachments;
  }

  public Boolean getRead() {
    return read;
  }

  /** When it arrived or was sent. */
  public String getReceivedAt() {
    return receivedAt;
  }

  /** Inbound: the reply without quotes, signature or hidden text. */
  public String getExtractedText() {
    return extractedText;
  }

  /** Inbound only. */
  public Screening getScreening() {
    return screening;
  }

  /** Inbound only. */
  public String getSpamVerdict() {
    return spamVerdict;
  }

  /** Inbound only. */
  public String getVirusVerdict() {
    return virusVerdict;
  }

  /** Outbound only: the email's latest delivery event. */
  public String getStatus() {
    return status;
  }

  /** The sent email's id (reply and forward results). */
  public String getEmailId() {
    return emailId;
  }

  public String getThreadId() {
    return threadId;
  }

  public String getInboxId() {
    return inboxId;
  }
}
