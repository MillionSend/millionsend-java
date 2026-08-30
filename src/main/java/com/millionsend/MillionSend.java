package com.millionsend;

import com.millionsend.core.HttpClient;
import com.millionsend.services.Batch;
import com.millionsend.services.Broadcasts;
import com.millionsend.services.Contacts;
import com.millionsend.services.Emails;
import com.millionsend.services.Segments;
import com.millionsend.services.Topics;

/**
 * The MillionSend client. Construct once and reuse.
 *
 * <pre>{@code
 * MillionSend ms = new MillionSend("ms_123", "https://mail.acme.dev");
 * CreateEmailResponse sent = ms.emails().send(
 *     CreateEmailOptions.builder()
 *         .from("Acme <onboarding@acme.dev>")
 *         .to("delivered@resend.dev")
 *         .subject("Hello")
 *         .html("<strong>it works</strong>")
 *         .build());
 * }</pre>
 *
 * <p>Every call throws a {@link MillionSendException} on a non-2xx response (or
 * a transport failure). The API key falls back to {@code MILLIONSEND_API_KEY}
 * and the base URL to {@code MILLIONSEND_BASE_URL} (then {@code
 * http://localhost:3001}, since MillionSend is self-hosted). A missing API key
 * throws {@link IllegalArgumentException} at construction.
 */
public final class MillionSend {

  private static final String DEFAULT_BASE_URL = "http://localhost:3001";

  private final Emails emails;
  private final Batch batch;
  private final Contacts contacts;
  private final Topics topics;
  private final Broadcasts broadcasts;
  private final Segments segments;

  /** Reads the API key from {@code MILLIONSEND_API_KEY} and the base URL from the environment. */
  public MillionSend() {
    this(null, null);
  }

  /** Uses the given API key; base URL from {@code MILLIONSEND_BASE_URL} or the default. */
  public MillionSend(String apiKey) {
    this(apiKey, null);
  }

  /**
   * @param apiKey the API key, or {@code null} to read {@code MILLIONSEND_API_KEY}
   * @param baseUrl your instance URL, or {@code null} to read {@code MILLIONSEND_BASE_URL}
   *     then fall back to {@code http://localhost:3001}
   */
  public MillionSend(String apiKey, String baseUrl) {
    this(apiKey, baseUrl, false);
  }

  /**
   * @param allowInsecureHttp accept a plain {@code http://} base URL on a non-loopback host;
   *     off by default because the API key travels as a bearer header
   */
  public MillionSend(String apiKey, String baseUrl, boolean allowInsecureHttp) {
    String key = apiKey != null ? apiKey : System.getenv("MILLIONSEND_API_KEY");
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException(
          "Missing API key. Pass it to new MillionSend(apiKey) or set MILLIONSEND_API_KEY.");
    }
    String url = baseUrl != null ? baseUrl : System.getenv("MILLIONSEND_BASE_URL");
    if (url == null || url.isEmpty()) {
      url = DEFAULT_BASE_URL;
    }
    HttpClient http = new HttpClient(key, url, allowInsecureHttp);
    this.emails = new Emails(http);
    this.batch = new Batch(http);
    this.contacts = new Contacts(http);
    this.topics = new Topics(http);
    this.broadcasts = new Broadcasts(http);
    this.segments = new Segments(http);
  }

  public Emails emails() {
    return emails;
  }

  public Batch batch() {
    return batch;
  }

  public Contacts contacts() {
    return contacts;
  }

  public Topics topics() {
    return topics;
  }

  public Broadcasts broadcasts() {
    return broadcasts;
  }

  public Segments segments() {
    return segments;
  }
}
