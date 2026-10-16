package com.millionsend.model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Filters for {@code inboxes().codes().latest(...)}. */
public final class LatestCodeOptions {

  private String from;
  private Integer wait;
  private Integer maxAge;
  private String since;

  private LatestCodeOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public Integer getWaitSeconds() {
    return wait;
  }

  /** Flatten into the wire query map, dropping unset params. */
  public Map<String, String> toQuery() {
    Map<String, String> q = new LinkedHashMap<>();
    Query.put(q, "from", from);
    Query.put(q, "wait", wait);
    Query.put(q, "max_age", maxAge);
    Query.put(q, "since", since);
    return q;
  }

  public static final class Builder {
    private final LatestCodeOptions o = new LatestCodeOptions();

    /** A sender address, or a domain that also covers its subdomains. */
    public Builder from(String from) {
      o.from = from;
      return this;
    }

    /** Seconds (0-50) to wait for a code to arrive. */
    public Builder waitSeconds(int seconds) {
      o.wait = seconds;
      return this;
    }

    /**
     * Ignore codes delivered (or released from quarantine) more than this many seconds ago
     * (60-86400; the API defaults to 600).
     */
    public Builder maxAge(int seconds) {
      o.maxAge = seconds;
      return this;
    }

    /**
     * ISO 8601 with an offset, like {@code Instant.toString()}: only mail received after it.
     * Pass the previous code's {@code receivedAt} so a second sign-in waits for its own code.
     */
    public Builder since(String since) {
      o.since = since;
      return this;
    }

    public LatestCodeOptions build() {
      return o;
    }
  }
}
