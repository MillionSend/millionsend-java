package com.millionsend.model;

import java.util.List;
import java.util.Map;

/**
 * A conversation in one inbox, both directions. {@code get}/{@code update} return the
 * summary ({@code object}, {@code id}, {@code subject}, {@code folder}, {@code labels},
 * {@code read}, {@code quarantined}); list items add the latest message's
 * {@code from}/{@code to}/{@code cc}/{@code bcc}, counts and flags; search results add
 * {@code matchedEmailId} and {@code highlights}.
 */
public final class InboxThread {

  private String object;
  private String id;
  private String subject;
  private String folder;
  private List<InboxLabel> labels;
  private Boolean read;
  private Integer quarantined;
  private String from;
  private List<String> to;
  private List<String> cc;
  private List<String> bcc;
  private Integer messageCount;
  private Boolean hasAttachment;
  private Boolean hasDraft;
  private String receivedAt;
  private String matchedEmailId;
  private Map<String, List<String>> highlights;

  public String getObject() {
    return object;
  }

  public String getId() {
    return id;
  }

  public String getSubject() {
    return subject;
  }

  /** {@code inbox}, {@code archive}, {@code spam}, {@code sent} or {@code trash}. */
  public String getFolder() {
    return folder;
  }

  public List<InboxLabel> getLabels() {
    return labels;
  }

  /** True when every message in the thread is read. */
  public Boolean getRead() {
    return read;
  }

  /**
   * Messages held in quarantine and not released; mail still being screened isn't counted.
   * The summary always carries it (0 outside owner views); list and search items only with
   * {@code includeQuarantined} in owner views, else {@code null}.
   */
  public Integer getQuarantined() {
    return quarantined;
  }

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

  public Integer getMessageCount() {
    return messageCount;
  }

  public Boolean getHasAttachment() {
    return hasAttachment;
  }

  public Boolean getHasDraft() {
    return hasDraft;
  }

  /** When the thread was last active. */
  public String getReceivedAt() {
    return receivedAt;
  }

  public String getMatchedEmailId() {
    return matchedEmailId;
  }

  /** Matched fragments per field, with {@code **match**} markers. */
  public Map<String, List<String>> getHighlights() {
    return highlights;
  }
}
