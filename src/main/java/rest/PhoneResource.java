package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import facades.PersonFacade;
import facades.PhoneFacade;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("phone")
public class PhoneResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private final PersonFacade PERSON_FACADE = PersonFacade.getPersonFacade(EMF);
    private final PhoneFacade PHONE_FACADE = PhoneFacade.getPhoneFacade(EMF);
    //private final ZipFacade ZIP_FACADE = ZipFacade.getZipFacade(EMF);
    //private final AddressFacade ADDRESS_FACADE = AddressFacade.getAddressFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String updatePhone(String phone) {
        PhoneDTO pDTO = GSON.fromJson(phone, PhoneDTO.class);
        PhoneDTO pNew = PHONE_FACADE.update(pDTO);
        return GSON.toJson(pNew);
    }

    @Path("{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public String deletePhone(@PathParam("id") long id) {
        PhoneDTO deleted = PHONE_FACADE.delete(id);
        return GSON.toJson(deleted);
    }

    @Path("list")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAll() {
        List<PhoneDTO> phones = PHONE_FACADE.getAll();
        return GSON.toJson(phones);
    }

    @Path("id/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getById(@PathParam("id") long id) {
        PhoneDTO phone = PHONE_FACADE.getById(id);
        return GSON.toJson(phone);
    }

    @Path("{number}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getByPhone(@PathParam("number") int number) {
        PhoneDTO phone = PHONE_FACADE.getByNumber(number);
        return GSON.toJson(phone);
    }

    @Path("person/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getByPhone(@PathParam("id") long id) {
        PersonDTO person = PERSON_FACADE.getById(id);
        List<PhoneDTO> phones = PHONE_FACADE.getByPerson(person);
        return GSON.toJson(phones);
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPhoneCount() {
        long count = PHONE_FACADE.getPhoneCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }
}
