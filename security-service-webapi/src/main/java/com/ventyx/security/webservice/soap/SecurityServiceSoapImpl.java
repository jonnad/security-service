package com.ventyx.security.webservice.soap;

import com.ventyx.security.api.TokenGenerationRequest;
import com.ventyx.security.api.TokenGenerationResponse;
import com.ventyx.security.service.ConfigurationService;
import com.ventyx.security.service.TokenService;
import com.ventyx.security.webservice.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;

@WebService(endpointInterface = "com.ventyx.security.webservice.SecurityService")
@Component
public class SecurityServiceSoapImpl implements SecurityService {

    @Autowired
    TokenService tokenService;

    @Autowired
    ConfigurationService configurationService;

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
