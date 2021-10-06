package rest;

import dtos.AddressDTO;
import entities.Address;
import entities.Zip;
import facades.AddressFacade;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class AddressResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Address a1, a2;
    private static AddressFacade addressFacade;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    static void beforeAll() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        addressFacade = AddressFacade.getAddressFacade(emf);

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
    void setUp() {
        EntityManager em = emf.createEntityManager();

        a1 = new Address("Test street 21",
                new Zip(6969, "Nice-ville"));
        a2 = new Address("2nd and Hill 34",
                new Zip(4242, "Cool-town"));

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
            em.persist(a1);
            em.persist(a2);
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
    void getAddressCount() {
    }

    @Test
    void updateAddress() {
        AddressDTO a2DTO = new AddressDTO(a2);
        a2DTO.setAddress("Fest street 21");

        given()
                .contentType(ContentType.JSON)
                .body(a2DTO)
                .when()
                .put("address" )
                .then()
                .body("address", equalTo("Fest street 21"))
                .body("zip.id", equalTo(4242))
                .body("zip.city", equalTo("Cool-town"));

    }

    @Test
    void deleteAddress() {
        AddressDTO a2DTO = addressFacade.getById(a2.getId());

        given()
                .contentType("application/json")
                .delete("address/" + a2DTO.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }
}