package com.millionsend.services;

import static com.millionsend.core.HttpClient.enc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.millionsend.MillionSendException;
import com.millionsend.core.HttpClient;
import com.millionsend.core.RequestOptions;
import com.millionsend.model.DeletedResponse;
import com.millionsend.model.ForwardOptions;
import com.millionsend.model.ListOptions;
import com.millionsend.model.ListReceivedEmailsOptions;
import com.millionsend.model.ListResponse;
import com.millionsend.model.ReceivedAttachment;
import com.millionsend.model.ReceivedEmail;
import com.millionsend.model.ReceivedEmailListResponse;
import com.millionsend.model.ReplyOptions;
import com.millionsend.model.SendResult;
import com.millionsend.model.ThreadEmail;
import java.util.Collections;

/**
 * The {@code receiving} resource: mail received at the team's receiving domains and
 * inboxes. Everything in a received email was written by its sender, so show its HTML
 * only in a sandbox. Attachments live under {@link #attachments()}.
 */
public final class Receiving {

  private static final TypeReference<ThreadEmail> SENT = new TypeReference<ThreadEmail>() {};

  private final HttpClient http;
  private final Attachments attachments;

  public Receiving(HttpClient http) {
    this.http = http;
    this.attachments = new Attachments(http);
  }

  /** A received email's attachments ({@code receiving().attachments().list(emailId)}). */
  public Attachments attachments() {
    return attachments;
  }

  /** GET /emails/receiving — newest first. */
  public ReceivedEmailListResponse list() throws MillionSendException {
    return list(null);
  }

  /**
   * GET /emails/receiving with filters. With {@code waitSeconds} the call long-polls: it returns
   * as soon as matching mail arrives, or an empty page when the wait ends, and lists held
   * matches by id under {@code held}.
   */
  public ReceivedEmailListResponse list(ListReceivedEmailsOptions options)
      throws MillionSendException {
    return http.longPoll(
        "/emails/receiving",
        options == null ? null : options.toQuery(),
        options == null ? null : options.getWaitSeconds(),
        new TypeReference<ReceivedEmailListResponse>() {});
  }

  /** GET /emails/receiving/{id} — 403 {@code email_quarantined} for held mail read by an agent. */
  public ReceivedEmail get(String id) throws MillionSendException {
    return get(id, null);
  }

  /**
   * GET /emails/receiving/{id}.
   *
   * @param htmlFormat {@code data_uri} (the default) inlines cid images; {@code cid} leaves the references
   */
  public ReceivedEmail get(String id, String htmlFormat) throws MillionSendException {
    return http.request(
        "GET", "/emails/receiving/" + enc(id), null,
        htmlFormat == null ? null : Collections.singletonMap("html_format", htmlFormat), null,
        new TypeReference<ReceivedEmail>() {});
  }

  /** POST /emails/receiving/{id}/reply */
  public SendResult<ThreadEmail> reply(String id, ReplyOptions options)
      throws MillionSendException {
    return reply(id, options, null);
  }

  /**
   * POST /emails/receiving/{id}/reply with an {@code Idempotency-Key}. When the inbox's
   * send rules hold the reply, the result is a draft waiting for approval.
   */
  public SendResult<ThreadEmail> reply(String id, ReplyOptions options, RequestOptions requestOptions)
      throws MillionSendException {
    return http.sendOrHold("/emails/receiving/" + enc(id) + "/reply", options, requestOptions, SENT);
  }

  /** POST /emails/receiving/{id}/forward */
  public SendResult<ThreadEmail> forward(String id, ForwardOptions options)
      throws MillionSendException {
    return forward(id, options, null);
  }

  /**
   * POST /emails/receiving/{id}/forward with an {@code Idempotency-Key}. 403
   * {@code email_quarantined} for an email that carries malware.
   */
  public SendResult<ThreadEmail> forward(
      String id, ForwardOptions options, RequestOptions requestOptions)
      throws MillionSendException {
    return http.sendOrHold("/emails/receiving/" + enc(id) + "/forward", options, requestOptions, SENT);
  }

  /** DELETE /emails/receiving/{id} — erases the email now (a full access key not scoped to an inbox). */
  public DeletedResponse remove(String id) throws MillionSendException {
    return http.request(
        "DELETE", "/emails/receiving/" + enc(id), null, null, null,
        new TypeReference<DeletedResponse>() {});
  }

  /** Attachments of a received email, each with a signed download link valid for one hour. */
  public static final class Attachments {

    private final HttpClient http;

    Attachments(HttpClient http) {
      this.http = http;
    }

    /** GET /emails/receiving/{emailId}/attachments — all of them unless a limit is given. */
    public ListResponse<ReceivedAttachment> list(String emailId) throws MillionSendException {
      return list(emailId, null);
    }

    /** GET /emails/receiving/{emailId}/attachments with pagination. */
    public ListResponse<ReceivedAttachment> list(String emailId, ListOptions options)
        throws MillionSendException {
      return http.request(
          "GET", "/emails/receiving/" + enc(emailId) + "/attachments", null,
          options == null ? null : options.toQuery(), null,
          new TypeReference<ListResponse<ReceivedAttachment>>() {});
    }

    /** GET /emails/receiving/{emailId}/attachments/{id} */
    public ReceivedAttachment get(String emailId, String id) throws MillionSendException {
      return http.request(
          "GET", "/emails/receiving/" + enc(emailId) + "/attachments/" + enc(id), null, null, null,
          new TypeReference<ReceivedAttachment>() {});
    }
  }
}
