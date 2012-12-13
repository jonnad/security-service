package com.ventyx.security.sampleservices.rest;

import com.ventyx.security.api.TokenGenerationRequest;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;

/**
 *
 * RESTful endpoint for processing application and user-level token requests
 *
 */

@Component
@Path("/service/rest/business")
public class BusinessRestService {

    @POST
    @Path("/securedPost")
    @Produces("application/json")
    @Consumes("application/json")
    public String generateUserTokenPOST() {
        System.out.println("You have infiltrated the system");
        return "You have infiltrated the system";
    }

    @GET
    @Path("/securedGet")
    @Produces("application/json")
    @Consumes("application/json")
    public String generateUserTokenGET() {
        System.out.println("You have infiltrated the system");
        return "You have infiltrated the system";
    }

    @PUT
    @Path("/securedPut")
    @Produces("application/json")
    @Consumes("application/json")
    public String generateUserTokenPUT() {
        System.out.println("You have infiltrated the system");
        return "You have infiltrated the system";
    }

    @DELETE
    @Path("/securedDelete")
    @Produces("application/json")
    @Consumes("application/json")
    public String generateUserTokenDELETE() {
        System.out.println("You have infiltrated the system");
        return "You have infiltrated the system";
    }

}
