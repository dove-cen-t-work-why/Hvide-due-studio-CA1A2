/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import entities.Zip;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tha
 */
public class ZipDTO extends DTO {
    private String city;

    public ZipDTO(long zip, String city) {
        this.id = zip;
        this.city = city;
    }

    public static List<ZipDTO> getDtos(List<Zip> zips) {

        List<ZipDTO> zipDTOs = new ArrayList();
        zips.forEach(zip -> zipDTOs.add(new ZipDTO(zip)));
        return zipDTOs;
    }

    public ZipDTO(Zip zip) {
            this.id = zip.getZip();
            this.city = zip.getCity();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean equals(Zip entity) {
        if (getId() != entity.getZip()) return false;
        return getCity().equals(entity.getCity());
    }
}

