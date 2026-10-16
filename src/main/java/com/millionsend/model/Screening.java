package com.millionsend.model;

import java.util.List;

/**
 * How an inbox screened a received email. Lists carry {@code verdict} and
 * {@code senderTrust} only. Every value is a plain string: the API may add new ones.
 */
public final class Screening {

  private String verdict;
  private String senderTrust;
  private String quarantineReason;
  private List<String> reasons;
  private Integer injectionScore;
  private Integer phishingScore;
  private Boolean hiddenTextRemoved;

  /** {@code pending}, {@code clean}, {@code suspicious}, {@code quarantined} or {@code released}. */
  public String getVerdict() {
    return verdict;
  }

  /**
   * {@code owner}, {@code allowlisted}, {@code known_thread}, {@code external},
   * {@code unauthenticated} or {@code failed}.
   */
  public String getSenderTrust() {
    return senderTrust;
  }

  /** Set only when quarantined or released, e.g. {@code not_allowlisted}, {@code injection}, {@code virus}. */
  public String getQuarantineReason() {
    return quarantineReason;
  }

  /** Reason keys from the injection screen, e.g. {@code targets_agent}. */
  public List<String> getReasons() {
    return reasons;
  }

  /** 0-100; {@code null} when the email was not screened. */
  public Integer getInjectionScore() {
    return injectionScore;
  }

  /** 0-100; {@code null} when the email was not screened. */
  public Integer getPhishingScore() {
    return phishingScore;
  }

  /** True when text a person could not see was stripped from {@code extractedText}. */
  public Boolean getHiddenTextRemoved() {
    return hiddenTextRemoved;
  }
}
