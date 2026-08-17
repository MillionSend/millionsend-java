package com.millionsend.services;

import static com.millionsend.core.HttpClient.enc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.millionsend.MillionSendException;
import com.millionsend.core.HttpClient;
import com.millionsend.model.Contact;
import com.millionsend.model.ContactAddress;
import com.millionsend.model.CreateContactOptions;
import com.millionsend.model.Id;
import com.millionsend.model.ListOptions;
import com.millionsend.model.ListResponse;
import com.millionsend.model.RemoveContactResponse;
import com.millionsend.model.UpdateContactOptions;
import com.millionsend.model.UpdateContactTopicsOptions;

/**
 * The {@code contacts} resource. Contacts are team-global and addressable by id
 * or email (email wins when both are set). Per-contact topic subscriptions live
 * under {@link #topics()}.
 */
public final class Contacts {

  private final HttpClient http;
  private final ContactTopics topics;

  public Contacts(HttpClient http) {
    this.http = http;
    this.topics = new ContactTopics(http);
  }

  /** Per-contact topic subscriptions ({@code contacts().topics().update(...)}). */
  public ContactTopics topics() {
    return topics;
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
}
