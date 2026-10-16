package com.millionsend.model;

/** A label of one inbox; a thread's {@code labels} carry {@code id}, {@code name} and {@code color}. */
public final class InboxLabel {

  private String object;
  private String id;
  private String name;
  private String color;
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

  /** {@code #RRGGBB}, uppercase. */
  public String getColor() {
    return color;
  }

  public String getCreatedAt() {
    return createdAt;
  }
}
