package com.millionsend.services;

import static com.millionsend.core.HttpClient.enc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.millionsend.MillionSendException;
import com.millionsend.core.HttpClient;
import com.millionsend.model.Audience;
import com.millionsend.model.CreateAudienceOptions;
import com.millionsend.model.DeletedResponse;
import com.millionsend.model.ListOptions;
import com.millionsend.model.ListResponse;

/** The {@code audiences} resource: create, get, list, remove. */
public final class Audiences {

  private final HttpClient http;

  public Audiences(HttpClient http) {
    this.http = http;
  }

  /** POST /audiences */
  public Audience create(CreateAudienceOptions options) throws MillionSendException {
    return http.request("POST", "/audiences", options, null, null, new TypeReference<Audience>() {});
  }

  /** GET /audiences/{id} */
  public Audience get(String id) throws MillionSendException {
    return http.request(
        "GET", "/audiences/" + enc(id), null, null, null, new TypeReference<Audience>() {});
  }

  /** GET /audiences */
  public ListResponse<Audience> list() throws MillionSendException {
    return list(null);
  }

  /** GET /audiences with pagination. */
  public ListResponse<Audience> list(ListOptions options) throws MillionSendException {
    return http.request(
        "GET", "/audiences", null, options == null ? null : options.toQuery(), null,
        new TypeReference<ListResponse<Audience>>() {});
  }

  /** DELETE /audiences/{id} */
  public DeletedResponse remove(String id) throws MillionSendException {
    return http.request(
        "DELETE", "/audiences/" + enc(id), null, null, null,
        new TypeReference<DeletedResponse>() {});
  }
}
