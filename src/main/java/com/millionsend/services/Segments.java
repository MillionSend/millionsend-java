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
 * over the team's contacts). No Resend equivalent.
 */
public final class Segments {

  private final HttpClient http;

  public Segments(HttpClient http) {
    this.http = http;
  }

  /** POST /segments */
  public Segment create(CreateSegmentOptions options) throws MillionSendException {
    return http.request("POST", "/segments", options, null, null, new TypeReference<Segment>() {});
  }

  /** GET /segments/{id} — includes a live {@code contactCount}. */
  public Segment get(String id) throws MillionSendException {
    return http.request(
        "GET", "/segments/" + enc(id), null, null, null, new TypeReference<Segment>() {});
  }

  /** GET /segments */
  public ListResponse<Segment> list() throws MillionSendException {
    return list(null);
  }

  /** GET /segments with pagination. */
  public ListResponse<Segment> list(ListOptions options) throws MillionSendException {
    return http.request(
        "GET", "/segments", null, options == null ? null : options.toQuery(), null,
        new TypeReference<ListResponse<Segment>>() {});
  }

  /** PATCH /segments/{id} */
  public Segment update(String id, UpdateSegmentOptions options) throws MillionSendException {
    return http.request(
        "PATCH", "/segments/" + enc(id), options, null, null, new TypeReference<Segment>() {});
  }

  /** DELETE /segments/{id} */
  public DeletedResponse remove(String id) throws MillionSendException {
    return http.request(
        "DELETE", "/segments/" + enc(id), null, null, null,
        new TypeReference<DeletedResponse>() {});
  }
}
