package entities;

import dtos.PhoneDTO;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

// TODO see if it fails because of the table name in the query
@Table(name = "PHONE")

@Entity
@NamedQuery(name = "Phone.deleteAllRows", query = "DELETE FROM Phone")
public class Phone extends Ent implements Serializable {

    private int number;
    private String info;

    public Phone() {
    }

    public Phone(int number) {
        this.number = number;
        this.info = "personal";
    }

    public Phone(int number, String info) {
        this.number = number;
        this.info = info;
    }

    public Phone(PhoneDTO phoneDTO) {
        this.id = phoneDTO.getId();
        this.number = phoneDTO.getNumber();
        this.info = phoneDTO.getInfo();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean equals(PhoneDTO dto) {
        if (getNumber() != dto.getNumber()) return false;
        if (getId() != dto.getId()) return false;
        return getInfo().equals(dto.getInfo());
    }
}