package com.millionsend.model;

/** One inbox a received email was delivered to (owner views of {@link ReceivedEmail#getInboxes()}). */
public final class ReceivedEmailInbox {

  private String inboxId;
  private String threadId;
  private String verdict;

  public String getInboxId() {
    return inboxId;
  }

  public String getThreadId() {
    return threadId;
  }

  /** The screening verdict in that inbox. */
  public String getVerdict() {
    return verdict;
  }
}
