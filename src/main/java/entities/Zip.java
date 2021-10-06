package entities;

import dtos.ZipDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(name = "ZIP")

@Entity
@NamedQuery(name = "Zip.deleteAllRows", query = "DELETE FROM Zip")
public class Zip implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // set manually
    long zip;
    private String city;
    
    @OneToMany(mappedBy = "zip")
    private List<Address> addresses;

    public Zip() {
    }

    public Zip(long zip, String city) {
        this.zip = zip;
        this.city = city;
        this.addresses = new ArrayList<>();
    }

    public Zip(ZipDTO zipDTO) {
        this.zip = zipDTO.getId();
        this.city = zipDTO.getCity();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public List<Address> getAddresses()
    {
        return addresses;
    }

    public boolean equals(ZipDTO dto) {
        if (getZip() != dto.getId()) return false;
        return getCity().equals(dto.getCity());
    }

    // Ent methods (pseudo-superclass)
    public long getZip() {
        return zip;
    }
    public void setZip(long zip) {
        this.zip = zip;
    }
    public boolean hasZip() {
        return zip != 0;
    }
}