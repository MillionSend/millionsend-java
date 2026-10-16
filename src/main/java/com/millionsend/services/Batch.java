package com.millionsend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.millionsend.MillionSendException;
import com.millionsend.core.HttpClient;
import com.millionsend.core.RequestOptions;
import com.millionsend.model.CreateBatchEmailsResponse;
import com.millionsend.model.CreateEmailOptions;
import java.util.List;

/**
 * The {@code batch} resource: send up to 100 emails in one call. Items never send from the
 * shared domain: such an item gets 403 {@code inbox_send_policy}, which fails the whole batch
 * in strict mode and lands in {@code errors} in permissive mode.
 */
public final class Batch {

  private final HttpClient http;

  public Batch(HttpClient http) {
    this.http = http;
  }

  /** POST /emails/batch */
  public CreateBatchEmailsResponse send(List<CreateEmailOptions> emails)
      throws MillionSendException {
    return send(emails, (RequestOptions) null);
  }

  /** POST /emails/batch with an {@code Idempotency-Key}. */
  public CreateBatchEmailsResponse send(List<CreateEmailOptions> emails, String idempotencyKey)
      throws MillionSendException {
    return send(emails, RequestOptions.idempotencyKey(idempotencyKey));
  }

  /**
   * POST /emails/batch with per-request options — an idempotency key and/or the
   * {@code x-batch-validation} mode ({@code RequestOptions.builder().batchValidation(...)}).
   */
  public CreateBatchEmailsResponse send(
      List<CreateEmailOptions> emails, RequestOptions requestOptions) throws MillionSendException {
    return http.request(
        "POST", "/emails/batch", emails, null, requestOptions,
        new TypeReference<CreateBatchEmailsResponse>() {});
  }

  /** Alias of {@link #send(List)}, mirroring Resend. */
  public CreateBatchEmailsResponse create(List<CreateEmailOptions> emails)
      throws MillionSendException {
    return send(emails);
  }

  /** Alias of {@link #send(List, RequestOptions)}, mirroring Resend. */
  public CreateBatchEmailsResponse create(
      List<CreateEmailOptions> emails, RequestOptions requestOptions) throws MillionSendException {
    return send(emails, requestOptions);
  }
}
