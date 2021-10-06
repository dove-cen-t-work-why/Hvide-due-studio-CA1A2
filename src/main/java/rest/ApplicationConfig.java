package rest;

import javax.ws.rs.core.Application;
import java.util.Set;

@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(errorhandling.GenericExceptionMapper.class);
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
        resources.add(rest.PersonResource.class);
        resources.add(rest.HobbyResource.class);
        resources.add(rest.PhoneResource.class);
        resources.add(rest.ZipResource.class);
        resources.add(rest.AddressResource.class);
        resources.add(rest.cors.CorsFilter.class);
    }

}
