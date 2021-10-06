package dtos;

import java.io.Serializable;

abstract public class DTO implements Serializable {
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean hasId() {
        return id != 0;
    }
}
