package com.millionsend;

/**
 * The single checked exception every MillionSend call throws on a non-2xx
 * response (or a transport failure). Mirrors the API's error body
 * {@code { statusCode, name, message }}: {@link #getName()} is the stable
 * snake_case discriminant (e.g. {@code validation_error}, {@code not_found}),
 * and {@link #getStatusCode()} is {@code null} when the request never reached
 * the API (a client-side or transport failure). {@link #getRetryAfter()} carries
 * the response's {@code retry-after} header.
 */
public class MillionSendException extends Exception {

  // The default the JVM computed before this was declared, so the serialized form is unchanged.
  private static final long serialVersionUID = -5469809787528467192L;

  private final String name;
  private final Integer statusCode;
  private final Integer retryAfter;

  public MillionSendException(String name, String message, Integer statusCode) {
    this(name, message, statusCode, null);
  }

  public MillionSendException(String name, String message, Integer statusCode, Integer retryAfter) {
    super(message);
    this.name = name;
    this.statusCode = statusCode;
    this.retryAfter = retryAfter;
  }

  /** Stable snake_case error code you can switch on. */
  public String getName() {
    return name;
  }

  /** HTTP status; {@code null} when the request never reached the API. */
  public Integer getStatusCode() {
    return statusCode;
  }

  /**
   * Seconds the response's {@code retry-after} header asks to wait before trying again;
   * {@code null} when it sent none, and on client-side or transport failures.
   */
  public Integer getRetryAfter() {
    return retryAfter;
  }
}
