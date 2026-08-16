package com.millionsend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

/**
 * Options for {@code contacts().create(...)}. {@code audienceId} selects the
 * path (audience-scoped vs top-level) and is never part of the body.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateContactOptions {

  @JsonIgnore private String audienceId;
  private String email;
  private String firstName;
  private String lastName;
  private Boolean unsubscribed;
  private Map<String, Object> properties;

  private CreateContactOptions() {}

  public static Builder builder() {
    return new Builder();
  }

  public String getAudienceId() {
    return audienceId;
  }

  public static final class Builder {
    private final CreateContactOptions o = new CreateContactOptions();

    public Builder audienceId(String audienceId) {
      o.audienceId = audienceId;
      return this;
    }

    public Builder email(String email) {
      o.email = email;
      return this;
    }

    public Builder firstName(String firstName) {
      o.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      o.lastName = lastName;
      return this;
    }

    public Builder unsubscribed(boolean unsubscribed) {
      o.unsubscribed = unsubscribed;
      return this;
    }

    public Builder properties(Map<String, Object> properties) {
      o.properties = properties;
      return this;
    }

    public CreateContactOptions build() {
      return o;
    }
  }
}
