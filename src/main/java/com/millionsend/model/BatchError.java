package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** One rejected item of a permissive-mode batch: its index in the request and the reason. */
public final class BatchError {

  private int index;

  @JsonProperty("statusCode")
  private Integer statusCode;

  private String name;
  private String message;

  public int getIndex() {
    return index;
  }

  /**
   * Contacts batch only: the HTTP status strict mode would answer for the item; {@code null}
   * on email batches and on servers before v0.12.0.
   */
  public Integer getStatusCode() {
    return statusCode;
  }

  /**
   * Contacts batch only: the error name strict mode would answer, like {@code validation_error};
   * {@code null} on email batches and on servers before v0.12.0.
   */
  public String getName() {
    return name;
  }

  public String getMessage() {
    return message;
  }
}
