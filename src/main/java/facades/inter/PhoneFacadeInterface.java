package facades.inter;

import dtos.PersonDTO;
import dtos.PhoneDTO;

import java.util.List;

public interface PhoneFacadeInterface {

    PhoneDTO create(PhoneDTO phone);

    PhoneDTO update(PhoneDTO phoneDTO);

    PhoneDTO delete(long id);

    PhoneDTO getById(long id);

    public PhoneDTO getByNumber(Integer number);

    List<PhoneDTO> getByPerson(PersonDTO person);

    List<PhoneDTO> getAll();

    long getPhoneCount();
}
