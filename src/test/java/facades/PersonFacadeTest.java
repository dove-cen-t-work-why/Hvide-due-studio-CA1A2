package facades;

import dtos.*;
import entities.*;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonFacadeTest {
    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static HobbyFacade HOBBY_FACADE;
    private static AddressFacade ADDRESS_FACADE;

    private static Person p1, p2;
    private static Hobby h1, h2, h3;
    
    @BeforeAll
    static void beforeAll() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getPersonFacade(emf);
        HOBBY_FACADE = HobbyFacade.getHobbyFacade(emf);
        ADDRESS_FACADE = AddressFacade.getAddressFacade(emf);
    }

    @AfterAll
    static void afterAll() {
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
                        new Zip(6969, "Nice-ville")));
        
        p1.addHobby(h1);
        p1.addHobby(h2);
        
        p2 = new Person(
                Arrays.asList(new Phone(22222222)),
                "alice@alice.com",
                "Alice",
                "Allison",
                new Address("2nd and Hill 34",
                        new Zip(4242, "Cool-town")));
        
        p2.addHobby(h2);
        p2.addHobby(h3);
        
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
    void create_newAddress_newZip() {
        PersonDTO person = new PersonDTO(
                Arrays.asList(new PhoneDTO(34343434, "work")),
                "lars@larsen.lars",
                "Lars",
                "Larsen",
                new AddressDTO("Lars street",
                        new ZipDTO(1234, "Lars city")));

        WebApplicationException e = assertThrows(WebApplicationException.class,
                () -> facade.create(person));
        assertEquals(404, e.getResponse().getStatus());
        assertEquals("ZIP code " + person.getAddress().getZip().getId() + " not found.", e.getMessage());
    }

    @Test
    void create_newAddress_existingZip() {
        PersonDTO person = new PersonDTO(
                Arrays.asList(new PhoneDTO(33333333, "work")),
                "chad@chad.com",
                "Chad",
                "Kroeger",
                new AddressDTO("Someday 42",
                        new ZipDTO(p2.getAddress().getZip())));

        PersonDTO created = facade.create(person);
        assertNotNull(created);
        assertEquals(person.getFirstName(), created.getFirstName());

        PersonDTO fromDb = facade.getById(created.getId());

        assertNotNull(fromDb);
        assertEquals(created.getFirstName(), fromDb.getFirstName());
    }

    @Test
    void create_existingAddress() {
        List<PhoneDTO> phones = new ArrayList<>();
        phones.add(new PhoneDTO(34343434, "work"));
        PersonDTO person = new PersonDTO(
                phones,
                "lars@larsen.lars",
                "Lars",
                "Larsen",
                new AddressDTO(p1.getAddress()));
        PersonDTO created = facade.create(person);
        assertNotNull(created);
        assertEquals(person.getFirstName(), created.getFirstName());

        PersonDTO fromDb = facade.getById(created.getId());

        assertNotNull(fromDb);
        assertEquals(created.getFirstName(), fromDb.getFirstName());
    }

    @Test
    void create_withHobbies() {
        PersonDTO person = new PersonDTO(
                Arrays.asList(new PhoneDTO(57575757, "nonya bizniz")),
                "charlie@charlie.com",
                "Charlie",
                "Chaplin",
                new AddressDTO("Comedy Inn",
                        new ZipDTO(p1.getAddress().getZip())),
                HobbyDTO.getDtos(p1.getHobbies())
        );
        PersonDTO created = facade.create(person);
        assertEquals(3, HOBBY_FACADE.getHobbyCount());
    }

    @Test
    void getById() {
        PersonDTO person = facade.getById(p1.getId());
        assertNotNull(person);
        assertEquals(p1.getFirstName(), person.getFirstName());
    }

    @Test
    void getPersonCount() {
        assertEquals(2, facade.getPersonCount());
    }

    @Test
    void getAll() {
        List<PersonDTO> persons = facade.getAll();
        assertNotNull(persons);
        assertEquals(2, persons.size());
    }

    @Test
    void edit() {
        p2.setLastName("Allis");
        PersonDTO p2DTO = new PersonDTO(p2);

        facade.update(p2DTO);
        assertEquals("Allis", facade.getById(p2.getId()).getLastName());
    }

    @Test
    void editPersonHobby_deleteHobby() {
        p2.setHobbies(Arrays.asList(h1));
        PersonDTO p2DTO = new PersonDTO(p2);

        PersonDTO edited1 = facade.update(p2DTO);
        assertEquals(1, edited1.getHobbies().size());
        assertTrue(h1.equals(edited1.getHobbies().get(0)));

        HOBBY_FACADE.delete(h1.getId());
        WebApplicationException e = assertThrows(WebApplicationException.class, () -> {
            facade.update(p2DTO);
        });
        assertEquals(404, e.getResponse().getStatus());
        assertEquals("Hobby not found", e.getMessage());
//        PersonDTO edited2 = facade.update(p2DTO);
//        edited2.getHobbies().forEach(System.out::println);    // only needed to debug if below test fails
//        assertEquals(0, edited2.getHobbies().size());         // if we want to ignore deleted hobbies and only process hobbies that are valid.
    }

    @Test
    void editAddress() {
        Address address = new Address("Test street 12", p1.getAddress().getZip());
        p1.setAddress(address);
        PersonDTO p1DTO = new PersonDTO(p1);

        facade.update(p1DTO);
        assertEquals(p1.getAddress().getAddress(), facade.getById(p1.getId()).getAddress().getAddress());
        assertEquals(2, ADDRESS_FACADE.getAddressCount());
    }

    // if a person is deleted, their address cascade deletes
    @Test
    void editAddressOnOneAndRemovePerson() {
        Address address = new Address("Test street 1", p1.getAddress().getZip());
        p1.setAddress(address);
        PersonDTO p1DTO = new PersonDTO(p1);

        facade.update(p1DTO);
        assertEquals(p1.getAddress().getAddress(), facade.getById(p1.getId()).getAddress().getAddress());
        
        facade.delete(p1.getId());

        assertEquals(1, ADDRESS_FACADE.getAddressCount());
    }
    
    // if two persons have same address, and one is deleted, the address is not deleted
    @Test
    void editAddressOnTwoAndRemovePerson() {
        Address address = new Address("Test street 12", p2.getAddress().getZip());
        p1.setAddress(address);
        p2.setAddress(address);
        PersonDTO p1DTO = new PersonDTO(p1);
        PersonDTO p2DTO = new PersonDTO(p2);

        facade.update(p1DTO);
        assertEquals(p1.getAddress().getAddress(), facade.getById(p1.getId()).getAddress().getAddress());
        facade.update(p2DTO);
        assertEquals(p2.getAddress().getAddress(), facade.getById(p2.getId()).getAddress().getAddress());
        
        facade.delete(p1.getId());

        assertEquals(1, ADDRESS_FACADE.getAddressCount());
    }

    @Test
    void delete() {
        facade.delete(p2.getId());
        List<PersonDTO> persons = facade.getAll();
        assertEquals(1, persons.size());
        assertEquals(p1.getFirstName(), persons.get(0).getFirstName());
        assertTrue(p1.equals(persons.get(0)));
        assertTrue(persons.get(0).equals(p1));
        assertEquals(1, ADDRESS_FACADE.getAddressCount());
    }

    @Test
    void delete_badId() {
        long id = 99;
        WebApplicationException e = assertThrows(WebApplicationException.class, () -> {
            facade.delete(99);
        });
        assertEquals(404, e.getResponse().getStatus());
        assertEquals("Person not found", e.getMessage());
    }

    @Test
    void getByPhone() {
        PhoneDTO phone = new PhoneDTO(p1.getPhones().get(0));
        PersonDTO person = facade.getByPhone(phone);
        assertNotNull(person);
        assertEquals(p1.getFirstName(), person.getFirstName());
    }

    @Test
    void getByHobby() {
        HobbyDTO hobby1 = new HobbyDTO(p1.getHobbies().get(1)); // h2
        List<PersonDTO> persons1 = facade.getByHobby(hobby1);
        assertNotNull(persons1);
        assertEquals(2, persons1.size());
        // assertThat(persons1, containsInAnyOrder(new PersonDTO(p1), new PersonDTO(p2));
        
        HobbyDTO hobby2 = new HobbyDTO(p2.getHobbies().get(1)); // h3
        List<PersonDTO> persons2 = facade.getByHobby(hobby2);
        assertNotNull(persons2);
        assertEquals(1, persons2.size());
    }

    @Test
    void getByAddress() {
        AddressDTO address = new AddressDTO(p1.getAddress());
        List<PersonDTO> persons = facade.getByAddress(address);
        assertNotNull(persons);
        assertEquals(1, persons.size());
        assertEquals("Bob", persons.get(0).getFirstName());
    }

    @Test
    void getByZip() {
        ZipDTO zip = new ZipDTO(4242, "Cool-town");
        List<PersonDTO> persons = facade.getByZip(zip);
        assertNotNull(persons);
        assertEquals(1, persons.size());
        assertEquals("Alice", persons.get(0).getFirstName());
    }
}