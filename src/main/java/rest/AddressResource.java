package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.ZipDTO;
import facades.AddressFacade;
import facades.PersonFacade;
import facades.ZipFacade;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("address")
public class AddressResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final PersonFacade PERSON_FACADE = PersonFacade.getPersonFacade(EMF);
    private static final ZipFacade ZIP_FACADE = ZipFacade.getZipFacade(EMF);
    private static final AddressFacade ADDRESS_FACADE = AddressFacade.getAddressFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @Path("list")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAll() {
        List<AddressDTO> addresses = ADDRESS_FACADE.getAll();
        return GSON.toJson(addresses);
    }

    @Path("id/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getById(@PathParam("id") long id) {
        AddressDTO addressDTO = ADDRESS_FACADE.getById(id);
        return GSON.toJson(addressDTO);
    }

    @Path("zip/{zip}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getById(@PathParam("zip") int zipcode) {
        ZipDTO zip = ZIP_FACADE.getByZip(zipcode);
        List<AddressDTO> addressDTOs = ADDRESS_FACADE.getByZip(zip);
        return GSON.toJson(addressDTOs);
    }

    @Path("person/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getByPerson(@PathParam("id") long id) {
        PersonDTO person = PERSON_FACADE.getById(id);
        AddressDTO addressDTO = ADDRESS_FACADE.getByPerson(person);
        return GSON.toJson(addressDTO);
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAddressCount() {

        long count = ADDRESS_FACADE.getAddressCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String updateAddress(String address) {
        AddressDTO aDTO = GSON.fromJson(address, AddressDTO.class);
        AddressDTO aNew = ADDRESS_FACADE.update(aDTO);
        return GSON.toJson(aNew);
    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String deletePerson(@PathParam("id") long id) throws Exception
    {
        AddressDTO aDeleted = ADDRESS_FACADE.getById(id);
        ADDRESS_FACADE.delete(id);
        return GSON.toJson(aDeleted);
    }
}
