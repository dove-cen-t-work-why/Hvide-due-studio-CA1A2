package entities;

import dtos.DTO;
import dtos.DTOList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EntList {
    private List<Ent> list;

    public EntList() {
        list = new ArrayList<>();
    }

    public EntList(List<Ent> list) {
        this.list = new ArrayList<>();
        this.list.addAll(list);
    }

    public EntList(Ent... ents) {
        this.list = new ArrayList<>();
        this.list.addAll(Arrays.asList(ents));
    }

    public boolean equals(DTOList w) {
        return equals(w.getList());
    }

    public boolean equals(Collection<DTO> c) {
        for (DTO dto : c) {
            boolean hasEqual = false;
            for (Ent ent : list) {
                if (dto.equals(ent)) {
                    hasEqual = true;
                    break;
                }
            }
            if (!hasEqual) return false;
        }
        return true;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int indexOf(Ent ent) {
        return list.indexOf(ent);
    }

    public boolean add(Ent ent) {
        return list.add(ent);
    }

    public boolean addAll(Collection<Ent> c) {
        return list.addAll(c);
    }

    public boolean remove(Ent ent) {
        return list.remove(ent);
    }

    public Ent remove(int index) {
        return list.remove(index);
    }

    public boolean removeAll(Collection<Ent> c) {
        return list.removeAll(c);
    }

    public void clear() {
        list = new ArrayList<>();
    }

    public boolean contains(Ent ent) {
        return list.contains(ent);
    }

    public boolean containsAll(Collection<Ent> c) {
        return list.containsAll(c);
    }

    public Ent get(int index) {
        return list.get(index);
    }

    public List<Ent> getList() {
        return list;
    }

    public Ent set(int index, Ent ent) {
        return list.set(index, ent);
    }

    public List<Ent> setList(List<Ent> list) {
        return list = list;
    }
}
