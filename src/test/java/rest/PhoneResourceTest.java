package rest;

import dtos.PhoneDTO;
import entities.Address;
import entities.Person;
import entities.Phone;
import entities.Zip;
import facades.PhoneFacade;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.util.HttpStatus;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
//Uncomment the line below, to temporarily disable this test

//@Disabled
public class PhoneResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Phone ph1, ph2;
    private static Person pe1;
    private static PhoneFacade phoneFacade;

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
        phoneFacade = PhoneFacade.getPhoneFacade(emf);

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

        ph1 = new Phone(11111111, "Business");
        ph2 = new Phone(22222222, "Pleasure");

        pe1 = new Person(
                Arrays.asList(ph1, ph2),
                "bob@bob.com",
                "Bob",
                "Roberts",
                new Address("Test street 21",
                        new Zip(6969, "Nice-ville")));

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
            em.persist(ph1);
            em.persist(ph2);
            em.persist(pe1);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    void getAll() {
    }

    @Test
    void getById() {
    }

    @Test
    void getByPhone() {
    }

    @Test
    void testGetByPhone() {
    }

    @Test
    void getPhoneCount() {
    }

    @Test
    void updatePhone() {
        PhoneDTO p2DTO = new PhoneDTO(ph2);
        p2DTO.setNumber(66666666);

        given()
                .contentType(ContentType.JSON)
                .body(p2DTO)
                .when()
                .put("phone" )
                .then()
                .body("number", equalTo(66666666))
                .body("info", equalTo("Pleasure"));
    }

    @Test
    void deletePhone() {
        given()
                .contentType("application/json")
                .when()
                .delete("phone/" + ph2.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("id", equalTo((int) ph2.getId()))
                .body("number", equalTo(ph2.getNumber()))
                .body("info", equalTo(ph2.getInfo()));;
    }
}