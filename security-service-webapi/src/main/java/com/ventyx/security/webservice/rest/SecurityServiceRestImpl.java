package com.ventyx.security.webservice.rest;

import com.ventyx.security.api.TokenGenerationRequest;
import com.ventyx.security.api.TokenGenerationResponse;
import com.ventyx.security.service.ConfigurationService;
import com.ventyx.security.service.TokenService;
import com.ventyx.security.webservice.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * RESTful endpoint for processing application and user-level token requests
 *
 */

@Component
@Path("/service/token")
public class SecurityServiceRestImpl implements SecurityService {

    @Autowired
    TokenService tokenService;

    @Autowired
    ConfigurationService configurationService;

    @POST
    @Path("/generate")
    @Produces("application/json")
    @Consumes("application/json")
    public TokenGenerationResponse generateToken(TokenGenerationRequest tokenGenerationRequest) {
        return tokenService.generateToken(tokenGenerationRequest);
    }

    public boolean invalidateToken(String tokenId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean validateToken(String token) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
