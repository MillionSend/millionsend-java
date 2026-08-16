package com.millionsend.model;

import java.util.List;

/** A bare {@code { data }} envelope — the batch-send result and the unpaginated topics list. */
public final class DataResponse<T> {

  private List<T> data;

  public List<T> getData() {
    return data;
  }
}
