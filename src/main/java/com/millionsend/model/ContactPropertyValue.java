package com.millionsend.model;

/** A contact's custom property as returned by the API: its declared type and the value. */
public final class ContactPropertyValue {

  private String type;
  private Object value;

  /** {@code string}, {@code number} or {@code date} (MillionSend extension). */
  public String getType() {
    return type;
  }

  /**
   * A {@link Number} when {@link #getType()} is {@code number}, else a {@link String}; a
   * {@code date} value is the date as it was written.
   */
  public Object getValue() {
    return value;
  }
}
