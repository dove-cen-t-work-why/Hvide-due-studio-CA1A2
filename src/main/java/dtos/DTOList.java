package dtos;

import entities.Ent;
import entities.EntList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DTOList {
    private List<DTO> list;

    public DTOList() {
        list = new ArrayList<>();
    }

    public DTOList(List<DTO> list) {
        this.list = new ArrayList<>();
        this.list.addAll(list);
    }

    public DTOList(DTO... dtos) {
        this.list = new ArrayList<>();
        this.list.addAll(Arrays.asList(dtos));
    }

    public boolean equals(EntList w) {
        return equals(w.getList());
    }

    public boolean equals(Collection<Ent> c) {
        for (Ent ent : c) {
            boolean hasEqual = false;
            for (DTO dto : list) {
                if (ent.equals(dto)) {
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

    public int indexOf(DTO o) {
        return list.indexOf(o);
    }

    public boolean add(DTO o) {
        return list.add(o);
    }

    public boolean addAll(Collection<DTO> c) {
        return list.addAll(c);
    }

    public boolean remove(DTO o) {
        return list.remove(o);
    }

    public DTO remove(int index) {
        return list.remove(index);
    }

    public boolean removeAll(Collection<DTO> c) {
        return list.removeAll(c);
    }

    public void clear() {
        list = new ArrayList<>();
    }

    public boolean contains(DTO o) {
        return list.contains(o);
    }

    public boolean containsAll(Collection<DTO> c) {
        return list.containsAll(c);
    }

    public DTO get(int index) {
        return list.get(index);
    }

    public List<DTO> getList() {
        return list;
    }

    public DTO set(int index, DTO dto) {
        return list.set(index, dto);
    }

    public List<DTO> setList(List<DTO> list) {
        return list = list;
    }
}
