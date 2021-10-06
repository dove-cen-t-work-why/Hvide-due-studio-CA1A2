/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Alex & Johan
 * MANUALLY DROP TABLE ADDRESS, PERSON, PERSON_HOBBY & PHONE BEFORE RUNNING (KEEP HOBBY & ZIP)
 */
public class PopulatorPerson {

    public static void main(String[] args) {
        populate();
    }

    private static List<Hobby> getHobbies(EntityManager em) {
        TypedQuery<Hobby> query = em.createQuery("SELECT h FROM Hobby h", Hobby.class);
        return (List<Hobby>) query.getResultList();
    }

    private static List<Zip> getZips(EntityManager em) {
        TypedQuery<Zip> query = em.createQuery("SELECT z FROM Zip z", Zip.class);
        return (List<Zip>) query.getResultList();
    }

    private static int getRandomNumber() {
        Random rand = new Random();
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            num.append(rand.nextInt(10));
        }
        return Integer.parseInt(num.toString());
    }

    private static Person buildPerson(String email, String fname, String lname, String address, List<Zip> zips, List<Hobby> hobbies) {
        Random rand = new Random();
        List<Phone> pho = new ArrayList<>();
        List<Hobby> hob = new ArrayList();
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < 8; i++) num.append(rand.nextInt(10));
        pho.add(new Phone(getRandomNumber()));
        pho.add(new Phone(getRandomNumber(), "work"));
        hob.add(hobbies.get(rand.nextInt(hobbies.size())));
        hob.add(hobbies.get(rand.nextInt(hobbies.size())));
        return new Person(pho, email, fname, lname, new Address(address, zips.get(rand.nextInt(zips.size()))), hob);
    }

    public static String populate() {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        try {
            List<Hobby> hobbies = getHobbies(em);
            List<Zip> zips = getZips(em);
            List<Person> persons = new ArrayList();

            persons.add(buildPerson(
                    "bente@bentsenogco.dk",
                    "Bente",
                    "Bentsen",
                    "Lysallen 246",
                    zips, hobbies));

            persons.add(buildPerson(
                    "gammelSteen@hotmail.com",
                    "Steen",
                    "Aldermann",
                    "Skuldelevvej 6, st. t.h.",
                    zips, hobbies));

            persons.add(buildPerson(
                    "kasper@christensen.dk",
                    "Kasper",
                    "Christensen",
                    "Rolighedsvej 12, 3. t.v.",
                    zips, hobbies));

            persons.add(buildPerson(
                    "mmmonaco@gmail.com",
                    "Monica",
                    "Kirkegaard",
                    "Kirsebærsvej 101, 1. t.h.",
                    zips, hobbies));

            persons.add(buildPerson(
                    "malibro@hotmail.dk",
                    "Malou",
                    "Brohammer",
                    "Vestergade 19, 5. t.v.",
                    zips, hobbies));

            persons.add(buildPerson(
                    "mikki1999@gmail.com",
                    "Mikkel",
                    "Trove",
                    "Skomagervej 164",
                    zips, hobbies));

            persons.add(buildPerson(
                    "eriksvend72@gmail.com",
                    "Erik",
                    "Svendsen",
                    "Teglvej 5",
                    zips, hobbies));
            persons.add(buildPerson(
                    "mjensen@bejensen.dk",
                    "Marie",
                    "Jensen",
                    "Vigevej 12, 2. t.v.",
                    zips, hobbies));
            persons.add(buildPerson(
                    "pnpnpn9@gmail.com",
                    "Peter",
                    "Nielsen",
                    "Vinkelvej 201B",
                    zips, hobbies));
            persons.add(buildPerson(
                    "christian@thansen.dk",
                    "Christian",
                    "Hansen",
                    "Engvej 76 st. t.h.",
                    zips, hobbies));
            persons.add(buildPerson(
                    "ap@net.dk",
                    "Anna",
                    "Pedersen",
                    "Tværvej 16",
                    zips, hobbies));
            persons.add(buildPerson(
                    "erik@degulesider.dk",
                    "Erik",
                    "Andersen",
                    "Skovvej 33",
                    zips, hobbies));
            persons.add(buildPerson(
                    "jensc81@gmail.com",
                    "Jens",
                    "Christensen",
                    "Drosselvej 2",
                    zips, hobbies));
            persons.add(buildPerson(
                    "hans@hanslarsen.dk",
                    "Hans",
                    "Larsen",
                    "Mågevej 21 2 t.h.",
                    zips, hobbies));
            persons.add(buildPerson(
                    "maggiv61@gmail.com",
                    "Margrethe",
                    "Vestergaard",
                    "Toftevej 50",
                    zips, hobbies));
            persons.add(buildPerson(
                    "nb@borgen.dk",
                    "Niels",
                    "Bach",
                    "Kastanievej 4",
                    zips, hobbies));
            persons.add(buildPerson(
                    "klauritsen@irma.dk",
                    "Karen",
                    "Lauritsen",
                    "Violvej 13",
                    zips, hobbies));
            persons.add(buildPerson(
                    "kriskro5@gmail.com",
                    "Kristian",
                    "Krogh",
                    "Plantagevej 21",
                    zips, hobbies));
            persons.add(buildPerson(
                    "jm@cphpost.dk",
                    "Johanne",
                    "Mathiasen",
                    "Rosenvænget 17B",
                    zips, hobbies));
            persons.add(buildPerson(
                    "familienpulsen@hotmail.dk",
                    "Henrik",
                    "Paulsen",
                    "Toften 6",
                    zips, hobbies));
            persons.add(buildPerson(
                    "aageovergaard01@gmail.com",
                    "Aage",
                    "Overgaard",
                    "Grønningen 44",
                    zips, hobbies));
            persons.add(buildPerson(
                    "es@schouit.dk",
                    "Else",
                    "Schou",
                    "Kirkebakken 9",
                    zips, hobbies));
            persons.add(buildPerson(
                    "lb@dmi.dk",
                    "Lars",
                    "Berg",
                    "Grønnegade 12, 4. t.v.",
                    zips, hobbies));
            persons.add(buildPerson(
                    "anders@bentsenbusiness.dk",
                    "Anders",
                    "Bendtsen",
                    "Søvej 16",
                    zips, hobbies));
            persons.add(buildPerson(
                    "tomheat12@gmail.com",
                    "Thomas",
                    "Hedegaard",
                    "Smedevej 36",
                    zips, hobbies));
            persons.add(buildPerson(
                    "masterthygger@hotmail.com",
                    "Knud",
                    "Thygesen",
                    "Rønnevej 1",
                    zips, hobbies));
            persons.add(buildPerson(
                    "hanne@hannekruse.dk",
                    "Hanne",
                    "Kruse",
                    "Vestervang 87",
                    zips, hobbies));
            persons.add(buildPerson(
                    "sobjer9@hotmail.com",
                    "Sofie",
                    "Bjerregaard",
                    "Kirkegade 11B",
                    zips, hobbies));
            persons.add(buildPerson(
                    "jsjan72@gmail.com",
                    "Jan",
                    "Svensson",
                    "Præstegårdsvej 3",
                    zips, hobbies));

            em.getTransaction().begin();
            // hopefully there's a better way than use a native query to wipe out the join table
            try {
                /*
                em.createNativeQuery("DELETE FROM PERSON_PHONE").executeUpdate();
                em.createNativeQuery("DELETE FROM PERSON_HOBBY").executeUpdate();
                em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
                em.createNamedQuery("Person.deleteAllRows").executeUpdate();
                em.createNamedQuery("Person.resetPK").executeUpdate();
                em.createNamedQuery("Address.deleteAllRows").executeUpdate();
                */
            } catch(Exception e) {
                System.out.println("Some delete queries failed to execute");
                e.printStackTrace();
            }
            for (Person person : persons) {
                em.persist(person);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            em.close();
        }
        return "Succes";
    }
}