package facades;

import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.Address;
import entities.Person;
import entities.Phone;
import entities.Zip;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhoneFacadeTest {
    private static EntityManagerFactory emf;
    private static PhoneFacade facade;

    private static Phone p1, p2;
    private static Person pe1, pe2;

    @BeforeAll
    static void beforeAll() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PhoneFacade.getPhoneFacade(emf);
    }

    @BeforeEach
    void setUp() {
        EntityManager em = emf.createEntityManager();

        p1 = new Phone(11111111, "Business");
        p2 = new Phone(22222222, "Pleasure");
        
        pe1 = new Person(
                Arrays.asList(p1),
                "bob@bob.com",
                "Bob",
                "Roberts",
                new Address("Test street 21",
                        new Zip(6969, "Nice-ville")));
    
        pe2 = new Person(
                Arrays.asList(p2),
                "alice@alice.com",
                "Alice",
                "Allison",
                new Address("2nd and Hill 34",
                        new Zip(4242, "Cool-town")));

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
            em.persist(pe1);
            em.persist(pe2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    void create() {
    }

    @Test
    void edit() {
        p1.setInfo("Bees knees");
        PhoneDTO p1DTO = new PhoneDTO(p1);

        facade.update(p1DTO);
        assertEquals("Bees knees", facade.getById(p1.getId()).getInfo());
    }

    @Test
    void delete() {
        facade.delete(p2.getId());
        List<PhoneDTO> phones = facade.getAll();
        assertEquals(1, phones.size());
        assertEquals(p1.getInfo(), phones.get(0).getInfo());
        
        PersonFacade personFacade = PersonFacade.getPersonFacade(emf);
        PersonDTO newPe2 = personFacade.getById(pe2.getId());
        assertEquals(0, newPe2.getPhones().size());
    }

    @Test
    void getById() {
        PhoneDTO phone = facade.getById(p1.getId());
        assertTrue(phone.equals(p1));
    }

    @Test
    void getByNumber() {
        PhoneDTO phone = facade.getByNumber(p2.getNumber());
        assertTrue(phone.equals(p2));
    }

    @Test
    void getByPerson() {
        List<PhoneDTO> phones = facade.getByPerson(new PersonDTO(pe2));
        assertEquals(1, phones.size());
        assertTrue(p2.equals(phones.get(0)));
    }

    @Test
    void getAll() {
        List<PhoneDTO> phones = facade.getAll();
        assertEquals(2, phones.size());
        
        // clunky way of checking if it contains equivalent phones
        for (PhoneDTO dto : phones)
        {
            if (dto.getId() == p1.getId())
            {
                assertTrue(dto.equals(p1));
            }
            else if (dto.getId() == p2.getId())
            {
                assertTrue(dto.equals(p2));
            }
        }
    }

    @Test
    void getPhoneCount() {
        assertEquals(2, facade.getPhoneCount());
    }
}