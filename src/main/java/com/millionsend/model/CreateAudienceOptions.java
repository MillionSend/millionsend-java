package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Options for {@code audiences().create(...)}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateAudienceOptions {

  private String name;

  private CreateAudienceOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public String getName() {
    return name;
  }

  public static final class Builder {
    private final CreateAudienceOptions o = new CreateAudienceOptions();

    public Builder name(String name) {
      o.name = name;
      return this;
    }

    public CreateAudienceOptions build() {
      return o;
    }
  }
}
