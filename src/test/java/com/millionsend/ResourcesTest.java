package com.millionsend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.millionsend.model.ContactAddress;
import com.millionsend.model.CreateAudienceOptions;
import com.millionsend.model.CreateBroadcastOptions;
import com.millionsend.model.CreateContactOptions;
import com.millionsend.model.CreateEmailOptions;
import com.millionsend.model.CreateSegmentOptions;
import com.millionsend.model.CreateTopicOptions;
import com.millionsend.model.ListOptions;
import com.millionsend.model.SegmentCondition;
import com.millionsend.model.SegmentFilter;
import com.millionsend.model.SendBroadcastOptions;
import com.millionsend.model.Subscription;
import com.millionsend.model.UpdateBroadcastOptions;
import com.millionsend.model.UpdateContactOptions;
import com.millionsend.model.UpdateContactTopicsOptions;
import com.millionsend.model.UpdateSegmentOptions;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Method + path + body mapping for a representative endpoint of every resource. */
class ResourcesTest {

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

  private static CreateEmailOptions email(String to, String subject) {
    return CreateEmailOptions.builder()
        .from("a@x.dev")
        .to(to)
        .subject(subject)
        .text("body")
        .build();
  }

  @Test
  void emails() throws Exception {
    ms.emails().get("e1");
    assertEquals("GET", server.method);
    assertEquals("/emails/e1", server.path);

    ms.emails().cancel("e1");
    assertEquals("POST", server.method);
    assertEquals("/emails/e1/cancel", server.path);
  }

  @Test
  void batch() throws Exception {
    server.responseBody = "{\"data\":[{\"id\":\"1\"},{\"id\":\"2\"}]}";
    ms.batch().send(Arrays.asList(email("b@x.dev", "1"), email("c@x.dev", "2")), "batch-1");
    assertEquals("POST", server.method);
    assertEquals("/emails/batch", server.path);
    assertTrue(body().isArray());
    assertEquals(2, body().size());
    assertEquals("batch-1", server.header("Idempotency-Key"));
  }

  @Test
  void audiences() throws Exception {
    ms.audiences().create(CreateAudienceOptions.builder().name("Users").build());
    assertEquals("POST", server.method);
    assertEquals("/audiences", server.path);
    assertEquals("Users", body().get("name").asText());

    ms.audiences().get("a1");
    assertEquals("/audiences/a1", server.path);

    ms.audiences().list(ListOptions.builder().limit(10).build());
    assertEquals("/audiences", server.path);
    assertEquals("limit=10", server.query);

    ms.audiences().remove("a1");
    assertEquals("DELETE", server.method);
    assertEquals("/audiences/a1", server.path);
  }

  @Test
  void contactsCreate() throws Exception {
    ms.contacts()
        .create(
            CreateContactOptions.builder().audienceId("a1").email("c@x.dev").firstName("Ada").build());
    assertEquals("/audiences/a1/contacts", server.path);
    assertEquals("c@x.dev", body().get("email").asText());
    assertEquals("Ada", body().get("first_name").asText());
    assertFalse(body().has("audience_id"));

    ms.contacts().create(CreateContactOptions.builder().email("c@x.dev").build());
    assertEquals("/contacts", server.path);
  }

  @Test
  void contactsAddressing() throws Exception {
    ms.contacts().get("c1");
    assertEquals("/contacts/c1", server.path);

    ms.contacts().get(ContactAddress.email("c@x.dev"));
    assertEquals("/contacts/c@x.dev", server.path); // decoded by the server; sent as c%40x.dev

    ms.contacts().get(ContactAddress.builder().audienceId("a1").id("c1").build());
    assertEquals("/audiences/a1/contacts/c1", server.path);
  }

  @Test
  void contactsUpdateSendsOnlySetKeysAndNullClears() throws Exception {
    ms.contacts()
        .update(UpdateContactOptions.builder().id("c1").firstName(null).unsubscribed(true).build());
    assertEquals("PATCH", server.method);
    assertEquals("/contacts/c1", server.path);
    assertTrue(body().get("first_name").isNull());
    assertTrue(body().get("unsubscribed").asBoolean());
    assertFalse(body().has("last_name"));
  }

  @Test
  void contactsRemoveAndScopedList() throws Exception {
    ms.contacts().remove(ContactAddress.email("c@x.dev"));
    assertEquals("DELETE", server.method);

    ms.contacts().list("a1", ListOptions.builder().after("cur").build());
    assertEquals("/audiences/a1/contacts", server.path);
    assertEquals("after=cur", server.query);
  }

  @Test
  void contactTopicsUpdate() throws Exception {
    ms.contacts()
        .topics()
        .update(
            UpdateContactTopicsOptions.builder().id("c1").topic("t1", Subscription.OPT_OUT).build());
    assertEquals("PATCH", server.method);
    assertEquals("/contacts/c1/topics", server.path);
    assertTrue(body().isArray());
    assertEquals("t1", body().get(0).get("id").asText());
    assertEquals("opt_out", body().get(0).get("subscription").asText());
  }

  @Test
  void broadcasts() throws Exception {
    ms.broadcasts()
        .create(
            CreateBroadcastOptions.builder()
                .audienceId("a1")
                .from("a@x.dev")
                .subject("News")
                .html("<p>hi</p>")
                .build());
    assertEquals("/broadcasts", server.path);
    assertEquals("a1", body().get("audience_id").asText());
    assertEquals("News", body().get("subject").asText());

    ms.broadcasts().get("b1");
    assertEquals("/broadcasts/b1", server.path);

    ms.broadcasts().list();
    assertEquals("/broadcasts", server.path);

    ms.broadcasts().update("b1", UpdateBroadcastOptions.builder().subject("New").build());
    assertEquals("PATCH", server.method);
    assertEquals("/broadcasts/b1", server.path);

    ms.broadcasts().send("b1", SendBroadcastOptions.builder().scheduledAt("2999-01-01T00:00:00Z").build());
    assertEquals("/broadcasts/b1/send", server.path);
    assertEquals("2999-01-01T00:00:00Z", body().get("scheduled_at").asText());

    ms.broadcasts().cancel("b1");
    assertEquals("/broadcasts/b1/cancel", server.path);

    ms.broadcasts().remove("b1");
    assertEquals("DELETE", server.method);
  }

  @Test
  void topics() throws Exception {
    ms.topics().create(CreateTopicOptions.builder().name("Product").defaultSubscription(Subscription.OPT_IN).build());
    assertEquals("/topics", server.path);
    assertEquals("Product", body().get("name").asText());
    assertEquals("opt_in", body().get("default_subscription").asText());

    ms.topics().get("t1");
    assertEquals("/topics/t1", server.path);

    ms.topics().list();
    assertEquals("GET", server.method);
    assertEquals("/topics", server.path);

    ms.topics().remove("t1");
    assertEquals("DELETE", server.method);
  }

  @Test
  void segments() throws Exception {
    SegmentFilter filter =
        SegmentFilter.builder().match("all").condition(new SegmentCondition("email", "is_set")).build();
    ms.segments().create(CreateSegmentOptions.builder().name("Active").audienceId("a1").filter(filter).build());
    assertEquals("/segments2", server.path);
    assertEquals("a1", body().get("audience_id").asText());
    assertEquals("all", body().get("filter").get("match").asText());

    ms.segments().get("s1");
    assertEquals("/segments2/s1", server.path);

    ms.segments().list(ListOptions.builder().before("cur").build());
    assertEquals("/segments2", server.path);
    assertEquals("before=cur", server.query);

    ms.segments().update("s1", UpdateSegmentOptions.builder().name("Renamed").build());
    assertEquals("PATCH", server.method);
    assertEquals("/segments2/s1", server.path);

    ms.segments().remove("s1");
    assertEquals("DELETE", server.method);
  }
}
