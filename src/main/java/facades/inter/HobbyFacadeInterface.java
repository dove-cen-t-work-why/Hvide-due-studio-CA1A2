package facades.inter;

import dtos.*;

import java.util.List;

public interface HobbyFacadeInterface {

    HobbyDTO create(HobbyDTO Hobby);

    HobbyDTO update(HobbyDTO Hobby);

    HobbyDTO delete(long id) throws Exception;

    HobbyDTO getById(long id);

    List<HobbyDTO> getByCategory(String category);

    List<HobbyDTO> getByType(String type);

    List<HobbyDTO> getByPerson(PersonDTO personDTO);

    List<HobbyDTO> getByPhone(PhoneDTO phoneDTO);

    List<HobbyDTO> getByAddress(AddressDTO addressDTO);

    List<HobbyDTO> getByZip(ZipDTO zipDTO);

    List<HobbyDTO> getAll();

    long getHobbyCount();
}
