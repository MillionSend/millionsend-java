package com.millionsend.model;

/** A topic as one contact sees it: the effective subscription and whether the contact chose it. */
public final class ContactTopic {

  private String id;
  private String name;
  private String description;
  private Subscription subscription;
  private boolean explicit;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  /** The effective choice: the contact's own if set, otherwise the topic default. */
  public Subscription getSubscription() {
    return subscription;
  }

  /** False when {@link #getSubscription()} is the topic default rather than the contact's choice. */
  public boolean isExplicit() {
    return explicit;
  }
}
