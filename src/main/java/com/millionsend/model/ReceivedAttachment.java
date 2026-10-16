package com.millionsend.model;

/**
 * An attachment of a received email, or of a thread email ({@code id}, {@code filename}
 * and {@code size} only; the id of an attachment the inbox sent is its position,
 * {@code "0"}, {@code "1"}, …). {@code downloadUrl} is a signed link valid for one
 * hour, set by the attachment endpoints.
 */
public final class ReceivedAttachment {

  private String object;
  private String id;
  private String filename;
  private Long size;
  private String contentType;
  private String contentDisposition;
  private String contentId;
  private String downloadUrl;
  private String expiresAt;

  public String getObject() {
    return object;
  }

  public String getId() {
    return id;
  }

  public String getFilename() {
    return filename;
  }

  public Long getSize() {
    return size;
  }

  public String getContentType() {
    return contentType;
  }

  /** {@code inline} or {@code attachment}. */
  public String getContentDisposition() {
    return contentDisposition;
  }

  public String getContentId() {
    return contentId;
  }

  /** Answers 410 {@code email_blocked} when the email carries malware. */
  public String getDownloadUrl() {
    return downloadUrl;
  }

  public String getExpiresAt() {
    return expiresAt;
  }
}
