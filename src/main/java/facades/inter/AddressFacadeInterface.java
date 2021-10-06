package facades.inter;

import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.ZipDTO;

import java.util.List;

public interface AddressFacadeInterface {

    AddressDTO create(AddressDTO address) throws Exception;

    AddressDTO update(AddressDTO addressDTO);

    AddressDTO delete(long id) throws Exception;

    AddressDTO getById(long id);

    List<AddressDTO> getAll();

    List<AddressDTO> getByZip(ZipDTO zipDTO);

    AddressDTO getByPerson(PersonDTO personDTO);

    long getAddressCount();
}
