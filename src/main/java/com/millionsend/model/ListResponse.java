package com.millionsend.model;

import java.util.List;

/** The paginated list envelope {@code { object: "list", data, has_more }}. */
public class ListResponse<T> {

  private String object;
  private List<T> data;
  private boolean hasMore;

  public String getObject() {
    return object;
  }

  public List<T> getData() {
    return data;
  }

  /** Whether more items follow in the direction of the cursor ({@code after} or {@code before}). */
  public boolean isHasMore() {
    return hasMore;
  }
}
