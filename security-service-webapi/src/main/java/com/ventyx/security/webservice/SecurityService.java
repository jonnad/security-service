package com.ventyx.security.webservice;

import com.ventyx.security.api.TokenGenerationRequest;
import com.ventyx.security.api.TokenGenerationResponse;
import org.springframework.stereotype.Component;

import javax.jws.WebService;

@WebService
@Component
public interface SecurityService {

    public TokenGenerationResponse generateToken(TokenGenerationRequest tokenGenerationRequest);

    public boolean invalidateToken(String tokenId);

    public boolean validateToken(String token);

}
