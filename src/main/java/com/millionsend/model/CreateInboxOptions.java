package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Options for {@code inboxes().create(...)}. Without {@code emailAddress} the
 * inbox gets a random address on the shared domain (when the instance has one).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateInboxOptions {

  private String emailAddress;
  private String name;
  private String fromName;
  private Boolean forwarding;
  private String kind;
  private String expiresIn;
  private InboxSafetyOptions safety;

  private CreateInboxOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final CreateInboxOptions o = new CreateInboxOptions();

    /** An address on a verified team domain with receiving on. */
    public Builder emailAddress(String emailAddress) {
      o.emailAddress = emailAddress;
      return this;
    }

    public Builder name(String name) {
      o.name = name;
      return this;
    }

    /** The display name used when the inbox sends. */
    public Builder fromName(String fromName) {
      o.fromName = fromName;
      return this;
    }

    /** Provision a {@code receivingAddress} to forward mail to, for a domain that keeps its own MX. */
    public Builder forwarding(boolean forwarding) {
      o.forwarding = forwarding;
      return this;
    }

    /** {@code agent} (the default) or {@code human}. */
    public Builder kind(String kind) {
      o.kind = kind;
      return this;
    }

    /** Like {@code 1h} or {@code 30d}. Shared-domain inboxes default to 24 hours, at most 30 days. */
    public Builder expiresIn(String expiresIn) {
      o.expiresIn = expiresIn;
      return this;
    }

    /** Safety rules to start with; the rest keep the defaults for the kind. */
    public Builder safety(InboxSafetyOptions safety) {
      o.safety = safety;
      return this;
    }

    public CreateInboxOptions build() {
      return o;
    }
  }
}
