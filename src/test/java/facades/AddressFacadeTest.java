package facades;

import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.ZipDTO;
import edu.emory.mathcs.backport.java.util.Arrays;
import entities.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddressFacadeTest {
    private static EntityManagerFactory emf;
    private static AddressFacade facade;
    private static PersonFacade PERSON_FACADE;

    private static Address a1, a2, a3;
    private static Person p1, p2;

    @BeforeAll
    static void beforeAll() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = AddressFacade.getAddressFacade(emf);
        PERSON_FACADE = PersonFacade.getPersonFacade(emf);
    }

    @BeforeEach
    void setUp() {
        EntityManager em = emf.createEntityManager();

        a1 = new Address("Test street 21",
                new Zip(6969, "Nice-ville"));
        a2 = new Address("2nd and Hill 34",
                new Zip(4242, "Cool-town"));
        a3 = new Address("Test street",
                new Zip(2323, "Test city"));
        // a3 has no persons by design

        Hobby h1 = new Hobby("Skiing", "skiing.com", "General", "Outdoors");
        Hobby h2 = new Hobby("Polo", "polo.com", "Sport", "Outdoors");
        Hobby h3 = new Hobby("Jogging", "jogging.com", "General", "Outdoors");

        p1 = new Person(
                new ArrayList<Phone>(Arrays.asList(new Phone[]{new Phone(11111111)})),
                "bob@bob.com",
                "Bob",
                "Roberts", a1);

        p1.addHobby(h1);
        p1.addHobby(h2);

        List<Phone> phones2 = new ArrayList<>();
        phones2.add(new Phone(22222222));
        p2 = new Person(phones2,
                "alice@alice.com",
                "Alice",
                "Allison", a2);

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
            em.persist(a1);
            em.persist(a2);
            em.persist(a3);
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    void createNewZip() {
        AddressDTO addressDTO = new AddressDTO("21 Jump Street",
                new ZipDTO(1515, "Cowabunga city"));
        WebApplicationException e = assertThrows(WebApplicationException.class,
                () -> facade.create(addressDTO));
        assertEquals(404, e.getResponse().getStatus());
        assertEquals("ZIP code " + addressDTO.getZip().getId() + " not found.", e.getMessage());
    }

    @Test
    void createExistingZip() {
        AddressDTO addressDTO = new AddressDTO("21 Jump Street",
                new ZipDTO(a1.getZip()));
        AddressDTO created = facade.create(addressDTO);
        assertNotNull(created);
        assertTrue(created.hasId());

        AddressDTO fromDb = facade.getById(created.getId());

        assertEquals(addressDTO.getAddress(), created.getAddress());
        assertEquals(addressDTO.getZip().getId(), created.getZip().getId());
        assertEquals(addressDTO.getZip().getCity(), created.getZip().getCity());

        assertEquals(addressDTO.getAddress(), fromDb.getAddress());
        assertEquals(addressDTO.getZip().getId(), fromDb.getZip().getId());
        assertEquals(addressDTO.getZip().getCity(), fromDb.getZip().getCity());
    }

    @Test
    void createEqualToExistingZip() {
        AddressDTO addressDTO = new AddressDTO("21 Jump Street",
                new ZipDTO(a1.getZip().getZip(), a1.getZip().getCity()));
        AddressDTO created = facade.create(addressDTO);
        assertNotNull(created);
        assertTrue(created.hasId());

        AddressDTO fromDb = facade.getById(created.getId());

        assertEquals(addressDTO.getAddress(), created.getAddress());
        assertEquals(addressDTO.getZip().getId(), created.getZip().getId());
        assertEquals(addressDTO.getZip().getCity(), created.getZip().getCity());

        assertEquals(addressDTO.getAddress(), fromDb.getAddress());
        assertEquals(addressDTO.getZip().getId(), fromDb.getZip().getId());
        assertEquals(addressDTO.getZip().getCity(), fromDb.getZip().getCity());
    }

    @Test
    void edit() {
        EntityManager em = emf.createEntityManager();

        a1.setAddress("Fest street 21");
        AddressDTO a1DTO = new AddressDTO(a1);

        facade.update(a1DTO);
        assertEquals("Fest street 21", facade.getById(a1.getId()).getAddress());

        Address address = em.find(Address.class, a1.getId());
        assertEquals(1, address.getPersons().size());

        PersonDTO person = PERSON_FACADE.getById(p1.getId());

        assertTrue(address.equals(person.getAddress()));
    }

    @Test
    void edit_thenDelete_HasPerson() {
        EntityManager em = emf.createEntityManager();

        a1.setAddress("Fest street 21");
        AddressDTO a1DTO = new AddressDTO(a1);

        facade.update(a1DTO);
        Address a = em.find(Address.class, a1.getId());
        assertEquals(1, a.getPersons().size());
        WebApplicationException e = assertThrows(WebApplicationException.class, () ->
                facade.delete(a1.getId()));
        assertEquals(400, e.getResponse().getStatus());
        assertEquals("Address has persons.", e.getMessage());
    }
    
    @Test
    void delete() throws Exception
    {
        facade.delete(a3.getId());
        List<AddressDTO> addresses = facade.getAll();
        assertEquals(2, addresses.size());
    }
    
    @Test
    void delete_addressHasPerson()
    {
        WebApplicationException e = assertThrows(WebApplicationException.class, () ->
                facade.delete(a2.getId()));
        assertEquals(400, e.getResponse().getStatus());
        assertEquals("Address has persons.", e.getMessage());
    }

    @Test
    void getById() {
        AddressDTO address = facade.getById(a2.getId());
        assertTrue(a2.equals(address));
    }

    @Test
    void getByZip() {
        ZipDTO zip = new ZipDTO(a3.getZip());
        List<AddressDTO> addresses = facade.getByZip(zip);
        assertEquals(1, addresses.size());
        assertTrue(a3.equals(addresses.get(0)));
    }

    @Test
    void getAddressCount() {
        assertEquals(3, facade.getAddressCount());
    }

    @Test
    void getAll() {
        List<AddressDTO> addresses = facade.getAll();
        assertEquals(3, addresses.size());
        addresses.forEach(a -> {
            if (a1.getId() == a.getId())
                assertTrue(a1.equals(a));
            else if (a2.getId() == a.getId())
                assertTrue(a2.equals(a));
            else if (a3.getId() == a.getId())
                assertTrue(a3.equals(a));
        });
    }
}