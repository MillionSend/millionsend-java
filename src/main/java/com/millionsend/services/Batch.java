package com.millionsend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.millionsend.MillionSendException;
import com.millionsend.core.HttpClient;
import com.millionsend.model.CreateEmailOptions;
import com.millionsend.model.CreateEmailResponse;
import com.millionsend.model.DataResponse;
import java.util.List;

/** The {@code batch} resource: send up to 100 emails in one call. */
public final class Batch {

  private final HttpClient http;

  public Batch(HttpClient http) {
    this.http = http;
  }

  /** POST /emails/batch */
  public DataResponse<CreateEmailResponse> send(List<CreateEmailOptions> emails)
      throws MillionSendException {
    return send(emails, null);
  }

  /** POST /emails/batch with an {@code Idempotency-Key}. */
  public DataResponse<CreateEmailResponse> send(
      List<CreateEmailOptions> emails, String idempotencyKey) throws MillionSendException {
    return http.request(
        "POST", "/emails/batch", emails, null, idempotencyKey,
        new TypeReference<DataResponse<CreateEmailResponse>>() {});
  }

  /** Alias of {@link #send(List)}, mirroring Resend. */
  public DataResponse<CreateEmailResponse> create(List<CreateEmailOptions> emails)
      throws MillionSendException {
    return send(emails);
  }
}
