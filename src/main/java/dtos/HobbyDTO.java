/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import entities.Hobby;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tha
 */
public class HobbyDTO extends DTO {
    private String name;
    private String link;
    private String category;
    private String type;

    public HobbyDTO(String name, String link, String category, String type) {
        this.name = name;
        this.link = link;
        this.category = category;
        this.type = type;
    }

    public static List<HobbyDTO> getDtos(List<Hobby> hobbies) {
        List<HobbyDTO> hobbyDTOs = new ArrayList();
        hobbies.forEach(hobby -> hobbyDTOs.add(new HobbyDTO(hobby)));
        return hobbyDTOs;
    }

    public HobbyDTO(Hobby hobby) {
        if (hobby.hasId())
            this.id = hobby.getId();
        this.name = hobby.getName();
        this.link = hobby.getLink();
        this.category = hobby.getCategory();
        this.type = hobby.getType();
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(Hobby entity) {
        if (getId() != entity.getId()) return false;
        if (!getName().equals(entity.getName())) return false;
        if (!getLink().equals(entity.getLink())) return false;
        if (!getCategory().equals(entity.getCategory())) return false;
        return getType().equals(entity.getType());

    }

    @Override
    public String toString() {
        return "id: " + id + " | name:" + name;
    }
}
