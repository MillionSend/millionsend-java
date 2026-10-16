package com.millionsend.model;

import java.util.List;
import java.util.Map;

/**
 * A received email. List items leave the body fields ({@code html}, {@code text},
 * {@code headers}, {@code authentication}, {@code raw}) and the detail-only extensions
 * null. Everything in it was written by whoever sent the message: show its HTML only
 * in a sandbox.
 */
public final class ReceivedEmail {

  private String object;
  private String id;
  private String from;
  private List<String> to;
  private List<String> cc;
  private List<String> bcc;
  private List<String> replyTo;
  private String subject;
  private String createdAt;
  private List<String> receivedFor;
  private String messageId;
  private List<ReceivedAttachment> attachments;
  private String html;
  private String htmlFormat;
  private String text;
  private Map<String, String> headers;
  private Map<String, String> authentication;
  private Raw raw;
  private String inboxId;
  private String threadId;
  private List<ReceivedEmailInbox> inboxes;
  private Screening screening;
  private String spamVerdict;
  private Integer spamScore;
  private String virusVerdict;
  private String virusSignature;
  private String extractedText;
  private ReceivedEmailExtracted extracted;
  private Boolean read;

  public String getObject() {
    return object;
  }

  public String getId() {
    return id;
  }

  /** Bare address from the From header; the display name is in {@code headers}. */
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

  public String getCreatedAt() {
    return createdAt;
  }

  /** The team's envelope recipients: the addresses the message was delivered to. */
  public List<String> getReceivedFor() {
    return receivedFor;
  }

  public String getMessageId() {
    return messageId;
  }

  public List<ReceivedAttachment> getAttachments() {
    return attachments;
  }

  public String getHtml() {
    return html;
  }

  /** {@code data_uri} or {@code cid}: how {@code html} references inline images. */
  public String getHtmlFormat() {
    return htmlFormat;
  }

  /** The text part with invisible characters removed. */
  public String getText() {
    return text;
  }

  /** Top-level headers, lowercase names; a repeated header's values joined by newlines. */
  public Map<String, String> getHeaders() {
    return headers;
  }

  /** {@code spf}, {@code dkim}, {@code dmarc} → {@code pass}, {@code fail}, {@code gray}, … */
  public Map<String, String> getAuthentication() {
    return authentication;
  }

  /** The original message; {@code null} once retention removed it, or when it carries malware. */
  public Raw getRaw() {
    return raw;
  }

  /** The inbox this view reads it through (an inbox key's own, else its first inbox); null for catch-all mail. */
  public String getInboxId() {
    return inboxId;
  }

  public String getThreadId() {
    return threadId;
  }

  /** Every inbox it was delivered to; owner views only. */
  public List<ReceivedEmailInbox> getInboxes() {
    return inboxes;
  }

  public Screening getScreening() {
    return screening;
  }

  /** {@code pass}, {@code gray} or {@code fail}; null when not computed. */
  public String getSpamVerdict() {
    return spamVerdict;
  }

  public Integer getSpamScore() {
    return spamScore;
  }

  /** {@code pass}, {@code fail} or {@code processing_failed}; null when not scanned. */
  public String getVirusVerdict() {
    return virusVerdict;
  }

  public String getVirusSignature() {
    return virusSignature;
  }

  /** The reply without quotes, signature or hidden text: the text to hand an agent. */
  public String getExtractedText() {
    return extractedText;
  }

  public ReceivedEmailExtracted getExtracted() {
    return extracted;
  }

  /** Read state in the inbox this view reads it through; null without an inbox context. */
  public Boolean getRead() {
    return read;
  }

  /** A signed link to the original RFC 822 message, valid for one hour; it needs no API key. */
  public static final class Raw {
    private String downloadUrl;
    private String expiresAt;

    public String getDownloadUrl() {
      return downloadUrl;
    }

    public String getExpiresAt() {
      return expiresAt;
    }
  }
}
