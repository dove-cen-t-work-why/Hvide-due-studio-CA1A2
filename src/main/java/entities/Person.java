package entities;

import dtos.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Table(name = "PERSON")

@Entity
@NamedQuery(name = "Person.deleteAllRows", query = "DELETE FROM Person")
@NamedNativeQuery(name = "Person.resetPK", query = "ALTER TABLE PERSON AUTO_INCREMENT = 1")

public class Person extends Ent implements Serializable {

    private String email;
    private String firstName;
    private String lastName;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "PERSON_HOBBY")
    private List<Hobby> hobbies;

    @OneToMany(orphanRemoval = true,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "PERSON_PHONE")
    private List<Phone> phones;

    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    private Address address;

    public Person() {
    }

    public Person(PersonDTO personDTO){
        if(personDTO.hasId()) this.id = personDTO.getId();
        this.email = personDTO.getEmail();
        this.firstName = personDTO.getFirstName();
        this.lastName = personDTO.getLastName();
        setHobbiesFromDtoList(personDTO.getHobbies());
        setPhonesFromDtoList(personDTO.getPhones());
        setAddressFromDTO(personDTO.getAddress());
    }

    public Person(List<Phone> phones, String email, String firstName, String lastName, Address address, List<Hobby> hobbies) {
        this.phones = new ArrayList<>();
        phones.forEach(this::addPhone);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        setAddress(address);
        this.hobbies = new ArrayList<>();
        hobbies.forEach(this::addHobby);
    }

    public Person(List<Phone> phones, String email, String firstName, String lastName, Address address) {
        this.phones = new ArrayList<>();
        phones.forEach(this::addPhone);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        setAddress(address);
        this.hobbies = new ArrayList<>();
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public void addPhone(Phone phone) {
        if (phone != null) {
            this.phones.add(phone);
        }
    }

    public void removePhone(Phone phone) {
        if (phone != null) {
            this.phones.remove(phone);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        if (address != null) {
            if (this.address != null) {
                this.address.getPersons().remove(this);
            }
            this.address = address;
            address.getPersons().add(this);
        }
    }

    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<Hobby> hobbies) {
        removeAllHobbies();
        hobbies.forEach(this::addHobby);
    }

    public void addHobby(Hobby hobby) {
        if (hobby != null) {
            this.hobbies.add(hobby);
            hobby.getPersons().add(this);
        }
    }

    public void removeHobby(Hobby hobby) {
        if (hobby != null) {
            this.hobbies.remove(hobby);
            hobby.getPersons().remove(this);
        }
    }

    public void removeAllHobbies() {
        Iterator<Hobby> iterator = hobbies.iterator();
        while (iterator.hasNext()) {
            Hobby hobby = iterator.next();
            if (hobby != null) {
                iterator.remove();
                hobby.getPersons().remove(this);
            }
        }
    }

    public boolean equals(PersonDTO personDTO) {
        if (getId() != personDTO.getId()) return false;
        if (!getEmail().equals(personDTO.getEmail())) return false;
        if (!getFirstName().equals(personDTO.getFirstName())) return false;
        if (!getLastName().equals(personDTO.getLastName())) return false;
        for (Hobby ent : hobbies) {
            boolean hasEqual = false;
            for (HobbyDTO dto : personDTO.getHobbies()) {
                if (ent.equals(dto)) {
                    hasEqual = true;
                    break;
                }
            }
            if (!hasEqual) return false;
        }
        for (Phone ent : phones) {
            boolean hasEqual = false;
            for (PhoneDTO dto : personDTO.getPhones()) {
                if (ent.equals(dto)) {
                    hasEqual = true;
                    break;
                }
            }
            if (!hasEqual) return false;
        }
        return getAddress().equals(personDTO.getAddress());
    }

    public void setHobbiesFromDtoList(List<HobbyDTO> hobbyList) {
        hobbies = new ArrayList<>();
        for (HobbyDTO dto : hobbyList) hobbies.add(new Hobby(dto));
    }

    public void setPhonesFromDtoList(List<PhoneDTO> phoneList) {
        phones = new ArrayList<>();
        for (PhoneDTO dto : phoneList) phones.add(new Phone(dto));
    }

    public void setAddressFromDTO(AddressDTO addressDTO) {
        this.address = new Address(addressDTO);
    }
}
