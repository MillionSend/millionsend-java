package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Options for {@code inboxes().create(...)}. Without {@code emailAddress} or {@code domainId}
 * the inbox gets an address on the instance's shared domain (when it has one):
 * {@code <username>@<team>.<shared domain>}, or a random one without {@code username}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateInboxOptions {

  private String emailAddress;
  private String domainId;
  private String username;
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

    /**
     * An address on a verified team domain with receiving on; not combinable with
     * {@code domainId} or {@code username}.
     */
    public Builder emailAddress(String emailAddress) {
      o.emailAddress = emailAddress;
      return this;
    }

    /**
     * Instead of {@code emailAddress}, the id of a {@code domains().list()} row: the
     * {@link Domain#isShared() shared} row works like leaving both out, and one of your own
     * domains needs {@link #username(String)} and receiving on (or {@code forwarding}).
     */
    public Builder domainId(String domainId) {
      o.domainId = domainId;
      return this;
    }

    /**
     * The part before the @: 1-64 of {@code a-z 0-9 . _ -}, starting and ending with a letter
     * or digit (the server trims and lowercases it). Optional on the shared domain, where
     * reserved names, a {@code fwd-} prefix and brand look-alikes answer 422 and a name in
     * use 409; required with the {@code domainId} of one of your own domains.
     */
    public Builder username(String username) {
      o.username = username;
      return this;
    }

    /** 1-320 characters. */
    public Builder name(String name) {
      o.name = name;
      return this;
    }

    /** The display name used when the inbox sends; reviewed on the shared domain. */
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

    /** From {@code 1h} to {@code 365d}; left out, the inbox never expires. */
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
