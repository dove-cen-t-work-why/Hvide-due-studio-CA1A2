/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import entities.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tha
 */
public class AddressDTO extends DTO {
    private String address;
    private ZipDTO zip;

    public AddressDTO(String address, ZipDTO zip) {
        this.address = address;
        this.zip = zip;
    }

    public static List<AddressDTO> getDtos(List<Address> addresses) {
        List<AddressDTO> addressDTOs = new ArrayList();
        addresses.forEach(address -> addressDTOs.add(new AddressDTO(address)));
        return addressDTOs;
    }

    public AddressDTO(Address address) {
        if (address.hasId())
            this.id = address.getId();
        this.address = address.getAddress();
        this.zip = new ZipDTO(address.getZip());
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ZipDTO getZip() {
        return zip;
    }

    public void setZip(ZipDTO zip) {
        this.zip = zip;
    }

    public boolean equals(Address entity) {
        if (getId() != entity.getId()) return false;
        if (!getAddress().equals(entity.getAddress())) return false;
        return getZip().equals(entity.getZip());

    }
}

