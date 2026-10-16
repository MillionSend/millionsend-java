package com.millionsend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.millionsend.core.RequestOptions;
import com.millionsend.model.AddSuppressionOptions;
import com.millionsend.model.AddSuppressionsOptions;
import com.millionsend.model.Attachment;
import com.millionsend.model.BatchGetContactsResponse;
import com.millionsend.model.BatchValidation;
import com.millionsend.model.ConflictMode;
import com.millionsend.model.Contact;
import com.millionsend.model.ContactAddress;
import com.millionsend.model.ContactInclude;
import com.millionsend.model.ContactProperty;
import com.millionsend.model.DataResponse;
import com.millionsend.model.CreateApiKeyOptions;
import com.millionsend.model.CreateApiKeyResponse;
import com.millionsend.model.CreateBatchContactsResponse;
import com.millionsend.model.CreateBatchEmailsResponse;
import com.millionsend.model.CreateBroadcastOptions;
import com.millionsend.model.CreateContactOptions;
import com.millionsend.model.CreateContactPropertyOptions;
import com.millionsend.model.CreateDomainOptions;
import com.millionsend.model.CreateEmailOptions;
import com.millionsend.model.CreateTemplateOptions;
import com.millionsend.model.CreateTopicOptions;
import com.millionsend.model.CreateWebhookOptions;
import com.millionsend.model.CreateWebhookResponse;
import com.millionsend.model.Domain;
import com.millionsend.model.CreateDraftOptions;
import com.millionsend.model.CreateInboxOptions;
import com.millionsend.model.DeletedResponse;
import com.millionsend.model.ForwardOptions;
import com.millionsend.model.Inbox;
import com.millionsend.model.InboxDraft;
import com.millionsend.model.InboxLabel;
import com.millionsend.model.InboxLabelOptions;
import com.millionsend.model.InboxSafety;
import com.millionsend.model.InboxSafetyOptions;
import com.millionsend.model.InboxThread;
import com.millionsend.model.LatestCodeOptions;
import com.millionsend.model.ListInboxesOptions;
import com.millionsend.model.ListReceivedEmailsOptions;
import com.millionsend.model.ListThreadsOptions;
import com.millionsend.model.QuarantineRelease;
import com.millionsend.model.QuarantinedEmail;
import com.millionsend.model.ReceivedAttachment;
import com.millionsend.model.ReceivedEmail;
import com.millionsend.model.ReceivedEmailListResponse;
import com.millionsend.model.ReplyOptions;
import com.millionsend.model.SearchThreadsOptions;
import com.millionsend.model.SendResult;
import com.millionsend.model.ThreadEmail;
import com.millionsend.model.UpdateDraftOptions;
import com.millionsend.model.UpdateInboxOptions;
import com.millionsend.model.UpdateThreadOptions;
import com.millionsend.model.VerificationCode;
import com.millionsend.model.ListContactsOptions;
import com.millionsend.model.ListOptions;
import com.millionsend.model.ListResponse;
import com.millionsend.model.ListSuppressionsOptions;
import com.millionsend.model.PreferencesLink;
import com.millionsend.model.RemoveContactOptions;
import com.millionsend.model.RemoveContactResponse;
import com.millionsend.model.RemoveContactsOptions;
import com.millionsend.model.RemoveSuppressionsOptions;
import com.millionsend.model.RotateWebhookOptions;
import com.millionsend.model.RotateWebhookResponse;
import com.millionsend.model.Subscription;
import com.millionsend.model.Suppression;
import com.millionsend.model.SuppressionOrigin;
import com.millionsend.model.Tag;
import com.millionsend.model.Template;
import com.millionsend.model.UpdateBroadcastOptions;
import com.millionsend.model.UpdateContactPropertyOptions;
import com.millionsend.model.UpdateDomainOptions;
import com.millionsend.model.UpdateEmailOptions;
import com.millionsend.model.UpdateTemplateOptions;
import com.millionsend.model.UpdateTopicOptions;
import com.millionsend.model.UpdateWebhookOptions;
import com.millionsend.model.UsageReport;
import com.millionsend.model.Webhook;
import com.millionsend.model.WebhookEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Wire parity: every request field reaches the body, the batch-validation
 * header and typed errors, explicit-null clears, and method + path + query +
 * body for each resource added alongside the Resend surface.
 */
class ParityTest {

  private static final ObjectMapper JSON = new ObjectMapper();

  private MockServer server;
  private MillionSend ms;

  @BeforeEach
  void setUp() throws Exception {
    server = new MockServer();
    ms = server.client();
  }

  @AfterEach
  void tearDown() {
    server.close();
  }

  private JsonNode body() throws Exception {
    return JSON.readTree(server.body);
  }

  private static JsonNode json(String s) throws Exception {
    return JSON.readTree(s);
  }

  @Test
  void sendPutsEveryFieldOnTheWire() throws Exception {
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("X-Entity-Ref-ID", "ref-1");
    ms.emails()
        .send(
            CreateEmailOptions.builder()
                .from("Acme <a@x.dev>")
                .to("b@x.dev", "c@x.dev")
                .subject("s")
                .html("<p>h</p>")
                .text("t")
                .cc("cc@x.dev")
                .bcc("bcc@x.dev")
                .replyTo("r@x.dev")
                .scheduledAt("2999-01-01T00:00:00Z")
                .tag(new Tag("k", "v"))
                .addTag(new Tag("k2", "v2"))
                .topicId("11111111-1111-1111-1111-111111111111")
                .attachment(
                    Attachment.builder()
                        .fileName("a.txt")
                        .content("aGk=")
                        .contentType("text/plain")
                        .contentId("cid1")
                        .build())
                .addAttachment(Attachment.builder().fileName("b.pdf").path("https://x.dev/b.pdf").build())
                .headers(headers)
                .header("X-Other", "2")
                .addHeader("X-Third", "3")
                .template(Collections.singletonMap("id", "tpl_1"))
                .build());
    assertEquals("POST", server.method);
    assertEquals("/emails", server.path);
    assertEquals(
        json(
            "{\"from\":\"Acme <a@x.dev>\",\"to\":[\"b@x.dev\",\"c@x.dev\"],\"subject\":\"s\","
                + "\"html\":\"<p>h</p>\",\"text\":\"t\",\"cc\":[\"cc@x.dev\"],\"bcc\":[\"bcc@x.dev\"],"
                + "\"reply_to\":[\"r@x.dev\"],\"scheduled_at\":\"2999-01-01T00:00:00Z\","
                + "\"tags\":[{\"name\":\"k\",\"value\":\"v\"},{\"name\":\"k2\",\"value\":\"v2\"}],"
                + "\"topic_id\":\"11111111-1111-1111-1111-111111111111\","
                + "\"attachments\":[{\"filename\":\"a.txt\",\"content\":\"aGk=\","
                + "\"content_type\":\"text/plain\",\"content_id\":\"cid1\"},"
                + "{\"filename\":\"b.pdf\",\"path\":\"https://x.dev/b.pdf\"}],"
                + "\"headers\":{\"X-Entity-Ref-ID\":\"ref-1\",\"X-Other\":\"2\",\"X-Third\":\"3\"},"
                + "\"template\":{\"id\":\"tpl_1\"}}"),
        body());
  }

  @Test
  void requestOptionsCarryIdempotencyKeyAndExtraHeaders() throws Exception {
    ms.emails()
        .send(
            CreateEmailOptions.builder().from("a@x.dev").to("b@x.dev").subject("s").text("t").build(),
            RequestOptions.builder().setIdempotencyKey("key-1").add("X-Trace", "abc").build());
    assertEquals("key-1", server.header("Idempotency-Key"));
    assertEquals("abc", server.header("X-Trace"));
    assertNull(server.header("x-batch-validation"));
  }

  @Test
  void batchSendsValidationHeaderAndTypesErrors() throws Exception {
    server.responseBody =
        "{\"data\":[{\"id\":\"e1\"}],\"errors\":[{\"index\":1,\"message\":\"emails.1: bad\"}]}";
    CreateBatchEmailsResponse res =
        ms.batch()
            .send(
                Arrays.asList(
                    CreateEmailOptions.builder().from("a@x.dev").to("b@x.dev").subject("1").text("t").build(),
                    CreateEmailOptions.builder().from("a@x.dev").to("bad").subject("2").text("t").build()),
                RequestOptions.builder()
                    .idempotencyKey("batch-1")
                    .batchValidation(BatchValidation.PERMISSIVE)
                    .build());
    assertEquals("POST", server.method);
    assertEquals("/emails/batch", server.path);
    assertEquals("permissive", server.header("x-batch-validation"));
    assertEquals("batch-1", server.header("Idempotency-Key"));
    assertEquals(2, body().size());
    assertEquals("e1", res.getData().get(0).getId());
    assertEquals(1, res.getErrors().get(0).getIndex());
    assertEquals("emails.1: bad", res.getErrors().get(0).getMessage());
    assertNull(res.getErrors().get(0).getStatusCode());
    assertNull(res.getErrors().get(0).getName());

    server.responseBody = "{\"data\":[{\"id\":\"e1\"}]}";
    res = ms.batch().send(Collections.singletonList(
        CreateEmailOptions.builder().from("a@x.dev").to("b@x.dev").subject("1").text("t").build()));
    assertNull(server.header("x-batch-validation"));
    assertNull(res.getErrors());
  }

  @Test
  void emailsListUpdateRemove() throws Exception {
    server.responseBody = "{\"object\":\"list\",\"data\":[],\"has_more\":false}";
    ms.emails().list(ListOptions.builder().limit(5).build());
    assertEquals("GET", server.method);
    assertEquals("/emails", server.path);
    assertEquals("limit=5", server.query);

    server.responseBody = "{\"object\":\"email\",\"id\":\"e1\"}";
    ms.emails().update("e1", UpdateEmailOptions.builder().scheduledAt("2999-01-01T00:00:00Z").build());
    assertEquals("PATCH", server.method);
    assertEquals("/emails/e1", server.path);
    assertEquals(json("{\"scheduled_at\":\"2999-01-01T00:00:00Z\"}"), body());

    ms.emails().remove("e1");
    assertEquals("DELETE", server.method);
    assertEquals("/emails/e1", server.path);
  }

  @Test
  void contactCreateCarriesSegmentsAndTopics() throws Exception {
    ms.contacts()
        .create(
            CreateContactOptions.builder()
                .email("c@x.dev")
                .firstName("Ada")
                .lastName("L")
                .unsubscribed(false)
                .properties(Collections.singletonMap("plan", "pro"))
                .segment("s1")
                .topic("t1", Subscription.OPT_OUT)
                .build());
    assertEquals(
        json(
            "{\"email\":\"c@x.dev\",\"first_name\":\"Ada\",\"last_name\":\"L\",\"unsubscribed\":false,"
                + "\"properties\":{\"plan\":\"pro\"},\"segments\":[{\"id\":\"s1\"}],"
                + "\"topics\":[{\"id\":\"t1\",\"subscription\":\"opt_out\"}]}"),
        body());
  }

  @Test
  void contactPropertiesDecodeAsTypedValues() throws Exception {
    server.responseBody =
        "{\"object\":\"contact\",\"id\":\"c1\",\"email\":\"c@x.dev\",\"first_name\":null,"
            + "\"last_name\":null,\"created_at\":\"2026-01-01T00:00:00.000Z\",\"unsubscribed\":false,"
            + "\"properties\":{\"plan\":{\"type\":\"string\",\"value\":\"pro\"},"
            + "\"seats\":{\"type\":\"number\",\"value\":3}}}";
    Contact c = ms.contacts().get("c1");
    assertEquals("string", c.getProperties().get("plan").getType());
    assertEquals("pro", c.getProperties().get("plan").getValue());
    assertEquals("number", c.getProperties().get("seats").getType());
    assertEquals(3, ((Number) c.getProperties().get("seats").getValue()).intValue());
  }

  @Test
  void contactsBatch() throws Exception {
    server.responseBody =
        "{\"data\":[{\"object\":\"contact\",\"index\":0,\"id\":\"c1\",\"status\":\"created\"}],"
            + "\"counts\":{\"created\":1,\"updated\":0,\"skipped\":0,\"failed\":1},"
            + "\"errors\":[{\"index\":1,\"statusCode\":422,\"name\":\"validation_error\","
            + "\"message\":\"invalid email\"}]}";
    CreateBatchContactsResponse res =
        ms.contacts()
            .batch()
            .create(
                Arrays.asList(
                    CreateContactOptions.builder().email("a@x.dev").build(),
                    CreateContactOptions.builder().email("nope").build()),
                ConflictMode.UPSERT,
                RequestOptions.builder().batchValidation(BatchValidation.PERMISSIVE).build());
    assertEquals("POST", server.method);
    assertEquals("/contacts/batch", server.path);
    assertEquals("on_conflict=upsert", server.query);
    assertEquals("permissive", server.header("x-batch-validation"));
    assertEquals(json("[{\"email\":\"a@x.dev\"},{\"email\":\"nope\"}]"), body());
    assertEquals(0, res.getData().get(0).getIndex());
    assertEquals("created", res.getData().get(0).getStatus());
    assertEquals(1, res.getCounts().getCreated());
    assertEquals(1, res.getCounts().getFailed());
    assertEquals("invalid email", res.getErrors().get(0).getMessage());
    assertEquals(422, res.getErrors().get(0).getStatusCode());
    assertEquals("validation_error", res.getErrors().get(0).getName());

    ms.contacts().batch().create(Collections.singletonList(CreateContactOptions.builder().email("a@x.dev").build()));
    assertNull(server.query);
    assertNull(server.header("x-batch-validation"));
  }

  @Test
  void contactsBatchRemove() throws Exception {
    server.responseBody =
        "{\"data\":[{\"object\":\"contact\",\"contact\":\"c1\",\"deleted\":true}]}";
    DataResponse<RemoveContactResponse> res =
        ms.contacts().batch().remove(RemoveContactsOptions.builder().id("c1").id("c2").build());
    assertEquals("POST", server.method);
    assertEquals("/contacts/batch/remove", server.path);
    assertEquals(json("{\"ids\":[\"c1\",\"c2\"]}"), body());
    assertEquals(1, res.getData().size());
    assertEquals("c1", res.getData().get(0).getContact());
    assertTrue(res.getData().get(0).getDeleted());

    ms.contacts()
        .batch()
        .remove(RemoveContactsOptions.builder().emails(Arrays.asList("a@x.dev", "b@x.dev")).build());
    assertEquals(json("{\"emails\":[\"a@x.dev\",\"b@x.dev\"]}"), body());
    ms.contacts().batch().remove(RemoveContactsOptions.builder().id("c1").erase(true).build());
    assertEquals(json("{\"ids\":[\"c1\"],\"erase\":true}"), body());
    assertThrows(
        IllegalArgumentException.class,
        () -> RemoveContactsOptions.builder().id("x").email("y").build());
    assertThrows(IllegalArgumentException.class, () -> RemoveContactsOptions.builder().build());
  }

  @Test
  void contactsRemoveErase() throws Exception {
    server.responseBody = "{\"object\":\"contact\",\"contact\":\"c1\",\"deleted\":true}";
    RemoveContactResponse res =
        ms.contacts().remove(RemoveContactOptions.builder().email("a@x.dev").erase(true).build());
    assertEquals("DELETE", server.method);
    assertEquals("/contacts/a@x.dev", server.path);
    assertEquals("erase=true", server.query);
    assertTrue(res.getDeleted());

    ms.contacts().remove(RemoveContactOptions.builder().id("c1").build());
    assertEquals("/contacts/c1", server.path);
    assertNull(server.query);

    ms.contacts().remove(ContactAddress.id("c1"));
    assertNull(server.query);
  }

  @Test
  void contactsBatchGet() throws Exception {
    server.responseBody =
        "{\"object\":\"list\",\"data\":[{\"object\":\"contact\",\"id\":\"c1\",\"email\":\"a@x.dev\","
            + "\"first_name\":null,\"last_name\":null,\"created_at\":\"2026-01-01T00:00:00.000Z\","
            + "\"unsubscribed\":false,\"properties\":{\"seats\":{\"type\":\"number\",\"value\":3}},"
            + "\"topics\":[]}],\"missing\":[{\"index\":1,\"email\":\"nobody@x.dev\"}]}";
    BatchGetContactsResponse res =
        ms.contacts()
            .batch()
            .get(
                Arrays.asList(ContactAddress.id("c1"), ContactAddress.email("nobody@x.dev")),
                ContactInclude.PROPERTIES,
                ContactInclude.TOPICS);
    assertEquals("POST", server.method);
    assertEquals("/contacts/batch/get", server.path);
    assertEquals(
        json(
            "{\"contacts\":[{\"id\":\"c1\"},{\"email\":\"nobody@x.dev\"}],"
                + "\"include\":[\"properties\",\"topics\"]}"),
        body());
    assertEquals("list", res.getObject());
    assertEquals(1, res.getData().size());
    Contact c = res.getData().get(0);
    assertEquals("c1", c.getId());
    assertEquals(3, ((Number) c.getProperties().get("seats").getValue()).intValue());
    assertTrue(c.getTopics().isEmpty());
    assertEquals(1, res.getMissing().get(0).getIndex());
    assertEquals("nobody@x.dev", res.getMissing().get(0).getEmail());
    assertNull(res.getMissing().get(0).getId());

    // No include → no key; an address with both set sends the email only.
    ms.contacts()
        .batch()
        .get(Collections.singletonList(ContactAddress.builder().id("c1").email("a@x.dev").build()));
    assertEquals(json("{\"contacts\":[{\"email\":\"a@x.dev\"}]}"), body());
  }

  @Test
  void contactsListInclude() throws Exception {
    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":[{\"id\":\"c1\",\"email\":\"c@x.dev\","
            + "\"first_name\":null,\"last_name\":null,\"created_at\":\"2026-01-01T00:00:00.000Z\","
            + "\"unsubscribed\":false,\"properties\":{\"plan\":{\"type\":\"string\",\"value\":\"pro\"}},"
            + "\"topics\":[{\"id\":\"t1\",\"name\":\"Insights\",\"description\":null,"
            + "\"subscription\":\"opt_out\",\"explicit\":true,\"visibility\":\"public\"}]}]}";
    ListResponse<Contact> res =
        ms.contacts()
            .list(
                ListContactsOptions.builder()
                    .limit(5)
                    .include(ContactInclude.PROPERTIES, ContactInclude.TOPICS)
                    .build());
    assertEquals("GET", server.method);
    assertEquals("/contacts", server.path);
    assertEquals("limit=5&include=properties%2Ctopics", server.query);
    Contact c = res.getData().get(0);
    assertEquals("pro", c.getProperties().get("plan").getValue());
    assertEquals("t1", c.getTopics().get(0).getId());
    assertEquals(Subscription.OPT_OUT, c.getTopics().get(0).getSubscription());
    assertTrue(c.getTopics().get(0).isExplicit());

    server.responseBody = "{\"object\":\"list\",\"data\":[],\"has_more\":false}";
    res = ms.contacts().list(ListContactsOptions.builder().limit(5).build());
    assertEquals("limit=5", server.query);
    assertTrue(res.getData().isEmpty());
  }

  @Test
  void contactPreferencesLink() throws Exception {
    server.responseBody =
        "{\"object\":\"preferences_link\",\"contact\":\"c1\","
            + "\"url\":\"https://app.x.dev/unsubscribe/tok\"}";
    PreferencesLink link = ms.contacts().preferencesLink("c1");
    assertEquals("POST", server.method);
    assertEquals("/contacts/c1/preferences-link", server.path);
    assertEquals("", server.body);
    assertEquals("preferences_link", link.getObject());
    assertEquals("c1", link.getContact());
    assertEquals("https://app.x.dev/unsubscribe/tok", link.getUrl());

    ms.contacts().preferencesLink(ContactAddress.email("ada+1@x.dev"));
    assertEquals("/contacts/ada%2B1%40x.dev/preferences-link", server.rawPath);
  }

  @Test
  void contactSegmentsAddRemove() throws Exception {
    ms.contacts().segments().add("c@x.dev", "s1");
    assertEquals("POST", server.method);
    assertEquals("/contacts/c%40x.dev/segments/s1", server.rawPath);
    assertEquals("", server.body);

    ms.contacts().segments().remove("c1", "s1");
    assertEquals("DELETE", server.method);
    assertEquals("/contacts/c1/segments/s1", server.path);
  }

  @Test
  void broadcastCreateCarriesPreviewSendScheduleAndUpdateClearsTopicWithNull() throws Exception {
    ms.broadcasts()
        .create(
            CreateBroadcastOptions.builder()
                .name("n")
                .segmentId("s1")
                .from("a@x.dev")
                .subject("News")
                .html("<p>hi</p>")
                .text("hi")
                .replyTo("r@x.dev")
                .previewText("pre")
                .topicId("t1")
                .send(true)
                .scheduledAt("in 1 hour")
                .build());
    assertEquals(
        json(
            "{\"name\":\"n\",\"segment_id\":\"s1\",\"from\":\"a@x.dev\",\"subject\":\"News\","
                + "\"html\":\"<p>hi</p>\",\"text\":\"hi\",\"reply_to\":[\"r@x.dev\"],"
                + "\"preview_text\":\"pre\",\"topic_id\":\"t1\",\"send\":true,"
                + "\"scheduled_at\":\"in 1 hour\"}"),
        body());

    ms.broadcasts()
        .update("b1", UpdateBroadcastOptions.builder().previewText("p").topicId(null).build());
    assertEquals("PATCH", server.method);
    assertEquals("/broadcasts/b1", server.path);
    assertEquals(json("{\"preview_text\":\"p\",\"topic_id\":null}"), body());
    assertFalse(body().has("name"));
  }

  @Test
  void topicsCarryVisibilityAndUpdate() throws Exception {
    ms.topics()
        .create(
            CreateTopicOptions.builder()
                .name("Product")
                .defaultSubscription(Subscription.OPT_IN)
                .visibility("public")
                .build());
    assertEquals("public", body().get("visibility").asText());

    ms.topics().update("t1", UpdateTopicOptions.builder().name("Renamed").build());
    assertEquals("PATCH", server.method);
    assertEquals("/topics/t1", server.path);
    assertEquals(json("{\"name\":\"Renamed\"}"), body());
  }

  @Test
  void segmentContacts() throws Exception {
    server.responseBody = "{\"object\":\"list\",\"data\":[],\"has_more\":false}";
    ms.segments().contacts("s1", ListOptions.builder().after("c9").build());
    assertEquals("GET", server.method);
    assertEquals("/segments/s1/contacts", server.path);
    assertEquals("after=c9", server.query);

    ms.segments()
        .contacts("s1", ListContactsOptions.builder().include(ContactInclude.TOPICS).build());
    assertEquals("/segments/s1/contacts", server.path);
    assertEquals("include=topics", server.query);
  }

  @Test
  void suppressions() throws Exception {
    server.responseBody = "{\"object\":\"suppression\",\"id\":\"sup_1\"}";
    ms.suppressions()
        .add(AddSuppressionOptions.builder().email("x@x.dev").origin(SuppressionOrigin.MANUAL).build());
    assertEquals("POST", server.method);
    assertEquals("/suppressions", server.path);
    assertEquals(json("{\"email\":\"x@x.dev\",\"origin\":\"manual\"}"), body());

    server.responseBody =
        "{\"object\":\"suppression\",\"id\":\"sup_1\",\"email\":\"x@x.dev\",\"origin\":\"bounce\","
            + "\"source_id\":\"e1\",\"created_at\":\"2026-01-01T00:00:00.000Z\"}";
    Suppression s = ms.suppressions().get("x@x.dev");
    assertEquals("GET", server.method);
    assertEquals("/suppressions/x%40x.dev", server.rawPath);
    assertEquals(SuppressionOrigin.BOUNCE, s.getOrigin());
    assertEquals("e1", s.getSourceId());

    server.responseBody = "{\"object\":\"list\",\"data\":[],\"has_more\":false}";
    ms.suppressions()
        .list(ListSuppressionsOptions.builder().limit(10).origin(SuppressionOrigin.COMPLAINT).build());
    assertEquals("/suppressions", server.path);
    assertEquals("limit=10&origin=complaint", server.query);

    server.responseBody = "{\"object\":\"suppression\",\"id\":\"sup_1\",\"deleted\":true}";
    ms.suppressions().remove("sup_1");
    assertEquals("DELETE", server.method);
    assertEquals("/suppressions/sup_1", server.path);

    server.responseBody = "{\"data\":[{\"object\":\"suppression\",\"id\":\"sup_1\"}]}";
    ms.suppressions()
        .batch()
        .add(
            AddSuppressionsOptions.builder()
                .emails(Arrays.asList("a@x.dev", "b@x.dev"))
                .origin(SuppressionOrigin.UNSUBSCRIBE)
                .build());
    assertEquals("POST", server.method);
    assertEquals("/suppressions/batch/add", server.path);
    assertEquals(json("{\"emails\":[\"a@x.dev\",\"b@x.dev\"],\"origin\":\"unsubscribe\"}"), body());

    server.responseBody = "{\"data\":[{\"object\":\"suppression\",\"id\":\"sup_1\",\"deleted\":true}]}";
    ms.suppressions().batch().remove(RemoveSuppressionsOptions.builder().id("sup_1").build());
    assertEquals("/suppressions/batch/remove", server.path);
    assertEquals(json("{\"ids\":[\"sup_1\"]}"), body());
    ms.suppressions().batch().remove(RemoveSuppressionsOptions.builder().email("a@x.dev").build());
    assertEquals(json("{\"emails\":[\"a@x.dev\"]}"), body());
    assertThrows(
        IllegalArgumentException.class,
        () -> RemoveSuppressionsOptions.builder().id("x").email("y").build());
  }

  static final String DOMAIN_JSON =
      "{\"object\":\"domain\",\"id\":\"d1\",\"name\":\"x.dev\",\"status\":\"pending\","
          + "\"created_at\":\"2026-01-01T00:00:00.000Z\",\"region\":\"us-east-1\","
          + "\"open_tracking\":false,\"click_tracking\":true,\"tracking_subdomain\":\"links\","
          + "\"capabilities\":{\"sending\":\"enabled\",\"receiving\":\"disabled\"},"
          + "\"records\":[{\"record\":\"DKIM\",\"name\":\"k._domainkey\",\"type\":\"CNAME\","
          + "\"ttl\":\"Auto\",\"status\":\"pending\",\"value\":\"k.dkim.amazonses.com\"},"
          + "{\"record\":\"MX\",\"name\":\"send\",\"type\":\"MX\",\"ttl\":\"Auto\","
          + "\"status\":\"pending\",\"value\":\"feedback-smtp.amazonses.com\",\"priority\":10}]}";

  @Test
  void domains() throws Exception {
    server.responseBody = DOMAIN_JSON;
    Domain d =
        ms.domains()
            .create(
                CreateDomainOptions.builder()
                    .name("x.dev")
                    .region("us-east-1")
                    .customReturnPath("mail")
                    .openTracking(false)
                    .clickTracking(true)
                    .trackingSubdomain("links")
                    .build());
    assertEquals("POST", server.method);
    assertEquals("/domains", server.path);
    assertEquals(
        json(
            "{\"name\":\"x.dev\",\"region\":\"us-east-1\",\"custom_return_path\":\"mail\","
                + "\"open_tracking\":false,\"click_tracking\":true,\"tracking_subdomain\":\"links\"}"),
        body());
    assertEquals("d1", d.getId());
    assertTrue(d.isClickTracking());
    assertEquals("enabled", d.getCapabilities().get("sending"));
    assertEquals(2, d.getRecords().size());
    assertNull(d.getRecords().get(0).getPriority());
    assertEquals(10, d.getRecords().get(1).getPriority());

    ms.domains().get("d1");
    assertEquals("GET", server.method);
    assertEquals("/domains/d1", server.path);

    server.responseBody = "{\"object\":\"list\",\"data\":[],\"has_more\":false}";
    ms.domains().list(ListOptions.builder().limit(3).build());
    assertEquals("/domains", server.path);
    assertEquals("limit=3", server.query);

    server.responseBody = DOMAIN_JSON;
    ms.domains().verify("d1");
    assertEquals("POST", server.method);
    assertEquals("/domains/d1/verify", server.path);

    ms.domains()
        .update(UpdateDomainOptions.builder().id("d1").openTracking(true).trackingSubdomain(null).build());
    assertEquals("PATCH", server.method);
    assertEquals("/domains/d1", server.path);
    assertEquals(json("{\"open_tracking\":true,\"tracking_subdomain\":null}"), body());
    assertFalse(body().has("click_tracking"));

    server.responseBody = "{\"object\":\"domain\",\"id\":\"d1\",\"deleted\":true}";
    ms.domains().remove("d1");
    assertEquals("DELETE", server.method);
    assertEquals("/domains/d1", server.path);
  }

  static final String SHARED_DOMAIN_JSON =
      "{\"object\":\"domain\",\"id\":\"sd1\",\"name\":\"acme.agents.dev\",\"status\":\"verified\","
          + "\"created_at\":\"2026-10-01T00:00:00.000Z\",\"region\":\"us-east-1\","
          + "\"open_tracking\":false,\"click_tracking\":false,\"tracking_subdomain\":null,"
          + "\"capabilities\":{\"sending\":\"enabled\",\"receiving\":\"enabled\"},"
          + "\"receiving_catch_all\":false,\"shared\":true,\"sending_mode\":\"reply_only\","
          + "\"sending_mode_reason\":\"plan\",\"records\":[]}";

  @Test
  void sharedDomainRow() throws Exception {
    server.responseBody = SHARED_DOMAIN_JSON;
    Domain shared = ms.domains().get("sd1");
    assertEquals("domain", shared.getObject());
    assertTrue(shared.isShared());
    assertEquals("verified", shared.getStatus());
    assertEquals("reply_only", shared.getSendingMode());
    assertEquals("plan", shared.getSendingModeReason());
    assertEquals("enabled", shared.getCapabilities().get("receiving"));
    assertFalse(shared.getReceivingCatchAll());
    assertNull(shared.getTrackingSubdomain());
    assertTrue(shared.getRecords().isEmpty());

    // A server before v0.14.0 sends no shared key; own domains never carry a sending mode.
    server.responseBody = DOMAIN_JSON;
    Domain own = ms.domains().get("d1");
    assertFalse(own.isShared());
    assertNull(own.getSendingMode());
    assertNull(own.getSendingModeReason());

    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":["
            + DOMAIN_JSON.replace("\"records\"", "\"shared\":false,\"records\"") + ","
            + SHARED_DOMAIN_JSON
                .replace("reply_only", "a_mode_from_a_newer_server")
                .replace("\"plan\"", "null")
            + "]}";
    ListResponse<Domain> list = ms.domains().list();
    assertFalse(list.getData().get(0).isShared());
    Domain last = list.getData().get(1);
    assertTrue(last.isShared());
    assertEquals("a_mode_from_a_newer_server", last.getSendingMode());
    assertNull(last.getSendingModeReason());

    server.status = 403;
    server.responseBody =
        "{\"statusCode\":403,\"name\":\"forbidden\",\"message\":\"acme.agents.dev is your team's "
            + "subdomain of the shared domain: it can't be changed or deleted\"}";
    MillionSendException e =
        assertThrows(
            MillionSendException.class,
            () -> ms.domains().update(UpdateDomainOptions.builder().id("sd1").openTracking(true).build()));
    assertEquals("forbidden", e.getName());
    assertEquals(403, e.getStatusCode());
  }

  @Test
  void webhooks() throws Exception {
    server.responseBody = "{\"object\":\"webhook\",\"id\":\"w1\",\"signing_secret\":\"whsec_abc\"}";
    CreateWebhookResponse created =
        ms.webhooks()
            .create(
                CreateWebhookOptions.builder()
                    .endpoint("https://x.dev/hook")
                    .events(WebhookEvent.EMAIL_DELIVERED, WebhookEvent.QUOTA_REACHED)
                    .signingSecret("whsec_abc")
                    .build());
    assertEquals("POST", server.method);
    assertEquals("/webhooks", server.path);
    assertEquals(
        json(
            "{\"endpoint\":\"https://x.dev/hook\",\"events\":[\"email.delivered\",\"quota.reached\"],"
                + "\"signing_secret\":\"whsec_abc\"}"),
        body());
    assertEquals("whsec_abc", created.getSigningSecret());

    server.responseBody =
        "{\"object\":\"webhook\",\"id\":\"w1\",\"endpoint\":\"https://x.dev/hook\","
            + "\"created_at\":\"2026-01-01T00:00:00.000Z\",\"status\":\"enabled\","
            + "\"events\":[\"email.delivered\"],\"signing_secret\":\"whsec_abc\","
            + "\"previous_secret_expires_at\":\"2026-01-02T00:00:00.000Z\"}";
    Webhook w = ms.webhooks().get("w1");
    assertEquals("GET", server.method);
    assertEquals("/webhooks/w1", server.path);
    assertEquals("whsec_abc", w.getSigningSecret());
    assertEquals("email.delivered", w.getEvents().get(0));
    assertEquals("2026-01-02T00:00:00.000Z", w.getPreviousSecretExpiresAt());

    server.responseBody = "{\"object\":\"list\",\"data\":[],\"has_more\":false}";
    ms.webhooks().list();
    assertEquals("/webhooks", server.path);

    server.responseBody = "{\"object\":\"webhook\",\"id\":\"w1\"}";
    ms.webhooks()
        .update("w1", UpdateWebhookOptions.builder().status("disabled").events(WebhookEvent.EMAIL_SENT).build());
    assertEquals("PATCH", server.method);
    assertEquals("/webhooks/w1", server.path);
    assertEquals(json("{\"events\":[\"email.sent\"],\"status\":\"disabled\"}"), body());

    server.responseBody = "{\"object\":\"webhook\",\"id\":\"w1\",\"deleted\":true}";
    ms.webhooks().remove("w1");
    assertEquals("DELETE", server.method);
    assertEquals("/webhooks/w1", server.path);
  }

  @Test
  void webhookRotate() throws Exception {
    server.responseBody =
        "{\"object\":\"webhook\",\"id\":\"w1\",\"signing_secret\":\"whsec_new\","
            + "\"previous_secret_expires_at\":\"2026-01-02T00:00:00.000Z\"}";
    RotateWebhookResponse r = ms.webhooks().rotate("w1");
    assertEquals("POST", server.method);
    assertEquals("/webhooks/w1/rotate", server.path);
    assertEquals(json("{}"), body());
    assertEquals("whsec_new", r.getSigningSecret());
    assertEquals("2026-01-02T00:00:00.000Z", r.getPreviousSecretExpiresAt());

    server.responseBody =
        "{\"object\":\"webhook\",\"id\":\"w1\",\"signing_secret\":\"whsec_mine\","
            + "\"previous_secret_expires_at\":null}";
    r =
        ms.webhooks()
            .rotate("w1", RotateWebhookOptions.builder().signingSecret("whsec_mine").overlapHours(0).build());
    assertEquals(json("{\"signing_secret\":\"whsec_mine\",\"overlap_hours\":0}"), body());
    assertNull(r.getPreviousSecretExpiresAt());

    ms.webhooks().rotate("w1", RotateWebhookOptions.builder().overlapHours(48).build());
    assertEquals(json("{\"overlap_hours\":48}"), body());
  }

  @Test
  void apiKeys() throws Exception {
    server.responseBody = "{\"id\":\"k1\",\"token\":\"ms_secret\"}";
    CreateApiKeyResponse created =
        ms.apiKeys()
            .create(
                CreateApiKeyOptions.builder()
                    .name("ci")
                    .permission("sending_access")
                    .domainId("d1")
                    .build());
    assertEquals("POST", server.method);
    assertEquals("/api-keys", server.path);
    assertEquals(
        json("{\"name\":\"ci\",\"permission\":\"sending_access\",\"domain_id\":\"d1\"}"), body());
    assertEquals("ms_secret", created.getToken());

    server.responseBody =
        "{\"object\":\"list\",\"data\":[{\"id\":\"k1\",\"name\":\"ci\","
            + "\"created_at\":\"2026-01-01T00:00:00.000Z\",\"last_used_at\":null}],\"has_more\":false}";
    assertNull(ms.apiKeys().list().getData().get(0).getLastUsedAt());
    assertEquals("GET", server.method);
    assertEquals("/api-keys", server.path);

    server.responseBody = "{\"object\":\"api_key\",\"id\":\"k1\",\"deleted\":true}";
    ms.apiKeys().remove("k1");
    assertEquals("DELETE", server.method);
    assertEquals("/api-keys/k1", server.path);
  }

  @Test
  void templates() throws Exception {
    server.responseBody = "{\"object\":\"template\",\"id\":\"t1\"}";
    ms.templates()
        .create(
            CreateTemplateOptions.builder()
                .name("Welcome")
                .html("<p>hi</p>")
                .subject("Hi")
                .text("hi")
                .alias("welcome")
                .from("Acme <a@x.dev>")
                .replyTo("r@x.dev")
                .variables(Collections.singletonList(Collections.singletonMap("key", "name")))
                .build());
    assertEquals("POST", server.method);
    assertEquals("/templates", server.path);
    assertEquals(
        json(
            "{\"name\":\"Welcome\",\"html\":\"<p>hi</p>\",\"subject\":\"Hi\",\"text\":\"hi\","
                + "\"alias\":\"welcome\",\"from\":\"Acme <a@x.dev>\",\"reply_to\":[\"r@x.dev\"],"
                + "\"variables\":[{\"key\":\"name\"}]}"),
        body());

    server.responseBody =
        "{\"object\":\"template\",\"id\":\"t1\",\"name\":\"Welcome\",\"alias\":\"welcome\","
            + "\"status\":\"published\",\"published_at\":\"2026-01-01T00:00:00.000Z\","
            + "\"created_at\":\"2026-01-01T00:00:00.000Z\",\"updated_at\":\"2026-01-01T00:00:00.000Z\","
            + "\"current_version_id\":\"v1\",\"from\":null,\"subject\":\"Hi\",\"reply_to\":null,"
            + "\"html\":\"<p>hi</p>\",\"text\":null,\"variables\":[],\"has_unpublished_versions\":false}";
    Template t = ms.templates().get("welcome");
    assertEquals("GET", server.method);
    assertEquals("/templates/welcome", server.path);
    assertEquals("v1", t.getCurrentVersionId());
    assertEquals("<p>hi</p>", t.getHtml());
    assertTrue(t.getVariables().isEmpty());

    server.responseBody = "{\"object\":\"list\",\"data\":[],\"has_more\":false}";
    ms.templates().list(ListOptions.builder().before("t0").build());
    assertEquals("/templates", server.path);
    assertEquals("before=t0", server.query);

    server.responseBody = "{\"object\":\"template\",\"id\":\"t1\"}";
    ms.templates()
        .update(
            "t1",
            UpdateTemplateOptions.builder()
                .html("<p>new</p>")
                .subject(null)
                .alias(null)
                .variables(Collections.emptyList())
                .build());
    assertEquals("PATCH", server.method);
    assertEquals("/templates/t1", server.path);
    assertEquals(
        json("{\"html\":\"<p>new</p>\",\"subject\":null,\"alias\":null,\"variables\":[]}"), body());

    ms.templates().publish("t1");
    assertEquals("POST", server.method);
    assertEquals("/templates/t1/publish", server.path);

    ms.templates().duplicate("t1");
    assertEquals("/templates/t1/duplicate", server.path);

    server.responseBody = "{\"object\":\"template\",\"id\":\"t1\",\"deleted\":true}";
    ms.templates().remove("welcome");
    assertEquals("DELETE", server.method);
    assertEquals("/templates/welcome", server.path);
  }

  @Test
  void contactProperties() throws Exception {
    server.responseBody =
        "{\"object\":\"contact_property\",\"id\":\"p1\",\"created_at\":\"2026-01-01T00:00:00.000Z\","
            + "\"key\":\"plan\",\"type\":\"string\",\"fallback_value\":\"free\"}";
    ContactProperty p =
        ms.contactProperties()
            .create(
                CreateContactPropertyOptions.builder().key("plan").type("string").fallbackValue("free").build());
    assertEquals("POST", server.method);
    assertEquals("/contact-properties", server.path);
    assertEquals(json("{\"key\":\"plan\",\"type\":\"string\",\"fallback_value\":\"free\"}"), body());
    assertEquals("free", p.getFallbackValue());

    server.responseBody =
        "{\"object\":\"contact_property\",\"id\":\"p2\",\"created_at\":\"2026-01-01T00:00:00.000Z\","
            + "\"key\":\"seats\",\"type\":\"number\",\"fallback_value\":null}";
    assertNull(ms.contactProperties().get("p2").getFallbackValue());
    assertEquals("GET", server.method);
    assertEquals("/contact-properties/p2", server.path);

    server.responseBody = "{\"object\":\"list\",\"data\":[],\"has_more\":false}";
    ms.contactProperties().list(ListOptions.builder().limit(50).build());
    assertEquals("/contact-properties", server.path);
    assertEquals("limit=50", server.query);

    server.responseBody = "{\"object\":\"contact_property\",\"id\":\"p1\"}";
    ms.contactProperties().update(UpdateContactPropertyOptions.builder().id("p1").fallbackValue(null).build());
    assertEquals("PATCH", server.method);
    assertEquals("/contact-properties/p1", server.path);
    assertEquals(json("{\"fallback_value\":null}"), body());

    server.responseBody = "{\"object\":\"contact_property\",\"id\":\"p1\",\"deleted\":true}";
    ms.contactProperties().remove("p1");
    assertEquals("DELETE", server.method);
    assertEquals("/contact-properties/p1", server.path);
  }

  @Test
  void usage() throws Exception {
    server.responseBody =
        "{\"object\":\"usage\",\"cloud\":true,\"plan\":\"pro\","
            + "\"limits\":{\"emails_per_day\":50000,\"domains\":10},"
            + "\"today\":{\"emails_sent\":123,\"resets_at\":\"2026-01-02T00:00:00.000Z\"},"
            + "\"team\":{\"id\":\"team_1\",\"name\":\"Acme\"},\"app_url\":\"https://app.millionsend.com\"}";
    UsageReport u = ms.usage().get();
    assertEquals("GET", server.method);
    assertEquals("/usage", server.path);
    assertTrue(u.isCloud());
    assertEquals("pro", u.getPlan());
    assertEquals(50000, u.getLimits().getEmailsPerDay());
    assertEquals(123, u.getToday().getEmailsSent());
    assertEquals("Acme", u.getTeam().getName());

    server.responseBody =
        "{\"object\":\"usage\",\"cloud\":false,\"plan\":null,"
            + "\"limits\":{\"emails_per_day\":null,\"domains\":null},"
            + "\"today\":{\"emails_sent\":0,\"resets_at\":\"2026-01-02T00:00:00.000Z\"},"
            + "\"team\":{\"id\":\"team_1\",\"name\":\"Acme\"},\"app_url\":null}";
    u = ms.usage().get();
    assertNull(u.getPlan());
    assertNull(u.getLimits().getEmailsPerDay());
  }
  // ---- receiving --------------------------------------------------------

  static final String RECEIVED_JSON =
      "{\"object\":\"email\",\"id\":\"r1\",\"to\":[\"bot@x.dev\"],\"from\":\"ada@y.dev\","
          + "\"created_at\":\"2026-10-12T10:00:00.000Z\",\"subject\":\"Your code\",\"bcc\":[],\"cc\":[],"
          + "\"reply_to\":[],\"received_for\":[\"bot@x.dev\"],\"message_id\":\"<m1@y.dev>\","
          + "\"attachments\":[{\"id\":\"a1\",\"filename\":\"a.pdf\",\"content_type\":\"application/pdf\","
          + "\"content_id\":null,\"content_disposition\":\"attachment\",\"size\":42}],"
          + "\"html\":\"<p>482913</p>\",\"html_format\":\"cid\",\"text\":\"482913\","
          + "\"headers\":{\"from\":\"Ada <ada@y.dev>\"},"
          + "\"authentication\":{\"spf\":\"pass\",\"dkim\":\"gray\",\"dmarc\":\"pass\"},"
          + "\"raw\":{\"download_url\":\"https://api.x.dev/raw/r1\",\"expires_at\":\"2026-10-12T11:00:00.000Z\"},"
          + "\"inbox_id\":\"i1\",\"thread_id\":\"t1\","
          + "\"inboxes\":[{\"inbox_id\":\"i1\",\"thread_id\":\"t1\",\"verdict\":\"clean\"}],"
          + "\"screening\":{\"verdict\":\"clean\",\"sender_trust\":\"external\",\"quarantine_reason\":null,"
          + "\"reasons\":[\"targets_agent\"],\"injection_score\":3,\"phishing_score\":null,"
          + "\"hidden_text_removed\":true},"
          + "\"spam_verdict\":\"pass\",\"spam_score\":4,\"virus_verdict\":\"pass\",\"virus_signature\":null,"
          + "\"extracted_text\":\"Your code is 482913\","
          + "\"extracted\":{\"kinds\":[\"code\"],\"code\":\"482913\",\"link\":null,"
          + "\"link_domain_matches_sender\":null,\"ambiguous\":false},\"read\":false,"
          + "\"a_field_from_a_newer_server\":{\"x\":1}}";

  static final String DRAFT_JSON =
      "{\"object\":\"inbox_draft\",\"id\":\"d1\",\"type\":\"reply\",\"to\":[\"ada@y.dev\"],\"cc\":[],"
          + "\"bcc\":[],\"subject\":\"Re: Hi\",\"html\":null,\"text\":\"Thanks\",\"thread_id\":\"t1\","
          + "\"reply_to_email_id\":\"r1\",\"email_id\":null,\"created_at\":\"2026-10-12T10:00:00.000Z\","
          + "\"updated_at\":\"2026-10-12T10:00:00.000Z\",\"status\":\"pending_approval\","
          + "\"approval_reasons\":[\"send_policy\",\"ramp\"],\"forward\":false,\"decided_by\":null,"
          + "\"decided_at\":null,\"expires_at\":\"2026-10-19T10:00:00.000Z\"}";

  @Test
  void receivingListCarriesEveryFilterAndHeldIds() throws Exception {
    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":[" + RECEIVED_JSON + "],"
            + "\"held\":[{\"id\":\"r2\",\"inbox_id\":\"i1\",\"reason\":\"quarantined\","
            + "\"quarantine_reason\":\"not_allowlisted\",\"created_at\":\"2026-10-12T10:00:01.000Z\"}]}";
    ReceivedEmailListResponse res =
        ms.receiving()
            .list(
                ListReceivedEmailsOptions.builder()
                    .limit(5)
                    .after("r0")
                    .waitSeconds(20)
                    .since("2026-10-12T10:00:00Z")
                    .inboxId("i1")
                    .to("bot@x.dev")
                    .from("y.dev")
                    .subjectContains("code")
                    .unread(true)
                    .includeQuarantined(true)
                    .quarantined(true)
                    .build());
    assertEquals("GET", server.method);
    assertEquals("/emails/receiving", server.path);
    assertEquals(
        "limit=5&after=r0&wait=20&since=2026-10-12T10%3A00%3A00Z&inbox_id=i1&to=bot%40x.dev"
            + "&from=y.dev&subject_contains=code&unread=true&include_quarantined=true&quarantined=true",
        server.query);
    assertEquals("r2", res.getHeld().get(0).getId());
    assertEquals("quarantined", res.getHeld().get(0).getReason());
    assertEquals("not_allowlisted", res.getHeld().get(0).getQuarantineReason());
    ReceivedEmail e = res.getData().get(0);
    assertEquals("clean", e.getScreening().getVerdict());
    assertEquals("external", e.getScreening().getSenderTrust());

    server.responseBody = "{\"object\":\"list\",\"has_more\":false,\"data\":[]}";
    res = ms.receiving().list();
    assertNull(server.query);
    assertNull(res.getHeld());
    ms.receiving()
        .list(ListReceivedEmailsOptions.builder().includeQuarantined(false).quarantined(false).build());
    assertNull(server.query);

    // An inbox-scoped key reads its own inbox's unread mail without naming it.
    ms.receiving().list(ListReceivedEmailsOptions.builder().unread(false).build());
    assertEquals("unread=false", server.query);

    server.status = 404;
    server.responseBody = "{\"statusCode\":404,\"name\":\"not_found\",\"message\":\"Inbox not found.\"}";
    MillionSendException missing =
        assertThrows(
            MillionSendException.class,
            () -> ms.receiving().list(ListReceivedEmailsOptions.builder().inboxId("i9").waitSeconds(30).build()));
    assertEquals("not_found", missing.getName());
    assertEquals(404, missing.getStatusCode());
  }

  @Test
  void receivingGetDecodesTheExtensionFields() throws Exception {
    server.responseBody = RECEIVED_JSON;
    ReceivedEmail e = ms.receiving().get("r1", "cid");
    assertEquals("GET", server.method);
    assertEquals("/emails/receiving/r1", server.path);
    assertEquals("html_format=cid", server.query);
    assertEquals("cid", e.getHtmlFormat());
    assertEquals("pass", e.getAuthentication().get("spf"));
    assertEquals("https://api.x.dev/raw/r1", e.getRaw().getDownloadUrl());
    assertEquals("Ada <ada@y.dev>", e.getHeaders().get("from"));
    assertEquals("attachment", e.getAttachments().get(0).getContentDisposition());
    assertEquals(42L, e.getAttachments().get(0).getSize());
    assertEquals("i1", e.getInboxId());
    assertEquals("clean", e.getInboxes().get(0).getVerdict());
    assertEquals(3, e.getScreening().getInjectionScore());
    assertNull(e.getScreening().getPhishingScore());
    assertTrue(e.getScreening().getHiddenTextRemoved());
    assertEquals("targets_agent", e.getScreening().getReasons().get(0));
    assertEquals(4, e.getSpamScore());
    assertEquals("482913", e.getExtracted().getCode());
    assertEquals("code", e.getExtracted().getKinds().get(0));
    assertFalse(e.getExtracted().getAmbiguous());
    assertEquals("Your code is 482913", e.getExtractedText());
    assertFalse(e.getRead());

    ms.receiving().get("r1");
    assertNull(server.query);
  }

  @Test
  void receivingReplySentOrHeld() throws Exception {
    server.responseBody = "{\"object\":\"email\",\"id\":\"e9\",\"thread_id\":\"t1\",\"inbox_id\":\"i1\"}";
    SendResult<ThreadEmail> sent =
        ms.receiving()
            .reply(
                "r1",
                ReplyOptions.builder()
                    .text("Thanks")
                    .html("<p>Thanks</p>")
                    .subject("Re: Hi")
                    .cc("cc@x.dev")
                    .bcc("bcc@x.dev")
                    .replyAll(true)
                    .requiresApproval(false)
                    .from("bot@x.dev")
                    .inboxId("i1")
                    .build(),
                RequestOptions.idempotencyKey("reply-1"));
    assertEquals("POST", server.method);
    assertEquals("/emails/receiving/r1/reply", server.path);
    assertEquals("reply-1", server.header("Idempotency-Key"));
    assertEquals(
        json(
            "{\"text\":\"Thanks\",\"html\":\"<p>Thanks</p>\",\"subject\":\"Re: Hi\",\"cc\":[\"cc@x.dev\"],"
                + "\"bcc\":[\"bcc@x.dev\"],\"reply_all\":true,\"requires_approval\":false,"
                + "\"from\":\"bot@x.dev\",\"inbox_id\":\"i1\"}"),
        body());
    assertFalse(sent.isDraft());
    assertEquals("sent", sent.getKind());
    assertEquals("e9", sent.getSent().getId());
    assertEquals("t1", sent.getSent().getThreadId());
    assertNull(sent.getDraft());

    server.status = 202;
    server.responseBody = DRAFT_JSON;
    SendResult<ThreadEmail> held = ms.receiving().reply("r1", ReplyOptions.builder().text("Thanks").build());
    assertNull(server.header("Idempotency-Key"));
    assertEquals(json("{\"text\":\"Thanks\"}"), body());
    assertTrue(held.isDraft());
    assertEquals("draft", held.getKind());
    assertNull(held.getSent());
    assertEquals("pending_approval", held.getDraft().getStatus());
    assertEquals(Arrays.asList("send_policy", "ramp"), held.getDraft().getApprovalReasons());
  }

  @Test
  void receivingForwardAndRemove() throws Exception {
    server.responseBody = "{\"object\":\"email\",\"id\":\"e9\",\"thread_id\":null,\"inbox_id\":null}";
    SendResult<ThreadEmail> res =
        ms.receiving()
            .forward(
                "r1",
                ForwardOptions.builder()
                    .to("ops@x.dev")
                    .cc("cc@x.dev")
                    .bcc("bcc@x.dev")
                    .text("FYI")
                    .html("<p>FYI</p>")
                    .subject("Fwd: Hi")
                    .requiresApproval(true)
                    .from("bot@x.dev")
                    .inboxId("i1")
                    .mode("attachment")
                    .build(),
                RequestOptions.idempotencyKey("fwd-1"));
    assertEquals("POST", server.method);
    assertEquals("/emails/receiving/r1/forward", server.path);
    assertEquals("fwd-1", server.header("Idempotency-Key"));
    assertEquals(
        json(
            "{\"to\":[\"ops@x.dev\"],\"cc\":[\"cc@x.dev\"],\"bcc\":[\"bcc@x.dev\"],\"text\":\"FYI\","
                + "\"html\":\"<p>FYI</p>\",\"subject\":\"Fwd: Hi\",\"requires_approval\":true,"
                + "\"from\":\"bot@x.dev\",\"inbox_id\":\"i1\",\"mode\":\"attachment\"}"),
        body());
    assertFalse(res.isDraft());
    assertNull(res.getSent().getThreadId());

    server.responseBody = "{\"object\":\"email\",\"id\":\"r1\",\"deleted\":true}";
    DeletedResponse removed = ms.receiving().remove("r1");
    assertEquals("DELETE", server.method);
    assertEquals("/emails/receiving/r1", server.path);
    assertTrue(removed.getDeleted());
  }

  @Test
  void receivingAttachments() throws Exception {
    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":[{\"id\":\"a1\",\"filename\":\"a.pdf\","
            + "\"size\":42,\"content_type\":\"application/pdf\",\"content_disposition\":\"attachment\","
            + "\"download_url\":\"https://api.x.dev/dl/a1\",\"expires_at\":\"2026-10-12T11:00:00.000Z\"}]}";
    ListResponse<ReceivedAttachment> list = ms.receiving().attachments().list("r1");
    assertEquals("GET", server.method);
    assertEquals("/emails/receiving/r1/attachments", server.path);
    assertNull(server.query);
    assertEquals("https://api.x.dev/dl/a1", list.getData().get(0).getDownloadUrl());

    ms.receiving().attachments().list("r1", ListOptions.builder().limit(2).build());
    assertEquals("limit=2", server.query);

    server.responseBody =
        "{\"object\":\"attachment\",\"id\":\"a1\",\"size\":42,\"content_type\":\"application/pdf\","
            + "\"content_disposition\":\"inline\",\"content_id\":\"cid-1\","
            + "\"download_url\":\"https://api.x.dev/dl/a1\",\"expires_at\":\"2026-10-12T11:00:00.000Z\"}";
    ReceivedAttachment a = ms.receiving().attachments().get("r1", "a1");
    assertEquals("/emails/receiving/r1/attachments/a1", server.path);
    assertEquals("attachment", a.getObject());
    assertEquals("cid-1", a.getContentId());
    assertNull(a.getFilename());
  }

  // ---- inboxes ----------------------------------------------------------

  static final String INBOX_JSON =
      "{\"object\":\"inbox\",\"id\":\"i1\",\"name\":\"Signup bot\",\"email_address\":\"bot-7f3k@acme.agents.dev\","
          + "\"domain_id\":\"sd1\",\"receiving_address\":null,\"from_name\":null,\"unread\":2,\"drafts\":1,"
          + "\"last_received\":null,\"created_at\":\"2026-10-12T10:00:00.000Z\",\"kind\":\"agent\","
          + "\"shared\":true,\"expires_at\":null,\"quarantined\":3,"
          + "\"pending_approval\":1}";

  @Test
  void inboxesCrud() throws Exception {
    server.status = 201;
    server.responseBody = INBOX_JSON;
    Inbox created =
        ms.inboxes()
            .create(
                CreateInboxOptions.builder()
                    .emailAddress("bot@x.dev")
                    .name("Signup bot")
                    .fromName("Acme")
                    .forwarding(false)
                    .kind("agent")
                    .expiresIn("24h")
                    .safety(InboxSafetyOptions.builder().acceptFrom("anyone").sendCap(null).build())
                    .build());
    assertEquals("POST", server.method);
    assertEquals("/inboxes", server.path);
    assertEquals(
        json(
            "{\"email_address\":\"bot@x.dev\",\"name\":\"Signup bot\",\"from_name\":\"Acme\","
                + "\"forwarding\":false,\"kind\":\"agent\",\"expires_in\":\"24h\","
                + "\"safety\":{\"accept_from\":\"anyone\",\"send_cap\":null}}"),
        body());
    assertEquals("i1", created.getId());
    assertEquals("agent", created.getKind());
    assertTrue(created.getShared());
    assertEquals("sd1", created.getDomainId());
    assertNull(created.getExpiresAt());
    assertEquals(3, created.getQuarantined());
    assertEquals(1, created.getPendingApproval());
    assertEquals(2, created.getUnread());

    server.status = 200;
    ms.inboxes().create(CreateInboxOptions.builder().build());
    assertEquals(json("{}"), body());
    ms.inboxes().create(CreateInboxOptions.builder().username("signup-bot").build());
    assertEquals(json("{\"username\":\"signup-bot\"}"), body());
    ms.inboxes().create(CreateInboxOptions.builder().domainId("sd1").build());
    assertEquals(json("{\"domain_id\":\"sd1\"}"), body());
    ms.inboxes().create(CreateInboxOptions.builder().domainId("d1").username("support").build());
    assertEquals(json("{\"domain_id\":\"d1\",\"username\":\"support\"}"), body());

    server.responseBody =
        "{\"object\":\"list\",\"has_more\":true,\"data\":[{\"id\":\"i1\",\"name\":null,"
            + "\"email_address\":\"bot@x.dev\",\"from_name\":null,\"unread\":0,\"last_received\":null,"
            + "\"kind\":\"human\",\"shared\":false,\"expires_at\":null}]}";
    ListResponse<Inbox> list =
        ms.inboxes().list(ListInboxesOptions.builder().limit(10).before("i0").kind("human").build());
    assertEquals("GET", server.method);
    assertEquals("/inboxes", server.path);
    assertEquals("limit=10&before=i0&kind=human", server.query);
    assertTrue(list.isHasMore());
    assertEquals("human", list.getData().get(0).getKind());
    assertNull(list.getData().get(0).getDrafts());

    server.responseBody = INBOX_JSON;
    ms.inboxes().get("Bot+1@x.dev");
    assertEquals("/inboxes/Bot%2B1%40x.dev", server.rawPath);

    ms.inboxes()
        .update("i1", UpdateInboxOptions.builder().name("Bot").fromName("").expiresIn(null).build());
    assertEquals("PATCH", server.method);
    assertEquals("/inboxes/i1", server.path);
    assertEquals(json("{\"name\":\"Bot\",\"from_name\":\"\",\"expires_in\":null}"), body());

    server.responseBody = "{\"object\":\"inbox\",\"id\":\"i1\",\"deleted\":true}";
    assertTrue(ms.inboxes().remove("i1").getDeleted());
    assertEquals("DELETE", server.method);
    assertEquals("/inboxes/i1", server.path);
  }

  @Test
  void inboxSafety() throws Exception {
    server.responseBody =
        "{\"object\":\"inbox_safety\",\"inbox_id\":\"i1\",\"accept_from\":\"verified\",\"allowlist\":[\"*@y.dev\"],"
            + "\"owner_addresses\":[],\"screening\":true,\"extract_codes\":true,\"send_policy\":\"reply_only\","
            + "\"require_approval\":false,\"private_codes\":true,\"send_cap\":null,\"forward_to\":[]}";
    InboxSafety safety = ms.inboxes().safety().get("i1");
    assertEquals("GET", server.method);
    assertEquals("/inboxes/i1/safety", server.path);
    assertEquals("verified", safety.getAcceptFrom());
    assertEquals("*@y.dev", safety.getAllowlist().get(0));
    assertNull(safety.getSendCap());
    assertTrue(safety.getPrivateCodes());

    ms.inboxes()
        .safety()
        .update(
            "i1",
            InboxSafetyOptions.builder()
                .acceptFrom("allowlist")
                .allowlist("ada@y.dev", "*@z.dev")
                .ownerAddresses(Collections.singletonList("me@x.dev"))
                .screening(true)
                .extractCodes(false)
                .sendPolicy("allowlist")
                .requireApproval(true)
                .privateCodes(true)
                .sendCap(50)
                .forwardTo(Collections.singletonList("me@x.dev"))
                .build());
    assertEquals("PATCH", server.method);
    assertEquals("/inboxes/i1/safety", server.path);
    assertEquals(
        json(
            "{\"accept_from\":\"allowlist\",\"allowlist\":[\"ada@y.dev\",\"*@z.dev\"],"
                + "\"owner_addresses\":[\"me@x.dev\"],\"screening\":true,\"extract_codes\":false,"
                + "\"send_policy\":\"allowlist\",\"require_approval\":true,\"private_codes\":true,"
                + "\"send_cap\":50,\"forward_to\":[\"me@x.dev\"]}"),
        body());

    ms.inboxes().safety().update("i1", InboxSafetyOptions.builder().sendCap(null).build());
    assertEquals(json("{\"send_cap\":null}"), body());
  }

  @Test
  void inboxCodesAndQuarantine() throws Exception {
    server.responseBody =
        "{\"object\":\"verification_code\",\"status\":\"found\",\"kind\":\"otp\",\"code\":\"482913\","
            + "\"url\":null,\"link_domain_matches_sender\":true,\"email_id\":\"r1\",\"from\":\"ada@y.dev\","
            + "\"received_at\":\"2026-10-12T10:00:00.000Z\",\"sender_trust\":\"external\"}";
    VerificationCode code =
        ms.inboxes()
            .codes()
            .latest("i1", LatestCodeOptions.builder().from("y.dev").waitSeconds(10).maxAge(600).build());
    assertEquals("GET", server.method);
    assertEquals("/inboxes/i1/codes/latest", server.path);
    assertEquals("from=y.dev&wait=10&max_age=600", server.query);
    ms.inboxes().codes().latest("i1", LatestCodeOptions.builder().since("2026-10-16T12:00:00.000Z").build());
    assertEquals("since=2026-10-16T12%3A00%3A00.000Z", server.query);
    assertEquals("found", code.getStatus());
    assertEquals("482913", code.getCode());
    assertTrue(code.getLinkDomainMatchesSender());

    server.responseBody =
        "{\"object\":\"verification_code\",\"status\":\"timeout\",\"kind\":null,\"code\":null,\"url\":null,"
            + "\"link_domain_matches_sender\":null,\"email_id\":null,\"from\":null,\"received_at\":null,"
            + "\"sender_trust\":null}";
    assertEquals("timeout", ms.inboxes().codes().latest("i1").getStatus());
    assertNull(server.query);

    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":[{\"id\":\"r2\",\"thread_id\":\"t2\","
            + "\"from\":\"eve@z.dev\",\"subject\":\"Hi\",\"received_at\":\"2026-10-12T10:00:00.000Z\","
            + "\"quarantine_reason\":\"injection\",\"sender_trust\":\"unauthenticated\","
            + "\"injection_score\":97,\"phishing_score\":12,\"virus_signature\":null}]}";
    ListResponse<QuarantinedEmail> held =
        ms.inboxes().quarantine().list("i1", ListOptions.builder().limit(5).build());
    assertEquals("GET", server.method);
    assertEquals("/inboxes/i1/quarantine", server.path);
    assertEquals("limit=5", server.query);
    assertEquals("injection", held.getData().get(0).getQuarantineReason());
    assertEquals(97, held.getData().get(0).getInjectionScore());

    server.responseBody =
        "{\"object\":\"inbox_release\",\"inbox_id\":\"i1\",\"email_id\":\"r2\","
            + "\"released_at\":\"2026-10-12T10:05:00.000Z\",\"trusted_sender\":\"eve@z.dev\"}";
    QuarantineRelease released = ms.inboxes().quarantine().release("i1", "r2", true);
    assertEquals("POST", server.method);
    assertEquals("/inboxes/i1/quarantine/r2/release", server.path);
    assertEquals(json("{\"trust_sender\":true}"), body());
    assertEquals("eve@z.dev", released.getTrustedSender());

    ms.inboxes().quarantine().release("i1", "r2");
    assertEquals(json("{}"), body());
  }

  @Test
  void inboxLabels() throws Exception {
    server.responseBody =
        "{\"object\":\"list\",\"data\":[{\"id\":\"l1\",\"name\":\"Billing\",\"color\":\"#FF0000\","
            + "\"created_at\":\"2026-10-12T10:00:00.000Z\"}]}";
    ListResponse<InboxLabel> labels = ms.inboxes().labels().list("i1");
    assertEquals("GET", server.method);
    assertEquals("/inboxes/i1/labels", server.path);
    assertEquals("#FF0000", labels.getData().get(0).getColor());
    assertFalse(labels.isHasMore());

    server.status = 201;
    server.responseBody =
        "{\"object\":\"inbox_label\",\"id\":\"l1\",\"name\":\"Billing\",\"color\":\"#FF0000\","
            + "\"created_at\":\"2026-10-12T10:00:00.000Z\"}";
    InboxLabel created =
        ms.inboxes().labels().create("i1", InboxLabelOptions.builder().name("Billing").color("#ff0000").build());
    assertEquals("POST", server.method);
    assertEquals("/inboxes/i1/labels", server.path);
    assertEquals(json("{\"name\":\"Billing\",\"color\":\"#ff0000\"}"), body());
    assertEquals("inbox_label", created.getObject());

    server.status = 200;
    ms.inboxes().labels().update("i1", "l1", InboxLabelOptions.builder().color("#00FF00").build());
    assertEquals("PATCH", server.method);
    assertEquals("/inboxes/i1/labels/l1", server.path);
    assertEquals(json("{\"color\":\"#00FF00\"}"), body());

    server.responseBody = "{\"object\":\"inbox_label\",\"id\":\"l1\",\"deleted\":true}";
    ms.inboxes().labels().remove("i1", "l1");
    assertEquals("DELETE", server.method);
    assertEquals("/inboxes/i1/labels/l1", server.path);
  }

  static final String THREAD_ITEM_JSON =
      "{\"id\":\"t1\",\"subject\":null,\"from\":\"Ada <ada@y.dev>\",\"to\":[\"bot@x.dev\"],\"cc\":[],"
          + "\"bcc\":[],\"labels\":[{\"id\":\"l1\",\"name\":\"Billing\",\"color\":\"#FF0000\"}],"
          + "\"message_count\":2,\"has_attachment\":false,\"has_draft\":true,\"read\":false,"
          + "\"received_at\":\"2026-10-12T10:00:00.000Z\",\"folder\":\"a_folder_from_a_newer_server\"";

  @Test
  void inboxThreads() throws Exception {
    server.responseBody = "{\"object\":\"list\",\"has_more\":false,\"data\":[" + THREAD_ITEM_JSON + "}]}";
    ListResponse<InboxThread> list =
        ms.inboxes()
            .threads()
            .list(
                "i1",
                ListThreadsOptions.builder()
                    .folders("inbox", "archive")
                    .labels("l1", "l2")
                    .read(false)
                    .limit(10)
                    .after("t0")
                    .includeQuarantined(true)
                    .build());
    assertEquals("GET", server.method);
    assertEquals("/inboxes/i1/threads", server.path);
    assertEquals(
        "folders=inbox%2Carchive&labels=l1%2Cl2&read=false&limit=10&after=t0&include_quarantined=true",
        server.query);
    InboxThread t = list.getData().get(0);
    assertEquals("a_folder_from_a_newer_server", t.getFolder());
    assertEquals("Billing", t.getLabels().get(0).getName());
    assertEquals(2, t.getMessageCount());
    assertTrue(t.getHasDraft());
    assertNull(t.getSubject());
    assertNull(t.getQuarantined());

    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":[" + THREAD_ITEM_JSON + ",\"quarantined\":2}]}";
    list = ms.inboxes().threads().list("i1", ListThreadsOptions.builder().includeQuarantined(true).build());
    assertEquals(2, list.getData().get(0).getQuarantined());

    ms.inboxes().threads().list("i1");
    assertNull(server.query);

    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":[" + THREAD_ITEM_JSON
            + ",\"matched_email_id\":\"r1\",\"highlights\":{\"subject\":[\"Your **code**\"]}}]}";
    list =
        ms.inboxes()
            .threads()
            .search(
                "i1",
                SearchThreadsOptions.builder()
                    .query("code -spam")
                    .from("ada", "y.dev")
                    .to("bot")
                    .cc("c")
                    .bcc("b")
                    .hasAttachment(false)
                    .startDate("2026-10-01")
                    .endDate("2026-10-12")
                    .folders("sent")
                    .labels("l1")
                    .read(true)
                    .limit(5)
                    .before("t9")
                    .includeQuarantined(true)
                    .build());
    assertEquals("/inboxes/i1/threads/search", server.path);
    assertEquals(
        "folders=sent&labels=l1&read=true&limit=5&before=t9&include_quarantined=true&query=code+-spam"
            + "&from=ada%2Cy.dev&to=bot&cc=c&bcc=b&has_attachment=false&start_date=2026-10-01"
            + "&end_date=2026-10-12",
        server.query);
    assertEquals("r1", list.getData().get(0).getMatchedEmailId());
    assertEquals("Your **code**", list.getData().get(0).getHighlights().get("subject").get(0));

    server.responseBody =
        "{\"object\":\"inbox_thread\",\"id\":\"t1\",\"subject\":\"Hi\",\"folder\":\"inbox\","
            + "\"labels\":[],\"read\":true,\"quarantined\":0}";
    InboxThread summary = ms.inboxes().threads().get("i1", "t1");
    assertEquals("GET", server.method);
    assertEquals("/inboxes/i1/threads/t1", server.path);
    assertEquals("inbox_thread", summary.getObject());
    assertEquals(0, summary.getQuarantined());

    ms.inboxes()
        .threads()
        .update("i1", "t1", UpdateThreadOptions.builder().read(true).folder("archive").labelId("l1").build());
    assertEquals("PATCH", server.method);
    assertEquals("/inboxes/i1/threads/t1", server.path);
    assertEquals(json("{\"read\":true,\"folder\":\"archive\",\"label_id\":\"l1\"}"), body());

    server.responseBody = "{\"object\":\"inbox_thread\",\"id\":\"t1\",\"deleted\":false}";
    assertFalse(ms.inboxes().threads().remove("i1", "t1").getDeleted());
    assertEquals("DELETE", server.method);
    assertEquals("/inboxes/i1/threads/t1", server.path);
  }

  static final String SENT_JSON =
      "{\"id\":\"e9\",\"email_id\":\"e9\",\"direction\":\"outbound\",\"from\":\"Acme <bot@x.dev>\","
          + "\"to\":[\"ada@y.dev\"],\"cc\":[],\"bcc\":[],\"reply_to\":[],\"subject\":\"Re: Hi\",\"message_id\":null,"
          + "\"html\":null,\"text\":\"Thanks\",\"attachments\":[{\"id\":\"0\",\"filename\":\"a.pdf\",\"size\":42}],"
          + "\"read\":true,\"received_at\":\"2026-10-12T10:00:00.000Z\",\"status\":\"queued\","
          + "\"thread_id\":\"t1\",\"inbox_id\":\"i1\"}";

  @Test
  void inboxThreadEmails() throws Exception {
    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":[{\"id\":\"r1\",\"direction\":\"inbound\","
            + "\"from\":\"Ada <ada@y.dev>\",\"to\":[\"bot@x.dev\"],\"cc\":[],\"bcc\":[],\"reply_to\":[],"
            + "\"subject\":\"Hi\",\"message_id\":\"<m1@y.dev>\",\"html\":null,\"text\":\"Hi\","
            + "\"attachments\":[],\"read\":false,\"received_at\":\"2026-10-12T10:00:00.000Z\","
            + "\"extracted_text\":\"Hi\",\"screening\":{\"verdict\":\"suspicious\",\"sender_trust\":\"unauthenticated\"},"
            + "\"spam_verdict\":\"gray\",\"virus_verdict\":null}]}";
    ListResponse<ThreadEmail> list =
        ms.inboxes().threads().emails().list("i1", "t1", ListOptions.builder().after("r0").build());
    assertEquals("GET", server.method);
    assertEquals("/inboxes/i1/threads/t1/emails", server.path);
    assertEquals("after=r0", server.query);
    ThreadEmail inbound = list.getData().get(0);
    assertEquals("inbound", inbound.getDirection());
    assertEquals("suspicious", inbound.getScreening().getVerdict());
    assertEquals("gray", inbound.getSpamVerdict());
    ms.inboxes().threads().emails().list("i1", "t1", ListOptions.builder().after("r0").build(), true);
    assertEquals("after=r0&include_quarantined=true", server.query);
    ms.inboxes().threads().emails().list("i1", "t1");
    assertNull(server.query);

    server.responseBody = SENT_JSON;
    ThreadEmail sentEmail = ms.inboxes().threads().emails().get("i1", "t1", "e9");
    assertEquals("/inboxes/i1/threads/t1/emails/e9", server.path);
    assertNull(server.query);
    assertEquals("0", sentEmail.getAttachments().get(0).getId());
    assertEquals("queued", sentEmail.getStatus());
    ms.inboxes().threads().emails().get("i1", "t1", "e9", true);
    assertEquals("include_quarantined=true", server.query);

    SendResult<ThreadEmail> reply =
        ms.inboxes()
            .threads()
            .emails()
            .reply(
                "i1", "t1", "r1",
                ReplyOptions.builder().text("Thanks").cc("cc@x.dev").replyAll(true).requiresApproval(true).build(),
                RequestOptions.idempotencyKey("k-1"));
    assertEquals("POST", server.method);
    assertEquals("/inboxes/i1/threads/t1/emails/r1/reply", server.path);
    assertEquals("k-1", server.header("Idempotency-Key"));
    assertEquals(
        json("{\"text\":\"Thanks\",\"cc\":[\"cc@x.dev\"],\"reply_all\":true,\"requires_approval\":true}"),
        body());
    assertFalse(reply.isDraft());
    assertEquals("e9", reply.getSent().getEmailId());
    assertEquals("i1", reply.getSent().getInboxId());

    server.status = 202;
    server.responseBody = DRAFT_JSON;
    SendResult<ThreadEmail> forward =
        ms.inboxes()
            .threads()
            .emails()
            .forward("i1", "t1", "r1", ForwardOptions.builder().to("ops@x.dev", "cto@x.dev").build());
    assertEquals("/inboxes/i1/threads/t1/emails/r1/forward", server.path);
    assertEquals(json("{\"to\":[\"ops@x.dev\",\"cto@x.dev\"]}"), body());
    assertTrue(forward.isDraft());
    assertEquals("d1", forward.getDraft().getId());
  }

  @Test
  void inboxDrafts() throws Exception {
    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":[{\"id\":\"d1\",\"type\":\"standalone\",\"to\":null,"
            + "\"cc\":[],\"bcc\":[],\"subject\":null,\"snippet\":\"Hello\",\"thread_id\":null,"
            + "\"reply_to_email_id\":null,\"updated_at\":\"2026-10-12T10:00:00.000Z\",\"status\":\"draft\","
            + "\"approval_reasons\":[]}]}";
    ListResponse<InboxDraft> list = ms.inboxes().drafts().list("i1", ListOptions.builder().limit(3).build());
    assertEquals("GET", server.method);
    assertEquals("/inboxes/i1/drafts", server.path);
    assertEquals("limit=3", server.query);
    assertEquals("Hello", list.getData().get(0).getSnippet());
    assertNull(list.getData().get(0).getTo());

    server.status = 201;
    server.responseBody = DRAFT_JSON;
    InboxDraft created =
        ms.inboxes()
            .drafts()
            .create(
                "i1",
                CreateDraftOptions.builder()
                    .to("ada@y.dev")
                    .cc("cc@x.dev")
                    .bcc("bcc@x.dev")
                    .subject("Re: Hi")
                    .html("<p>Thanks</p>")
                    .text("Thanks")
                    .threadId("t1")
                    .replyToEmailId("r1")
                    .build());
    assertEquals("POST", server.method);
    assertEquals("/inboxes/i1/drafts", server.path);
    assertEquals(
        json(
            "{\"to\":[\"ada@y.dev\"],\"cc\":[\"cc@x.dev\"],\"bcc\":[\"bcc@x.dev\"],\"subject\":\"Re: Hi\","
                + "\"html\":\"<p>Thanks</p>\",\"text\":\"Thanks\",\"thread_id\":\"t1\",\"reply_to_email_id\":\"r1\"}"),
        body());
    assertEquals("reply", created.getType());
    assertEquals("2026-10-19T10:00:00.000Z", created.getExpiresAt());

    server.status = 200;
    ms.inboxes().drafts().get("i1", "d1");
    assertEquals("GET", server.method);
    assertEquals("/inboxes/i1/drafts/d1", server.path);

    ms.inboxes()
        .drafts()
        .update(
            "i1", "d1",
            UpdateDraftOptions.builder()
                .to(null)
                .cc(Collections.emptyList())
                .bcc(Arrays.asList("b@x.dev"))
                .subject(null)
                .text("New")
                .html(null)
                .build());
    assertEquals("PATCH", server.method);
    assertEquals("/inboxes/i1/drafts/d1", server.path);
    assertEquals(
        json("{\"to\":null,\"cc\":[],\"bcc\":[\"b@x.dev\"],\"subject\":null,\"text\":\"New\",\"html\":null}"),
        body());

    server.responseBody = "{\"object\":\"inbox_draft\",\"id\":\"d1\",\"thread_id\":\"t1\",\"email_id\":\"e9\"}";
    SendResult<InboxDraft> sent =
        ms.inboxes().drafts().send("i1", "d1", RequestOptions.idempotencyKey("send-1"));
    assertEquals("POST", server.method);
    assertEquals("/inboxes/i1/drafts/d1/send", server.path);
    assertEquals("", server.body);
    assertNull(server.header("Content-Type"));
    assertEquals("send-1", server.header("Idempotency-Key"));
    assertFalse(sent.isDraft());
    assertEquals("e9", sent.getSent().getEmailId());

    server.status = 202;
    server.responseBody = DRAFT_JSON;
    SendResult<InboxDraft> held = ms.inboxes().drafts().send("i1", "d1");
    assertTrue(held.isDraft());
    assertEquals("pending_approval", held.getDraft().getStatus());

    server.status = 200;
    server.responseBody = DRAFT_JSON.replace("pending_approval", "rejected");
    assertEquals("rejected", ms.inboxes().drafts().reject("i1", "d1").getStatus());
    assertEquals("POST", server.method);
    assertEquals("/inboxes/i1/drafts/d1/reject", server.path);

    server.responseBody = "{\"object\":\"inbox_draft\",\"id\":\"d1\",\"deleted\":true}";
    ms.inboxes().drafts().remove("i1", "d1");
    assertEquals("DELETE", server.method);
    assertEquals("/inboxes/i1/drafts/d1", server.path);
  }

  @Test
  void approvalRequiredSurfacesAsTypedError() throws Exception {
    server.status = 403;
    server.responseBody =
        "{\"statusCode\":403,\"name\":\"approval_required\",\"message\":\"A person must approve this draft\"}";
    MillionSendException e =
        assertThrows(MillionSendException.class, () -> ms.inboxes().drafts().send("i1", "d1"));
    assertEquals("approval_required", e.getName());
    assertEquals(403, e.getStatusCode());
  }

  @Test
  void inboxFieldsOnDomainsKeysAndWebhooks() throws Exception {
    server.responseBody = DOMAIN_JSON.replace("\"records\"", "\"receiving_catch_all\":true,\"records\"");
    assertTrue(ms.domains().get("d1").getReceivingCatchAll());
    ms.domains().update(UpdateDomainOptions.builder().id("d1").receivingCatchAll(false).build());
    assertEquals(json("{\"receiving_catch_all\":false}"), body());

    server.responseBody = "{\"id\":\"k1\",\"token\":\"ms_secret\"}";
    ms.apiKeys().create(CreateApiKeyOptions.builder().name("agent").permission("full_access").inboxId("i1").build());
    assertEquals(json("{\"name\":\"agent\",\"permission\":\"full_access\",\"inbox_id\":\"i1\"}"), body());
    server.responseBody =
        "{\"object\":\"list\",\"has_more\":false,\"data\":[{\"id\":\"k1\",\"name\":\"agent\","
            + "\"created_at\":\"2026-01-01T00:00:00.000Z\",\"last_used_at\":null,\"inbox_id\":\"i1\"}]}";
    assertEquals("i1", ms.apiKeys().list().getData().get(0).getInboxId());

    server.responseBody = "{\"object\":\"webhook\",\"id\":\"w1\",\"signing_secret\":\"whsec_abc\"}";
    ms.webhooks()
        .create(
            CreateWebhookOptions.builder()
                .endpoint("https://x.dev/hook")
                .events(WebhookEvent.EMAIL_RECEIVED, WebhookEvent.INBOX_EMAIL_QUARANTINED)
                .inboxIds(Collections.singletonList("i1"))
                .build());
    assertEquals(
        json(
            "{\"endpoint\":\"https://x.dev/hook\",\"events\":[\"email.received\",\"inbox.email.quarantined\"],"
                + "\"inbox_ids\":[\"i1\"]}"),
        body());
    ms.webhooks().update("w1", UpdateWebhookOptions.builder().inboxIds(null).build());
    assertEquals(json("{\"inbox_ids\":null}"), body());

    server.responseBody =
        "{\"object\":\"webhook\",\"id\":\"w1\",\"endpoint\":\"https://x.dev/hook\","
            + "\"created_at\":\"2026-01-01T00:00:00.000Z\",\"status\":\"enabled\","
            + "\"events\":[\"inbox.thread.created\"],\"inbox_ids\":[\"i1\",\"i2\"]}";
    Webhook w = ms.webhooks().get("w1");
    assertEquals(Arrays.asList("i1", "i2"), w.getInboxIds());
  }

  @Test
  void webhookEventsCoverReceivingAndInboxes() throws Exception {
    String[] wire = {
      "email.received", "inbox.created", "inbox.updated", "inbox.deleted", "inbox.expired",
      "inbox.thread.created", "inbox.thread.folder.updated", "inbox.thread.labels.updated",
      "inbox.email.received", "inbox.email.sent", "inbox.email.quarantined",
      "inbox.draft.created", "inbox.draft.updated", "inbox.draft.sent", "inbox.draft.deleted"
    };
    for (String value : wire) {
      WebhookEvent e = WebhookEvent.fromValue(value);
      assertEquals(value, e.getValue());
    }
    assertNull(WebhookEvent.fromValue("inbox.thread.assigned"));
  }
}
