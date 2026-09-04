package com.millionsend.services;

import static com.millionsend.core.HttpClient.enc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.millionsend.MillionSendException;
import com.millionsend.core.HttpClient;
import com.millionsend.core.RequestOptions;
import com.millionsend.model.ConflictMode;
import com.millionsend.model.Contact;
import com.millionsend.model.ContactAddress;
import com.millionsend.model.CreateBatchContactsResponse;
import com.millionsend.model.CreateContactOptions;
import com.millionsend.model.DeletedResponse;
import com.millionsend.model.Id;
import com.millionsend.model.ListOptions;
import com.millionsend.model.ListResponse;
import com.millionsend.model.RemoveContactResponse;
import com.millionsend.model.UpdateContactOptions;
import com.millionsend.model.UpdateContactTopicsOptions;
import java.util.Collections;
import java.util.List;

/**
 * The {@code contacts} resource. Contacts are team-global and addressable by id
 * or email (email wins when both are set). Per-contact topic subscriptions live
 * under {@link #topics()}, segment membership under {@link #segments()}, and
 * bulk creation under {@link #batch()}.
 */
public final class Contacts {

  private final HttpClient http;
  private final ContactTopics topics;
  private final ContactSegments segments;
  private final ContactsBatch batch;

  public Contacts(HttpClient http) {
    this.http = http;
    this.topics = new ContactTopics(http);
    this.segments = new ContactSegments(http);
    this.batch = new ContactsBatch(http);
  }

  /** Per-contact topic subscriptions ({@code contacts().topics().update(...)}). */
  public ContactTopics topics() {
    return topics;
  }

  /** Segment membership ({@code contacts().segments().add(...)}). */
  public ContactSegments segments() {
    return segments;
  }

  /** Bulk creation ({@code contacts().batch().create(...)}), a MillionSend extension. */
  public ContactsBatch batch() {
    return batch;
  }

  /** POST /contacts — 409 {@code validation_error} when the email already exists on the team. */
  public Id create(CreateContactOptions options) throws MillionSendException {
    return http.request("POST", "/contacts", options, null, null, new TypeReference<Id>() {});
  }

  /** GET a contact by a bare id. */
  public Contact get(String id) throws MillionSendException {
    return get(ContactAddress.id(id));
  }

  /** GET a contact by address. */
  public Contact get(ContactAddress address) throws MillionSendException {
    return http.request("GET", contactPath(address), null, null, null, new TypeReference<Contact>() {});
  }

  /** PATCH a contact. Only the fields set on {@code options} are sent (null clears). */
  public Id update(UpdateContactOptions options) throws MillionSendException {
    String path = contactPath(options.getId(), options.getEmail());
    return http.request("PATCH", path, options.getChanges(), null, null, new TypeReference<Id>() {});
  }

  /** DELETE a contact by a bare id. */
  public RemoveContactResponse remove(String id) throws MillionSendException {
    return remove(ContactAddress.id(id));
  }

  /** DELETE a contact by address. */
  public RemoveContactResponse remove(ContactAddress address) throws MillionSendException {
    return http.request(
        "DELETE", contactPath(address), null, null, null,
        new TypeReference<RemoveContactResponse>() {});
  }

  /** GET /contacts */
  public ListResponse<Contact> list() throws MillionSendException {
    return list(null);
  }

  /** GET /contacts with pagination. */
  public ListResponse<Contact> list(ListOptions options) throws MillionSendException {
    return http.request(
        "GET", "/contacts", null, options == null ? null : options.toQuery(), null,
        new TypeReference<ListResponse<Contact>>() {});
  }

  private static String contactPath(ContactAddress a) {
    return contactPath(a.getId(), a.getEmail());
  }

  /** Email wins over id. */
  private static String contactPath(String id, String email) {
    return "/contacts/" + enc(email != null ? email : (id != null ? id : ""));
  }

  /** Per-contact topic subscriptions (opt in/out of a topic). */
  public static final class ContactTopics {

    private final HttpClient http;

    ContactTopics(HttpClient http) {
      this.http = http;
    }

    /** PATCH /contacts/{idOrEmail}/topics with the bare array of topic updates. */
    public Id update(UpdateContactTopicsOptions options) throws MillionSendException {
      String key = enc(options.getEmail() != null ? options.getEmail() : options.getId());
      return http.request(
          "PATCH", "/contacts/" + key + "/topics", options.getTopics(), null, null,
          new TypeReference<Id>() {});
    }
  }

  /** Segment membership of a contact. */
  public static final class ContactSegments {

    private final HttpClient http;

    ContactSegments(HttpClient http) {
      this.http = http;
    }

    /** POST /contacts/{idOrEmail}/segments/{segmentId} — idempotent. */
    public Id add(String contactIdOrEmail, String segmentId) throws MillionSendException {
      return http.request(
          "POST", "/contacts/" + enc(contactIdOrEmail) + "/segments/" + enc(segmentId), null, null,
          null, new TypeReference<Id>() {});
    }

    /** DELETE /contacts/{idOrEmail}/segments/{segmentId} — 404 when not a member. */
    public DeletedResponse remove(String contactIdOrEmail, String segmentId)
        throws MillionSendException {
      return http.request(
          "DELETE", "/contacts/" + enc(contactIdOrEmail) + "/segments/" + enc(segmentId), null,
          null, null, new TypeReference<DeletedResponse>() {});
    }
  }

  /** Bulk contact creation: POST /contacts/batch, up to 1000 items. */
  public static final class ContactsBatch {

    private final HttpClient http;

    ContactsBatch(HttpClient http) {
      this.http = http;
    }

    /** POST /contacts/batch with the server defaults ({@code on_conflict=error}, strict validation). */
    public CreateBatchContactsResponse create(List<CreateContactOptions> contacts)
        throws MillionSendException {
      return create(contacts, null, null);
    }

    /** POST /contacts/batch?on_conflict={mode} */
    public CreateBatchContactsResponse create(
        List<CreateContactOptions> contacts, ConflictMode onConflict) throws MillionSendException {
      return create(contacts, onConflict, null);
    }

    /**
     * POST /contacts/batch?on_conflict={mode} with per-request options — set
     * {@code batchValidation(PERMISSIVE)} to write the valid items and get the
     * rest back in {@code errors}.
     */
    public CreateBatchContactsResponse create(
        List<CreateContactOptions> contacts, ConflictMode onConflict, RequestOptions requestOptions)
        throws MillionSendException {
      return http.request(
          "POST", "/contacts/batch", contacts,
          onConflict == null ? null : Collections.singletonMap("on_conflict", onConflict.getValue()),
          requestOptions, new TypeReference<CreateBatchContactsResponse>() {});
    }
  }
}
