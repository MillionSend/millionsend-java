package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Options for {@code contactProperties().create(...)}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateContactPropertyOptions {

  private String key;
  private String type;
  private Object fallbackValue;

  private CreateContactPropertyOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final CreateContactPropertyOptions o = new CreateContactPropertyOptions();

    public Builder key(String key) {
      o.key = key;
      return this;
    }

    /**
     * {@code string}, {@code number} or {@code date}. {@code date} is a MillionSend extension:
     * its values must read as an ISO 8601 date ({@code 2026-01-08}, midnight UTC), an ISO 8601
     * date-time ({@code 2026-01-08T10:30:00Z} or {@code 2026-01-08 10:30}, UTC when it names no
     * zone) or Unix time in seconds or milliseconds from 2000 to 2100, else the write fails
     * with 422 {@code validation_error}.
     */
    public Builder type(String type) {
      o.type = type;
      return this;
    }

    /** A {@link String} or a {@link Number} matching {@code type}; for {@code date}, a date. */
    public Builder fallbackValue(Object fallbackValue) {
      o.fallbackValue = fallbackValue;
      return this;
    }

    public CreateContactPropertyOptions build() {
      return o;
    }
  }
}
