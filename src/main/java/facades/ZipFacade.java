package facades;

import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.ZipDTO;
import entities.Address;
import entities.Person;
import entities.Zip;
import facades.inter.ZipFacadeInterface;
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
public class ZipFacade implements ZipFacadeInterface {

    private static ZipFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private ZipFacade() {
    }

    public static ZipFacade getZipFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ZipFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public ZipDTO create(ZipDTO zipDTO) {
        EntityManager em = emf.createEntityManager();
        try {
            Zip zip = new Zip(zipDTO);
            em.getTransaction().begin();
            em.persist(zip);
            em.getTransaction().commit();
            return new ZipDTO(zip);
        } catch(Exception e) {
            throw new WebApplicationException("Transaction failed.", 500);
        } finally {
            em.close();
        }
    }

    @Override
    public ZipDTO update(ZipDTO Zip) {
        return null;
    }

    @Override
    public ZipDTO delete(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Zip zip = em.find(Zip.class, id);
            if (zip == null) throw new WebApplicationException("Zip not found.", 404);
            ZipDTO zipDTO = new ZipDTO(zip);
            em.remove(new ZipDTO(zip));
            return zipDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public ZipDTO getByZip(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Zip zip = em.find(Zip.class, id);
            if (zip == null) throw new WebApplicationException("Zip not found", 404);
            return new ZipDTO(zip);
        } finally {
            em.close();
        }
    }

    @Override
    public ZipDTO getByPerson(PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();
        Person person = new Person(personDTO);
        TypedQuery<Zip> query = em.createQuery("SELECT z FROM Zip z JOIN Person p WHERE p.address.zip = z AND p = :person", Zip.class);
        query.setParameter("person", person);
        try {
            Zip zip = query.getSingleResult();
            return new ZipDTO(zip);
        } catch (NoResultException e) {
            throw new WebApplicationException("Zip not found.", 404);
        } finally {
            em.close();
        }
    }

    @Override
    public ZipDTO getByAddress(AddressDTO addressDTO) {
        EntityManager em = emf.createEntityManager();

        Address address = new Address(addressDTO);
        TypedQuery<Zip> query = em.createQuery("SELECT z FROM Zip z JOIN Address a WHERE a.zip = z AND a = :address", Zip.class);
        query.setParameter("address", address);
        try {
            Zip zip = query.getSingleResult();
            return new ZipDTO(zip);
        } catch (NoResultException e) {
            throw new WebApplicationException("Zip not found.", 404);
        } finally {
            em.close();
        }
    }

    @Override
    public List<ZipDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Zip> query = em.createQuery("SELECT z FROM Zip z", Zip.class);
            List<Zip> zips = query.getResultList();
            if (zips.size() == 0) throw new WebApplicationException("Zips not found", 404);
            return ZipDTO.getDtos(zips);
        } finally {
            em.close();
        }
    }

    @Override
    public long getZipCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long zipCount = (long) em.createQuery("SELECT COUNT(z) FROM Zip z").getSingleResult();
            return zipCount;
        } catch (NoResultException e) {
            throw new WebApplicationException("Zip database empty", 404);
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        emf = EMF_Creator.createEntityManagerFactory();
        ZipFacade zf = getZipFacade(emf);
        zf.getAll().forEach(dto -> System.out.println(dto));
    }
}
