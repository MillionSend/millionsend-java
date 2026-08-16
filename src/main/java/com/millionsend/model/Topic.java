package com.millionsend.model;

/** A subscription topic. */
public final class Topic {

  private String id;
  private String name;
  private String description;
  private Subscription defaultSubscription;
  private String createdAt;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Subscription getDefaultSubscription() {
    return defaultSubscription;
  }

  public String getCreatedAt() {
    return createdAt;
  }
}
