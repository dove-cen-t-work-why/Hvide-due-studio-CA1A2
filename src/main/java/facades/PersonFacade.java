package facades;

import dtos.*;
import entities.*;
import facades.inter.PersonFacadeInterface;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;

public class PersonFacade implements PersonFacadeInterface {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public PersonDTO create(PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();

        Address address = getAddressOrCreateNew(em, personDTO.getAddress());
        List<Hobby> hobbies = getHobbiesFromDTOs(em, personDTO.getHobbies());

        Person person = new Person(personDTO);
        person.setAddress(address);
        person.setHobbies(hobbies);

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
            return new PersonDTO(person);
        } catch (Exception e) {
            throw new WebApplicationException("Transaction failed", 500);
        } finally {
            em.close();
        }
    }

    // gets managed entity for each hobby in the DTO
    private List<Hobby> getHobbiesFromDTOs(EntityManager em, List<HobbyDTO> hobbyDTOs) {
        List<Hobby> hobbies = new ArrayList<>();
        hobbyDTOs.forEach(hobbyDTO -> {
            Hobby hobby = em.find(Hobby.class, hobbyDTO.getId());
            if (hobby != null) hobbies.add(hobby);
            else throw new WebApplicationException("Hobby not found", 404);
        });
        return hobbies;
    }

    // check if address already exist. If it doesn't, create new address and get managed entity.
    private Address getAddressOrCreateNew(EntityManager em, AddressDTO addressDTO) {
        AddressFacade ADDRESS_FACADE = AddressFacade.getAddressFacade(emf);
        AddressDTO newAddressDTO = null;
        try {
            newAddressDTO = ADDRESS_FACADE.getByFields(addressDTO);
        } catch (WebApplicationException e) {
            newAddressDTO = ADDRESS_FACADE.create(addressDTO);
        }
        Address address = em.find(Address.class, newAddressDTO.getId());
        if (address == null) throw new WebApplicationException("Failed to get or create address, 500");
        else return address;

    }

    @Override
    public PersonDTO update(PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();
        // New person
        Person newPerson = new Person(personDTO);
        newPerson.setHobbies(getHobbiesFromDTOs(em, personDTO.getHobbies()));
        Address newAddress = getAddressOrCreateNew(em, personDTO.getAddress());
        newPerson.setAddress(newAddress);
        // Old person
        Person oldPerson = em.find(Person.class, personDTO.getId());
        if (oldPerson == null) throw new WebApplicationException("Person not found", 404);
        oldPerson.removeAllHobbies();
        Address oldAddress = oldPerson.getAddress();
        oldAddress.getPersons().remove(oldPerson);
        removeAddressIfChildless(em, oldAddress);

        // merge
        try {
            em.getTransaction().begin();
            em.merge(newPerson);
            em.getTransaction().commit();
            return new PersonDTO(newPerson);
        } catch (Exception e) {
            throw new WebApplicationException("Transaction failed", 500);
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO delete(long id) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null) throw new WebApplicationException("Person not found", 404);
        Address address = person.getAddress();
        try {
            em.getTransaction().begin();
            address.getPersons().remove(person);
            removeAddressIfChildless(em, address);
            em.remove(person);
            em.getTransaction().commit();
            em.clear();
            return new PersonDTO(person);
        } catch (Exception e) {
            throw new WebApplicationException("Transaction failed", 500);
        } finally {
            em.close();
        }
    }

    private void removeAddressIfChildless(EntityManager em, Address address) {
        try {
            if (address.getPersons().isEmpty()) em.remove(address);
        } catch (Exception e) {
            throw new WebApplicationException("Failed to delete empty address", 500);
        }
    }

    @Override
    public PersonDTO getById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class, id);
            if (person == null) throw new WebApplicationException("Person not found", 404);
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO getByPhone(PhoneDTO phoneDTO) {
        EntityManager em = emf.createEntityManager();

        Phone phone = new Phone(phoneDTO);
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE :phone MEMBER OF p.phones", Person.class);
        query.setParameter("phone", phone);
        try {
            Person person = query.getSingleResult();
            return new PersonDTO(person);
        } catch (NoResultException e) {
            throw new WebApplicationException("Person not found", 404);
        } finally {
            em.close();
        }
    }

    @Override
    public List<PersonDTO> getByHobby(HobbyDTO hobbyDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            Hobby hobby = new Hobby(hobbyDTO);
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE :hobby MEMBER OF p.hobbies", Person.class);
            query.setParameter("hobby", hobby);
            List<Person> persons = query.getResultList();
            if (persons.size() == 0) throw new WebApplicationException("Persons not found", 404);
            return PersonDTO.getDtos(persons);
        } finally {
            em.close();
        }
    }

    @Override
    public List<PersonDTO> getByAddress(AddressDTO addressDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            Address address = new Address(addressDTO);
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.address = :address", Person.class);
            query.setParameter("address", address);
            List<Person> persons = query.getResultList();
            if (persons.size() == 0) throw new WebApplicationException("Persons not found", 404);
            return PersonDTO.getDtos(persons);
        } finally {
            em.close();
        }
    }

    @Override
    public List<PersonDTO> getByZip(ZipDTO zipDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            Zip zip = new Zip(zipDTO);
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.address.zip = :zip", Person.class);
            query.setParameter("zip", zip);
            List<Person> persons = query.getResultList();
            if (persons.size() == 0) throw new WebApplicationException("Persons not found", 404);
            return PersonDTO.getDtos(persons);
        } finally {
            em.close();
        }
    }

    @Override
    public List<PersonDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
            List<Person> persons = query.getResultList();
            if (persons.size() == 0) throw new WebApplicationException("Persons not found", 404);
            return PersonDTO.getDtos(persons);
        } finally {
            em.close();
        }
    }

    @Override
    public long getPersonCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long personCount = (long) em.createQuery("SELECT COUNT(p) FROM Person p").getSingleResult();
            return personCount;
        } catch (NoResultException e) {
            throw new WebApplicationException("Person database empty", 404);
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        emf = EMF_Creator.createEntityManagerFactory();
        PersonFacade pf = getPersonFacade(emf);
        pf.getAll().forEach(dto -> System.out.println(dto));
    }
}
