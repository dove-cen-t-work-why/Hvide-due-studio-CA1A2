package rest;

import dtos.HobbyDTO;
import entities.*;
import facades.HobbyFacade;
import facades.PersonFacade;
import facades.PhoneFacade;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;

class HobbyResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    private static HobbyFacade HOBBY_FACADE;
    private static PersonFacade PERSON_FACADE;

    private static Person p1, p2, p3;
    private static Hobby h1, h2, h3;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        HOBBY_FACADE = HobbyFacade.getHobbyFacade(emf);
        PERSON_FACADE = PersonFacade.getPersonFacade(emf);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();

        h1 = new Hobby("Skiing", "skiing.com", "General", "Outdoors");
        h2 = new Hobby("Polo", "polo.com", "Sport", "Outdoors");
        h3 = new Hobby("Jogging", "jogging.com", "General", "Outdoors");

        p1 = new Person(
                Arrays.asList(new Phone(11111111)),
                "bob@bob.com",
                "Bob",
                "Roberts",
                new Address("Test street 21",
                        new Zip(6969, "Nice-ville")),
                Arrays.asList(h1, h2));

        p2 = new Person(
                Arrays.asList(new Phone(22222222)),
                "alice@alice.com",
                "Alice",
                "Allison",
                new Address("2nd and Hill 34",
                        new Zip(4242, "Cool-town")),
                Arrays.asList(h2, h3));

        try {
            em.getTransaction().begin();
            // hopefully there's a better way than use a native query to wipe out the join table
            em.createNativeQuery("DELETE FROM PERSON_PHONE").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.resetPK").executeUpdate();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("Zip.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    void demo() {
    }

    @Test
    void createHobby() {
        HobbyDTO hobbyDTO = new HobbyDTO("Music", "music.com", "General", "Creative");

        given()
                .contentType(ContentType.JSON)
                .body(hobbyDTO)
                .when()
                .post("hobby")
                .then()
                .statusCode(200)
                .body("id", not(0))
                .body("name", equalTo(hobbyDTO.getName()))
                .body("link", equalTo(hobbyDTO.getLink()))
                .body("category", equalTo(hobbyDTO.getCategory()))
                .body("type", equalTo(hobbyDTO.getType()));
    }

    @Test
    void updateHobby() {
        h1.setName("Snowboarding");
        HobbyDTO hobbyDTO = new HobbyDTO(h1);

        given()
                .contentType(ContentType.JSON)
                .body(hobbyDTO)
                .when()
                .put("hobby")
                .then()
                .statusCode(200)
                .body("id", equalTo((int)hobbyDTO.getId()))
                .body("name", equalTo(hobbyDTO.getName()))
                .body("link", equalTo(hobbyDTO.getLink()))
                .body("category", equalTo(hobbyDTO.getCategory()))
                .body("type", equalTo(hobbyDTO.getType()));
    }

    @Test
    void deleteHobby() {
        HobbyDTO hobbyDTO = new HobbyDTO(h2);

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("hobby/" + hobbyDTO.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo((int)hobbyDTO.getId()))
                .body("name", equalTo(hobbyDTO.getName()))
                .body("link", equalTo(hobbyDTO.getLink()))
                .body("category", equalTo(hobbyDTO.getCategory()))
                .body("type", equalTo(hobbyDTO.getType()));
    }

    @Test
    void deleteHobby_badId() {
        long id = 666;

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("hobby/" + id)
                .then()
                .body("code", equalTo(404))
                .body("message", equalTo("No hobby with id: " + id));
    }

    @Test
    void getAll() {
    }

    @Test
    void getByCategory() {
    }

    @Test
    void getByType() {
    }

    @Test
    void getByZip() {
    }

    @Test
    void getByAddress() {
    }

    @Test
    void testGetByAddress() {
    }

    @Test
    void getByPerson() {
    }

    @Test
    void getHobbyCount() {
    }
}