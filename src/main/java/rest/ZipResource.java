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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("zip")
public class ZipResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private final PersonFacade PERSON_FACADE = PersonFacade.getPersonFacade(EMF);
    private final AddressFacade ADDRESS_FACADE = AddressFacade.getAddressFacade(EMF);
    private static final ZipFacade ZIP_FACADE = ZipFacade.getZipFacade(EMF);
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
        List<ZipDTO> zips = ZIP_FACADE.getAll();
        return GSON.toJson(zips);
    }

    @Path("address/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getByAddress(@PathParam("id") long id) {
        AddressDTO addressDTO = ADDRESS_FACADE.getById(id);
        ZipDTO zipDTO = ZIP_FACADE.getByAddress(addressDTO);
        return GSON.toJson(zipDTO);
    }

    @Path("person/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getByPerson(@PathParam("id") long id) {
        PersonDTO personDTO = PERSON_FACADE.getById(id);
        ZipDTO zipDTO = ZIP_FACADE.getByPerson(personDTO);
        return GSON.toJson(zipDTO);
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getZipCount() {

        long count = ZIP_FACADE.getZipCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }
}
