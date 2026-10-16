package com.millionsend.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Filters for {@code inboxes().threads().list(...)}. {@code folders} defaults to
 * {@code inbox} ({@code inbox,archive,sent} with {@code labels}).
 */
public final class ListThreadsOptions {

  private List<String> folders;
  private List<String> labels;
  private Boolean read;
  private Integer limit;
  private String after;
  private String before;
  private boolean includeQuarantined;

  private ListThreadsOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  /** Flatten into the wire query map, dropping unset params. */
  public Map<String, String> toQuery() {
    Map<String, String> q = new LinkedHashMap<>();
    Query.csv(q, "folders", folders);
    Query.csv(q, "labels", labels);
    Query.put(q, "read", read);
    Query.put(q, "limit", limit);
    Query.put(q, "after", after);
    Query.put(q, "before", before);
    if (includeQuarantined) {
      q.put("include_quarantined", "true");
    }
    return q;
  }

  public static final class Builder {
    private final ListThreadsOptions o = new ListThreadsOptions();

    /** Any of {@code inbox}, {@code archive}, {@code spam}, {@code sent}, {@code trash}. */
    public Builder folders(String... folders) {
      o.folders = new ArrayList<>(Arrays.asList(folders));
      return this;
    }

    /** Label ids; a thread matches when it has any of them. */
    public Builder labels(String... labels) {
      o.labels = new ArrayList<>(Arrays.asList(labels));
      return this;
    }

    /** {@code true}: fully read threads; {@code false}: threads with unread mail. */
    public Builder read(boolean read) {
      o.read = read;
      return this;
    }

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

    /**
     * Also count held mail, so each item carries {@code quarantined} (owner views: a full
     * access key not scoped to an inbox; an inbox-scoped key gets 403).
     */
    public Builder includeQuarantined(boolean includeQuarantined) {
      o.includeQuarantined = includeQuarantined;
      return this;
    }

    public ListThreadsOptions build() {
      return o;
    }
  }
}
