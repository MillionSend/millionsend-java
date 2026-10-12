package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A partial set of safety rules, for {@code inboxes().safety().update(...)} and
 * {@link CreateInboxOptions.Builder#safety}. Only the fields you set are sent;
 * {@code sendCap(null)} removes the inbox's daily cap.
 */
public final class InboxSafetyOptions {

  private final Map<String, Object> changes = new LinkedHashMap<>();

  private InboxSafetyOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  /** The snake_case body — only the fields the caller set. */
  @JsonValue
  public Map<String, Object> getChanges() {
    return changes;
  }

  public static final class Builder {
    private final InboxSafetyOptions o = new InboxSafetyOptions();

    /** {@code anyone}, {@code verified} or {@code allowlist}. */
    public Builder acceptFrom(String acceptFrom) {
      o.changes.put("accept_from", acceptFrom);
      return this;
    }

    public Builder allowlist(String... allowlist) {
      return allowlist(new ArrayList<>(Arrays.asList(allowlist)));
    }

    /** Replaces the whole list. */
    public Builder allowlist(List<String> allowlist) {
      o.changes.put("allowlist", allowlist);
      return this;
    }

    public Builder ownerAddresses(List<String> ownerAddresses) {
      o.changes.put("owner_addresses", ownerAddresses);
      return this;
    }

    public Builder screening(boolean screening) {
      o.changes.put("screening", screening);
      return this;
    }

    public Builder extractCodes(boolean extractCodes) {
      o.changes.put("extract_codes", extractCodes);
      return this;
    }

    /** {@code reply_only}, {@code allowlist} or {@code open}. */
    public Builder sendPolicy(String sendPolicy) {
      o.changes.put("send_policy", sendPolicy);
      return this;
    }

    public Builder requireApproval(boolean requireApproval) {
      o.changes.put("require_approval", requireApproval);
      return this;
    }

    public Builder privateCodes(boolean privateCodes) {
      o.changes.put("private_codes", privateCodes);
      return this;
    }

    /** {@code null} removes the cap. */
    public Builder sendCap(Integer sendCap) {
      o.changes.put("send_cap", sendCap);
      return this;
    }

    /** Team members' login addresses or owner addresses that get a copy of every message. */
    public Builder forwardTo(List<String> forwardTo) {
      o.changes.put("forward_to", forwardTo);
      return this;
    }

    public InboxSafetyOptions build() {
      return o;
    }
  }
}
