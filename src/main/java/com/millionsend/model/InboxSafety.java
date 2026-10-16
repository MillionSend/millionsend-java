package com.millionsend.model;

import java.util.List;

/** An inbox's safety rules ({@code inboxes().safety().get(...)}). */
public final class InboxSafety {

  private String object;
  private String inboxId;
  private String acceptFrom;
  private List<String> allowlist;
  private List<String> ownerAddresses;
  private Boolean screening;
  private Boolean extractCodes;
  private String sendPolicy;
  private Boolean requireApproval;
  private Boolean privateCodes;
  private Integer sendCap;
  private List<String> forwardTo;

  public String getObject() {
    return object;
  }

  public String getInboxId() {
    return inboxId;
  }

  /** {@code anyone}, {@code verified} or {@code allowlist}: mail failing it is quarantined. */
  public String getAcceptFrom() {
    return acceptFrom;
  }

  /** {@code local@domain} addresses, or {@code *@domain} for a domain and its subdomains. */
  public List<String> getAllowlist() {
    return allowlist;
  }

  /** Owners besides the team members' login addresses. */
  public List<String> getOwnerAddresses() {
    return ownerAddresses;
  }

  /** The prompt-injection screen (agent inboxes). */
  public Boolean getScreening() {
    return screening;
  }

  public Boolean getExtractCodes() {
    return extractCodes;
  }

  /** {@code reply_only}, {@code allowlist} or {@code open}. */
  public String getSendPolicy() {
    return sendPolicy;
  }

  public Boolean getRequireApproval() {
    return requireApproval;
  }

  /** Received codes and sign-in links can't be sent to anyone but owners and the allowlist. */
  public Boolean getPrivateCodes() {
    return privateCodes;
  }

  /** Unattended recipients per UTC day; null for no inbox cap. */
  public Integer getSendCap() {
    return sendCap;
  }

  public List<String> getForwardTo() {
    return forwardTo;
  }
}
