package entities;

import dtos.HobbyDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Table(name = "HOBBY")

@Entity
@NamedQuery(name = "Hobby.deleteAllRows", query = "DELETE FROM Hobby")
public class Hobby extends Ent implements Serializable {

    private String name;
    private String link;
    private String category;
    private String type;

    @ManyToMany(mappedBy = "hobbies",
        cascade = CascadeType.MERGE)
    private List<Person> persons;

    public Hobby() {
    }

    public Hobby(String name, String link, String category, String type) {
        this.name = name;
        this.link = link;
        this.category = category;
        this.type = type;
        this.persons = new ArrayList<>();
    }

    public List<Hobby> updateHobbyDTOToEntity(List<HobbyDTO> hobbiesDTO) {
        List<Hobby> hobbies = new ArrayList<>();
        for (HobbyDTO h : hobbiesDTO) {
            hobbies.add(new Hobby(h));
        }
        return hobbies;
    }

    public Hobby(HobbyDTO hobbyDTO) {
        this.id = hobbyDTO.getId();
        this.name = hobbyDTO.getName();
        this.link = hobbyDTO.getLink();
        this.category = hobbyDTO.getCategory();
        this.type = hobbyDTO.getType();
        this.persons = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Person> getPersons()
    {
        return persons;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public void addPerson(Person person) {
        if (person != null) {
            this.persons.add(person);
            person.getHobbies().add(this);
        }
    }
    
    public void removePerson(Person person) {
        if (person != null) {
            this.persons.remove(person);
            person.getHobbies().remove(this);
        }
    }
    
    public void removeAllPersons() {
        Iterator<Person> iterator = persons.iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            if (person != null) {
                iterator.remove();
                person.getHobbies().remove(this);
            }
        }
    }

    public void setPersonsBi(List<Person> persons) {
        removeAllPersons();
        persons.forEach(this::addPerson);
    }

    public void setPersonsUnidirectional(List<Person> persons) {
        this.persons = persons;
    }

    public boolean equals(HobbyDTO dto) {
        if (getId() != dto.getId()) return false;
        if (!getName().equals(dto.getName())) return false;
        if (!getLink().equals(dto.getLink())) return false;
        if (!getCategory().equals(dto.getCategory())) return false;
        return getType().equals(dto.getType());
    }
}
