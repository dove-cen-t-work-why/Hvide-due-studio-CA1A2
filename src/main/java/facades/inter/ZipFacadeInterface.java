package facades.inter;

import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.ZipDTO;

import java.util.List;

public interface ZipFacadeInterface {

    ZipDTO create(ZipDTO Zip);

    ZipDTO update(ZipDTO Zip);

    ZipDTO delete(long zip);

    ZipDTO getByZip(long zip);

    ZipDTO getByPerson(PersonDTO personDTO);

    ZipDTO getByAddress(AddressDTO addressDTO);

    List<ZipDTO> getAll();

    long getZipCount();
}
