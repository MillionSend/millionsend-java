package com.millionsend.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The {@link ListThreadsOptions} filters plus search terms for
 * {@code inboxes().threads().search(...)}. Search covers subject, sender and
 * recipients; every per-email filter must match the same email.
 */
public final class SearchThreadsOptions {

  private final ListThreadsOptions.Builder list = ListThreadsOptions.builder();
  private String query;
  private List<String> from;
  private List<String> to;
  private List<String> cc;
  private List<String> bcc;
  private Boolean hasAttachment;
  private String startDate;
  private String endDate;

  private SearchThreadsOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  /** Flatten into the wire query map, dropping unset params. */
  public Map<String, String> toQuery() {
    Map<String, String> q = list.build().toQuery();
    Query.put(q, "query", query);
    Query.csv(q, "from", from);
    Query.csv(q, "to", to);
    Query.csv(q, "cc", cc);
    Query.csv(q, "bcc", bcc);
    Query.put(q, "has_attachment", hasAttachment);
    Query.put(q, "start_date", startDate);
    Query.put(q, "end_date", endDate);
    return q;
  }

  public static final class Builder {
    private final SearchThreadsOptions o = new SearchThreadsOptions();

    /** Every word must match; {@code "a phrase"} and {@code -word} exclusions work. */
    public Builder query(String query) {
      o.query = query;
      return this;
    }

    /** Substrings of the sender's address or name; any may match. */
    public Builder from(String... from) {
      o.from = new ArrayList<>(Arrays.asList(from));
      return this;
    }

    public Builder to(String... to) {
      o.to = new ArrayList<>(Arrays.asList(to));
      return this;
    }

    public Builder cc(String... cc) {
      o.cc = new ArrayList<>(Arrays.asList(cc));
      return this;
    }

    public Builder bcc(String... bcc) {
      o.bcc = new ArrayList<>(Arrays.asList(bcc));
      return this;
    }

    public Builder hasAttachment(boolean hasAttachment) {
      o.hasAttachment = hasAttachment;
      return this;
    }

    /** {@code YYYY-MM-DD} (the whole day) or ISO 8601. */
    public Builder startDate(String startDate) {
      o.startDate = startDate;
      return this;
    }

    /** {@code YYYY-MM-DD} (the whole day) or ISO 8601. */
    public Builder endDate(String endDate) {
      o.endDate = endDate;
      return this;
    }

    public Builder folders(String... folders) {
      o.list.folders(folders);
      return this;
    }

    public Builder labels(String... labels) {
      o.list.labels(labels);
      return this;
    }

    public Builder read(boolean read) {
      o.list.read(read);
      return this;
    }

    public Builder limit(int limit) {
      o.list.limit(limit);
      return this;
    }

    public Builder after(String after) {
      o.list.after(after);
      return this;
    }

    public Builder before(String before) {
      o.list.before(before);
      return this;
    }

    public Builder includeQuarantined(boolean includeQuarantined) {
      o.list.includeQuarantined(includeQuarantined);
      return this;
    }

    public SearchThreadsOptions build() {
      return o;
    }
  }
}
