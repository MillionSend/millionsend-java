package com.millionsend.model;

import java.util.List;

/**
 * The {@code receiving().list(...)} page. With {@code waitSeconds}, {@code held} lists the
 * matching emails that arrived but are held, by id only; it is {@code null} otherwise.
 */
public final class ReceivedEmailListResponse extends ListResponse<ReceivedEmail> {

  private List<HeldEmail> held;

  public List<HeldEmail> getHeld() {
    return held;
  }
}
