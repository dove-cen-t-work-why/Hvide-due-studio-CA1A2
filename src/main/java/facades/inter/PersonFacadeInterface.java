package facades.inter;

import dtos.*;
import entities.Person;

import java.util.List;

public interface PersonFacadeInterface {

    PersonDTO create(PersonDTO person) throws Exception;

    PersonDTO update(PersonDTO person);

    PersonDTO delete(long id);

    PersonDTO getById(long id);

    PersonDTO getByPhone(PhoneDTO phone);

    List<PersonDTO> getByHobby(HobbyDTO hobby);

    List<PersonDTO> getByAddress(AddressDTO address);

    List<PersonDTO> getByZip(ZipDTO zip);

    List<PersonDTO> getAll();

    long getPersonCount();
}
