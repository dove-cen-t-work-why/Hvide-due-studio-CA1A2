package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import dtos.ZipDTO;
import facades.*;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("person")
public class PersonResource {

    private final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private final PersonFacade PERSON_FACADE = PersonFacade.getPersonFacade(EMF);
    private final PhoneFacade PHONE_FACADE = PhoneFacade.getPhoneFacade(EMF);
    private final ZipFacade ZIP_FACADE = ZipFacade.getZipFacade(EMF);
    private final AddressFacade ADDRESS_FACADE = AddressFacade.getAddressFacade(EMF);
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(String person) {
        PersonDTO personToCreate = GSON.fromJson(person, PersonDTO.class);
        PersonDTO personCreated = PERSON_FACADE.create(personToCreate);
        return GSON.toJson(personCreated);
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String update(String person) {
        PersonDTO newPersonDTO = GSON.fromJson(person, PersonDTO.class);
        PersonDTO updatedPersonDTO = PERSON_FACADE.update(newPersonDTO);
        return GSON.toJson(updatedPersonDTO);
    }

    @Path("{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public String delete(@PathParam("id") long id) {
        PersonDTO deletedPerson = PERSON_FACADE.delete(id);
        return GSON.toJson(deletedPerson);
    }

    @Path("list")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAll() {
        List<PersonDTO> persons = PERSON_FACADE.getAll();
        return GSON.toJson(persons);
    }

    @Path("id/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getById(@PathParam("id") long id) {
        PersonDTO person = PERSON_FACADE.getById(id);
        return GSON.toJson(person);
    }

    @Path("phone/{number}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getByPhone(@PathParam("number") int number) {
        PhoneDTO phone = PHONE_FACADE.getByNumber(number);
        PersonDTO person = PERSON_FACADE.getByPhone(phone);
        return GSON.toJson(person);
    }

    @Path("address/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getByZip(@PathParam("id") long id) {
        AddressDTO address = ADDRESS_FACADE.getById(id);
        List<PersonDTO> persons = PERSON_FACADE.getByAddress(address);
        return GSON.toJson(persons);
    }

    @Path("zip/{zip}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getByZip(@PathParam("zip") int zip) {
        ZipDTO zipDTO = ZIP_FACADE.getByZip(zip);
        List<PersonDTO> persons = PERSON_FACADE.getByZip(zipDTO);
        return GSON.toJson(persons);
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonCount() {
        long count = PERSON_FACADE.getPersonCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }

    @Path("populate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPopulate() {
        String pop = PopulatorPerson.populate();
        return "{\"Message:\":" + pop + "}";
    }
}
