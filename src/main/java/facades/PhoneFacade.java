package facades;

import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.Person;
import entities.Phone;
import facades.inter.PhoneFacadeInterface;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.util.List;

/**
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PhoneFacade implements PhoneFacadeInterface {

    private static PhoneFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PhoneFacade() {
    }

    public static PhoneFacade getPhoneFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PhoneFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public PhoneDTO create(PhoneDTO phoneDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            Phone phone = new Phone(phoneDTO);
            em.getTransaction().begin();
            em.persist(phone);
            em.getTransaction().commit();
            return new PhoneDTO(phone);
        } catch (Exception e) {
            throw new WebApplicationException("Transaction failed", 500);
        } finally {
            em.close();
        }
    }

    @Override
    public PhoneDTO update(PhoneDTO phoneDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            Phone phone = new Phone(phoneDTO);
            if (phoneDTO.getId() == getById(phoneDTO.getId()).getId()) {
                em.getTransaction().begin();
                em.merge(phone);
                em.getTransaction().commit();
                return new PhoneDTO(phone);
            } else throw new WebApplicationException("Phone not found", 404);
        } catch (Exception e) {
            throw new WebApplicationException("Transaction failed", 500);
        } finally {
            em.close();
        }
    }

    @Override
    public PhoneDTO delete(long id) {
        EntityManager em = emf.createEntityManager();
        Phone phone = em.find(Phone.class, id);
        if (phone == null) throw new WebApplicationException("Phone not found, 404");
        PhoneDTO phoneDTO = new PhoneDTO(phone);
        try {
            Person person = em.createQuery("SELECT p FROM Person p WHERE :phone MEMBER OF p.phones", Person.class)
                    .setParameter("phone", phone)
                    .getSingleResult();
            person.removePhone(phone);
            em.getTransaction().begin();
            em.remove(phone);   // orphan removal also takes care of this so explicitly removing the phone is unneeded.
            em.getTransaction().commit();
            return phoneDTO;
        } catch (Exception e) {
            if (e instanceof NoResultException) throw new WebApplicationException("Phone not found, 404");
            else throw new WebApplicationException("Transaction failed, 500");
        } finally {
            em.close();
        }
    }

    @Override
    public PhoneDTO getById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Phone phone = em.find(Phone.class, id);
            if (phone == null) throw new WebApplicationException("Phone not found", 404);
            return new PhoneDTO(phone);
        } finally {
            em.close();
        }
    }

    @Override
    public PhoneDTO getByNumber(Integer number) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.number = :number", Phone.class);
        query.setParameter("number", number);
        try {
            Phone phone = query.getSingleResult();
            return new PhoneDTO(phone);
        } catch (NoResultException e) {
            throw new WebApplicationException("Phone not found, 404");
        } finally {
            em.close();
        }
    }

    @Override
    public List<PhoneDTO> getByPerson(PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();
        Person person = new Person(personDTO);
        TypedQuery<Phone> query = em.createQuery("SELECT pho FROM Phone pho JOIN Person pers WHERE pers = :person and pho MEMBER OF pers.phones", Phone.class);
        query.setParameter("person", person);
        try {
            List<Phone> phones = query.getResultList();
            if (phones.size() == 0) throw new WebApplicationException("Phones not found", 404);
            return PhoneDTO.getDtos(phones);
        } finally {
            em.close();
        }

    }

    @Override
    public List<PhoneDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p", Phone.class);
            List<Phone> phones = query.getResultList();
            if (phones.size() == 0) throw new WebApplicationException("Phones not found", 404);
            return PhoneDTO.getDtos(phones);
        } finally {
            em.close();
        }
    }

    @Override
    public long getPhoneCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long phoneCount = (long) em.createQuery("SELECT COUNT(p) FROM Phone p").getSingleResult();
            return phoneCount;
        } catch (NoResultException e) {
            throw new WebApplicationException("Phone database empty", 404);
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        emf = EMF_Creator.createEntityManagerFactory();
        PhoneFacade pf = getPhoneFacade(emf);
        pf.getAll().forEach(dto -> System.out.println(dto));
    }
}
