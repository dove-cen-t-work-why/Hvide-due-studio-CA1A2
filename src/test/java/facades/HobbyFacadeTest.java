package facades;

import dtos.HobbyDTO;
import dtos.PersonDTO;
import entities.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HobbyFacadeTest {
    private static EntityManagerFactory emf;
    private static HobbyFacade HOBBY_FACADE;
    private static PersonFacade PERSON_FACADE;

    private static Person p1, p2, p3;
    private static Hobby h1, h2, h3;

    @BeforeAll
    static void beforeAll() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        HOBBY_FACADE = HobbyFacade.getHobbyFacade(emf);
        PERSON_FACADE = PersonFacade.getPersonFacade(emf);
    }

    @BeforeEach
    void setUp() {
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
    void create() {
        HobbyDTO hobbyDTO = new HobbyDTO("Music", "music.com", "General", "Creative");
        HobbyDTO createdDTO = HOBBY_FACADE.create(hobbyDTO);
        HobbyDTO fromDb = HOBBY_FACADE.getById(createdDTO.getId());

        assertNotNull(createdDTO);
        assertNotNull(fromDb);
        assertEquals(hobbyDTO.getName(), createdDTO.getName());
        assertEquals(hobbyDTO.getName(), fromDb.getName());
        assertEquals(4, HOBBY_FACADE.getHobbyCount());
    }

    @Test
    void update() {
        h1.setName("Snowboarding");
        HobbyDTO hobbyDTO = new HobbyDTO(h1);
        HobbyDTO updated = HOBBY_FACADE.update(hobbyDTO);
        HobbyDTO fromDb = HOBBY_FACADE.getById(h1.getId());
        assertTrue(h1.equals(updated));
        assertTrue(h1.equals(fromDb));

        PersonDTO p1DTO = PERSON_FACADE.getById(p1.getId());    // gets fresh from DB
        p1DTO.getHobbies().forEach(h -> {
            if (h1.getId() == h.getId()) {
                assertEquals(h1.getName(), h.getName());
            }
        });
    }

    @Test
    void updateThenDelete() {
        h1.setName("Snowboarding");
        HobbyDTO hobbyDTO = new HobbyDTO(h1);
        HOBBY_FACADE.update(hobbyDTO);
        HOBBY_FACADE.delete(hobbyDTO.getId());

        PersonDTO p1DTO = PERSON_FACADE.getById(p1.getId());    // gets fresh from DB
        assertEquals(1, p1DTO.getHobbies().size());
    }

    @Test
    void delete_goodId() throws Exception {
        HobbyDTO deleted = HOBBY_FACADE.delete(h2.getId());
        assertTrue(h2.equals(deleted));
        PersonDTO p1DTO = PERSON_FACADE.getById(p1.getId());    // gets fresh from DB
        PersonDTO p2DTO = PERSON_FACADE.getById(p2.getId());    // gets fresh from DB
        assertEquals(1, p1DTO.getHobbies().size());
        assertEquals(1, p2DTO.getHobbies().size());
    }

    @Test
    void delete_badId() {
        long id = 666;
        WebApplicationException e = assertThrows(WebApplicationException.class,
                () -> HOBBY_FACADE.delete(id));
        assertEquals(404, e.getResponse().getStatus());
        assertEquals("No hobby with id: " + id, e.getMessage());
    }

    @Test
    void getAll() {
        List<HobbyDTO> hobbyDTOS = HOBBY_FACADE.getAll();
        assertEquals(3, hobbyDTOS.size());
        // figure out the nicest way to check if list contains the equivalent of our hobbies.
        // thinking of overriding the actual .equals method. That way maybe contains will work.
    }

    @Test
    void getHobbyCount() {
        assertEquals(3, HOBBY_FACADE.getHobbyCount());
    }

    @Test
    void getById() {
        HobbyDTO hobby = HOBBY_FACADE.getById(h3.getId());
        assertTrue(h3.equals(hobby));
    }
}