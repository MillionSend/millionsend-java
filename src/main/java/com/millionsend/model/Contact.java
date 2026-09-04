package com.millionsend.model;

import java.util.Map;

/**
 * A contact. Also used for contact list items — {@code properties} is only
 * populated when a single contact is fetched.
 */
public final class Contact {

  private String object;
  private String id;
  private String email;
  private String firstName;
  private String lastName;
  private String createdAt;
  private boolean unsubscribed;
  private Map<String, ContactPropertyValue> properties;

  public String getObject() {
    return object;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public boolean isUnsubscribed() {
    return unsubscribed;
  }

  /** Custom properties keyed by property key, each with its declared type and value. */
  public Map<String, ContactPropertyValue> getProperties() {
    return properties;
  }
}
