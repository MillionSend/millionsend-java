package com.millionsend.model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Pagination plus an optional {@code kind} filter for {@code inboxes().list(...)}. */
public final class ListInboxesOptions {

  private Integer limit;
  private String after;
  private String before;
  private String kind;

  private ListInboxesOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  /** Flatten into the wire query map, dropping unset params. */
  public Map<String, String> toQuery() {
    Map<String, String> q = new LinkedHashMap<>();
    Query.put(q, "limit", limit);
    Query.put(q, "after", after);
    Query.put(q, "before", before);
    Query.put(q, "kind", kind);
    return q;
  }

  public static final class Builder {
    private final ListInboxesOptions o = new ListInboxesOptions();

    public Builder limit(int limit) {
      o.limit = limit;
      return this;
    }

    public Builder after(String after) {
      o.after = after;
      return this;
    }

    public Builder before(String before) {
      o.before = before;
      return this;
    }

    /** {@code agent} or {@code human}. */
    public Builder kind(String kind) {
      o.kind = kind;
      return this;
    }

    public ListInboxesOptions build() {
      return o;
    }
  }
}
