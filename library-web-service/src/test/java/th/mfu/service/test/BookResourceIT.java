package th.mfu.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * INTEGRATION TEST for the Book endpoints.
 *
 * A unit test calls a Java method. This calls the service the way Postman does:
 * over a real HTTP connection, through Tomcat, through Jackson, into MySQL and
 * back. If any tier is wired up wrongly, this notices - a unit test would not.
 *
 * The name ends in IT, not Test, on purpose:
 *     surefire runs *Test  during  mvn test
 *     failsafe runs *IT    during  mvn verify
 *
 * Run it from the TOP folder:
 *
 *     mvn verify
 *
 * Maven starts the service, runs this, then stops the service - the two extra
 * executions of spring-boot-maven-plugin in this module's pom.
 *
 * Nothing here mentions Book, Category or any entity class. The test only knows
 * the JSON contract, exactly like a real client.
 */
public class BookResourceIT {

    private static final String WEB_SERVICE_URI = "http://localhost:8080/api/books";

    private static Client client;

    @BeforeClass
    public static void createClient() {
        // Slide "Create Client": ClientBuilder makes a client that can open
        // connections to the web service.
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void closeConnection() {
        client.close();
    }

    /** The body a client sends. Plain JSON - no entity class involved. */
    private String bookJson(String title, String author, int year, long categoryId) {
        return "{\"title\":\"" + title + "\",\"author\":\"" + author + "\","
                + "\"publish-year\":" + year + ",\"category_id\":" + categoryId + "}";
    }

    private Builder target(String uri) {
        return client.target(uri).request(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testRetrieveAll() {
        // Slide "Get Request": build a target, then call get().
        try (Response response = target(WEB_SERVICE_URI).get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

            List<Map<String, Object>> books = response.readEntity(
                    new GenericType<List<Map<String, Object>>>() {
                    });
            assertTrue("data.sql should have loaded some books", books.size() >= 10);

            // The DTO's JSON names, not the entity's field names.
            Map<String, Object> first = books.get(0);
            assertTrue(first.containsKey("publish-year"));
            assertTrue(first.containsKey("category_name"));
            // The entity has a transactions list; the DTO must not expose it.
            assertTrue("DTO must not leak the entity's collections",
                    !first.containsKey("transactions"));
        }
    }

    @Test
    public void testRetrieve() {
        try (Response response = target(WEB_SERVICE_URI + "/1").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

            Map<String, Object> book = response.readEntity(
                    new GenericType<Map<String, Object>>() {
                    });
            assertEquals("Effective Java", book.get("title"));
            assertEquals("Programming", book.get("category_name"));
        }
    }

    @Test
    public void testRetrieveUnknownBook() {
        try (Response response = target(WEB_SERVICE_URI + "/9999").get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    @Test
    public void testCreate() {
        // Slide "Post/Put Request": Entity.json turns the body into JSON.
        try (Response response = target(WEB_SERVICE_URI)
                .post(Entity.json(bookJson("Domain-Driven Design", "Eric Evans", 2003, 1)))) {

            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

            Map<String, Object> created = response.readEntity(
                    new GenericType<Map<String, Object>>() {
                    });
            assertNotNull("the database must assign an id", created.get("id"));
            assertEquals("Domain-Driven Design", created.get("title"));
            assertEquals("Programming", created.get("category_name"));
        }
    }

    @Test
    public void testCreateWithUnknownCategory() {
        try (Response response = target(WEB_SERVICE_URI)
                .post(Entity.json(bookJson("Nowhere", "Nobody", 2000, 999)))) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    public void testUpdateReplacesEverything() {
        Long id = createBook("Temporary title", "Temporary author", 1999);

        // PUT sends the whole object, and leaves out the author on purpose.
        try (Response response = client.target(WEB_SERVICE_URI + "/" + id)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json("{\"title\":\"Replaced\",\"publish-year\":2020}"))) {
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }

        Map<String, Object> after = getBook(id);
        assertEquals("Replaced", after.get("title"));
        // PUT replaces: the author that was left out is gone.
        assertNull("PUT must not keep the author that was left out", after.get("author"));
    }

    @Test
    public void testUpdateUnknownBook() {
        try (Response response = client.target(WEB_SERVICE_URI + "/9999")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json("{\"title\":\"Nothing\"}"))) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    /**
     * THE POINT OF THE MAPSTRUCT LESSON.
     *
     * Same starting book, same missing author - but PATCH keeps it, because
     * BookMapper ignores the null fields instead of copying them over.
     * Compare this test with testUpdateReplacesEverything above.
     */
    @Test
    public void testPatchKeepsTheFieldsYouDidNotSend() {
        Long id = createBook("Before patch", "Martin Fowler", 2002);

        try (Response response = client.target(WEB_SERVICE_URI + "/" + id)
                .request(MediaType.APPLICATION_JSON)
                .method("PATCH", Entity.json("{\"title\":\"After patch\"}"))) {

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

            Map<String, Object> patched = response.readEntity(
                    new GenericType<Map<String, Object>>() {
                    });
            assertEquals("After patch", patched.get("title"));
            assertEquals("the author was not sent, so it must survive",
                    "Martin Fowler", patched.get("author"));
            assertEquals("the year was not sent, so it must survive",
                    2002, patched.get("publish-year"));
            assertNotNull("the date was not sent, so it must survive",
                    patched.get("added-date"));
        }
    }

    @Test
    public void testPatchUnknownBook() {
        try (Response response = client.target(WEB_SERVICE_URI + "/9999")
                .request(MediaType.APPLICATION_JSON)
                .method("PATCH", Entity.json("{\"title\":\"Nothing\"}"))) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    @Test
    public void testDelete() {
        Long id = createBook("To be deleted", "Nobody", 2001);

        try (Response response = target(WEB_SERVICE_URI + "/" + id).delete()) {
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
        try (Response response = target(WEB_SERVICE_URI + "/" + id).get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    @Test
    public void testDeleteUnknownBook() {
        try (Response response = target(WEB_SERVICE_URI + "/9999").delete()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    // ---------------------------------------------------------------- helpers

    private Long createBook(String title, String author, int year) {
        try (Response response = target(WEB_SERVICE_URI)
                .post(Entity.json(bookJson(title, author, year, 1)))) {
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            Map<String, Object> created = response.readEntity(
                    new GenericType<Map<String, Object>>() {
                    });
            return Long.valueOf(created.get("id").toString());
        }
    }

    private Map<String, Object> getBook(Long id) {
        try (Response response = target(WEB_SERVICE_URI + "/" + id).get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            return response.readEntity(new GenericType<Map<String, Object>>() {
            });
        }
    }
}
