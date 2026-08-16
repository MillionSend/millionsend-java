package com.millionsend.services;

import static com.millionsend.core.HttpClient.enc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.millionsend.MillionSendException;
import com.millionsend.core.HttpClient;
import com.millionsend.model.CreateSegmentOptions;
import com.millionsend.model.DeletedResponse;
import com.millionsend.model.ListOptions;
import com.millionsend.model.ListResponse;
import com.millionsend.model.Segment;
import com.millionsend.model.UpdateSegmentOptions;

/**
 * The {@code segments} resource — MillionSend dynamic segments (a saved filter
 * over an audience's contacts). No Resend equivalent; served under /segments2.
 */
public final class Segments {

  private final HttpClient http;

  public Segments(HttpClient http) {
    this.http = http;
  }

  /** POST /segments2 */
  public Segment create(CreateSegmentOptions options) throws MillionSendException {
    return http.request("POST", "/segments2", options, null, null, new TypeReference<Segment>() {});
  }

  /** GET /segments2/{id} — includes a live {@code contactCount}. */
  public Segment get(String id) throws MillionSendException {
    return http.request(
        "GET", "/segments2/" + enc(id), null, null, null, new TypeReference<Segment>() {});
  }

  /** GET /segments2 */
  public ListResponse<Segment> list() throws MillionSendException {
    return list(null);
  }

  /** GET /segments2 with pagination. */
  public ListResponse<Segment> list(ListOptions options) throws MillionSendException {
    return http.request(
        "GET", "/segments2", null, options == null ? null : options.toQuery(), null,
        new TypeReference<ListResponse<Segment>>() {});
  }

  /** PATCH /segments2/{id} */
  public Segment update(String id, UpdateSegmentOptions options) throws MillionSendException {
    return http.request(
        "PATCH", "/segments2/" + enc(id), options, null, null, new TypeReference<Segment>() {});
  }

  /** DELETE /segments2/{id} */
  public DeletedResponse remove(String id) throws MillionSendException {
    return http.request(
        "DELETE", "/segments2/" + enc(id), null, null, null,
        new TypeReference<DeletedResponse>() {});
  }
}
