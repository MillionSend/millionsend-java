package com.millionsend.model;

/** An audience — a named contact list. Also used for audience list items. */
public final class Audience {

  private String object;
  private String id;
  private String name;
  private String createdAt;

  public String getObject() {
    return object;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCreatedAt() {
    return createdAt;
  }
}
