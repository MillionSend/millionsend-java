package com.millionsend.model;

import java.util.List;

/**
 * Codes and sign-in links found in a received email. Lists carry {@code kinds} only;
 * the code and link values come only from {@code receiving().get(id)}, and never for
 * held mail, failed authentication or an inbox with code extraction off.
 */
public final class ReceivedEmailExtracted {

  private List<String> kinds;
  private String code;
  private String link;
  private Boolean linkDomainMatchesSender;
  private Boolean ambiguous;

  /** A subset of {@code code} and {@code link}. */
  public List<String> getKinds() {
    return kinds;
  }

  public String getCode() {
    return code;
  }

  public String getLink() {
    return link;
  }

  public Boolean getLinkDomainMatchesSender() {
    return linkDomainMatchesSender;
  }

  /** True when several candidates matched and none was picked. */
  public Boolean getAmbiguous() {
    return ambiguous;
  }
}
