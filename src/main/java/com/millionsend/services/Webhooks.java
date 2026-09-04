package com.millionsend.services;

import static com.millionsend.core.HttpClient.enc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.millionsend.MillionSendException;
import com.millionsend.core.HttpClient;
import com.millionsend.model.CreateWebhookOptions;
import com.millionsend.model.CreateWebhookResponse;
import com.millionsend.model.DeletedResponse;
import com.millionsend.model.Id;
import com.millionsend.model.ListOptions;
import com.millionsend.model.ListResponse;
import com.millionsend.model.UpdateWebhookOptions;
import com.millionsend.model.Webhook;

/** The {@code webhooks} resource: create, get, list, update, remove. */
public final class Webhooks {

  private final HttpClient http;

  public Webhooks(HttpClient http) {
    this.http = http;
  }

  /** POST /webhooks — returns the id and the signing secret. */
  public CreateWebhookResponse create(CreateWebhookOptions options) throws MillionSendException {
    return http.request(
        "POST", "/webhooks", options, null, null, new TypeReference<CreateWebhookResponse>() {});
  }

  /** GET /webhooks/{id} — includes {@code signingSecret}. */
  public Webhook get(String id) throws MillionSendException {
    return http.request(
        "GET", "/webhooks/" + enc(id), null, null, null, new TypeReference<Webhook>() {});
  }

  /** GET /webhooks */
  public ListResponse<Webhook> list() throws MillionSendException {
    return list(null);
  }

  /** GET /webhooks with pagination. */
  public ListResponse<Webhook> list(ListOptions options) throws MillionSendException {
    return http.request(
        "GET", "/webhooks", null, options == null ? null : options.toQuery(), null,
        new TypeReference<ListResponse<Webhook>>() {});
  }

  /** PATCH /webhooks/{id} */
  public Id update(String id, UpdateWebhookOptions options) throws MillionSendException {
    return http.request(
        "PATCH", "/webhooks/" + enc(id), options, null, null, new TypeReference<Id>() {});
  }

  /** DELETE /webhooks/{id} */
  public DeletedResponse remove(String id) throws MillionSendException {
    return http.request(
        "DELETE", "/webhooks/" + enc(id), null, null, null,
        new TypeReference<DeletedResponse>() {});
  }
}
