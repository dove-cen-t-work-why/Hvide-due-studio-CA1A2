package facades;

import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.ZipDTO;
import entities.Address;
import entities.Person;
import entities.Zip;
import facades.inter.AddressFacadeInterface;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;


public class AddressFacade implements AddressFacadeInterface {

    private static AddressFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private AddressFacade() {
    }

    public static AddressFacade getAddressFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AddressFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public AddressDTO create(AddressDTO addressDTO) {
        EntityManager em = emf.createEntityManager();
        Zip zip = em.find(Zip.class, addressDTO.getZip().getId());
        if (zip == null)
            throw new WebApplicationException("ZIP code " + addressDTO.getZip().getId() + " not found.", 404);
        Address address = new Address(addressDTO.getAddress(), zip);
        try {
            em.getTransaction().begin();
            em.persist(address);
            em.getTransaction().commit();
            return new AddressDTO(address);
        } catch (Exception e) {
            throw new WebApplicationException("Transaction failed.", 500);
        } finally {
            em.close();
        }
    }

    @Override
    public AddressDTO update(AddressDTO addressDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            Address original = em.find(Address.class, addressDTO.getId());
            if (original == null) throw new WebApplicationException("Address not found", 404);
            Address address = new Address(addressDTO);
                /* More complex, bidirectional removal and setting. Unneeded in this situation.
                List<Person> persons = getRealPersons(em, original.getPersons());
                address.setPersonsBi(persons); */
            address.setPersonsUnidirectional(original.getPersons());
            try {
                em.getTransaction().begin();
                em.merge(address);
                em.getTransaction().commit();
                return new AddressDTO(address);
            } catch (Exception e) {
                throw new WebApplicationException("Transaction failed", 500);
            }
        } finally {
            em.close();
        }
    }

    // only needed if we're using the bidirectional Set method when updating Addresses.
    // TODO: ERROR HANDLING IF IN USE
    private List<Person> getRealPersons(EntityManager em, List<Person> addressPersons) {
        List<Person> realPersons = new ArrayList<>();
        addressPersons.forEach(person -> {
            Person newPerson = em.find(Person.class, person.getId());
            if (person != null) {
                realPersons.add(newPerson);
            }
        });
        return realPersons;
    }

    @Override
    public AddressDTO delete(long id) {
        EntityManager em = emf.createEntityManager();
        Address address = em.find(Address.class, id);
        if (address == null) throw new WebApplicationException("Address not found", 404);
        AddressDTO addressDTO = new AddressDTO(address);
        if (!address.getPersons().isEmpty()) {
            throw new WebApplicationException("Address has persons.", 400);
        }
        try {
            em.getTransaction().begin();
            em.remove(address);
            em.getTransaction().commit();
            return addressDTO;
        } catch (Exception e) {
            throw new WebApplicationException("Transaction failed.", 500);
        } finally {
            em.close();
        }
    }

    @Override
    public AddressDTO getById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Address address = em.find(Address.class, id);
            if (address == null) throw new WebApplicationException("Address not found", 404);
            return new AddressDTO(address);
        } finally {
            em.close();
        }
    }

    public AddressDTO getByFields(AddressDTO addressDTO) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Address> query = em.createQuery("SELECT a FROM Address a WHERE a.address = :address AND a.zip.zip = :zip", Address.class);
        query.setParameter("address", addressDTO.getAddress());
        query.setParameter("zip", addressDTO.getZip().getId());
        try {
            Address address = query.getSingleResult();
            return new AddressDTO(address);
        } catch (NoResultException e) {
            throw new WebApplicationException("Address not found.", 404);
        } finally {
            em.close();
        }

    }

    @Override
    public List<AddressDTO> getByZip(ZipDTO zipDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            Zip zip = new Zip(zipDTO);
            TypedQuery<Address> query = em.createQuery("SELECT a FROM Address a WHERE a.zip = :zip", Address.class);
            query.setParameter("zip", zip);
            List<Address> addresses = query.getResultList();
            if (addresses.size() == 0) throw new WebApplicationException("Addresses not found.", 404);
            return AddressDTO.getDtos(addresses);
        } finally {
            em.close();
        }
    }

    @Override
    public AddressDTO getByPerson(PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();
        Person person = new Person(personDTO);
        TypedQuery<Address> query = em.createQuery("SELECT a FROM Address a JOIN Person p WHERE a = p.address AND p = :person", Address.class);
        query.setParameter("person", person);
        try {
            Address address = query.getSingleResult();
            return new AddressDTO(address);
        } catch (NoResultException e) {
            throw new WebApplicationException("Address not found.", 404);
        } finally {
            em.close();
        }
    }

    @Override
    public long getAddressCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long addressCount = (long) em.createQuery("SELECT COUNT(a) FROM Address a").getSingleResult();
            return addressCount;
        } catch (NoResultException e) {
            throw new WebApplicationException("Address databases empty", 404);
        } finally {
            em.close();
        }
    }

    @Override
    public List<AddressDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Address> query = em.createQuery("SELECT a FROM Address a", Address.class);
            List<Address> addresses = query.getResultList();
            if (addresses.size() == 0) throw new WebApplicationException("Addresses not found", 404);
            return AddressDTO.getDtos(addresses);
        } finally {
            em.close();
        }
    }

    /*public AddressDTO create(AddressDTO addressDTO, PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();

        try{
            Person person = new Person(personDTO);
            if(em.find(Person.class, person.getId()) != null) {
            Address address = new Address(addressDTO);
                if(getById(address.getId()) == null) {
                    em.getTransaction().begin();
                    em.persist(address);
                }

        }

    }              */  // add address?

    public static void main(String[] args) {
        emf = EMF_Creator.createEntityManagerFactory();
        AddressFacade af = getAddressFacade(emf);
        af.getAll().forEach(dto -> System.out.println(dto));
    }
}
