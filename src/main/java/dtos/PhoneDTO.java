/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import entities.Phone;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tha
 */
public class PhoneDTO extends DTO {
    private int number;
    private String info;

    public PhoneDTO(int number, String info) {
        this.number = number;
        this.info = info;
    }

    public static List<PhoneDTO> getDtos(List<Phone> phones) {
        List<PhoneDTO> phoneDTOs = new ArrayList();
        phones.forEach(phone -> phoneDTOs.add(new PhoneDTO(phone)));
        return phoneDTOs;
    }

    public PhoneDTO(Phone phone) {
        if (phone.hasId())
            this.id = phone.getId();
        this.number = phone.getNumber();
        this.info = phone.getInfo();
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

    public boolean equals(Phone entity) {
        if (getNumber() != entity.getNumber()) return false;
        if (getId() != entity.getId()) return false;
        return getInfo().equals(entity.getInfo());
    }
}
