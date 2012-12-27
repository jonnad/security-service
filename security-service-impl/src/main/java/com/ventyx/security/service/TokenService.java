package com.ventyx.security.service;

import com.ventyx.security.api.TokenGenerationRequest;
import com.ventyx.security.api.TokenGenerationResponse;
import com.ventyx.security.api.UserService;
import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.api.model.ServiceConfiguration;
import com.ventyx.security.api.model.Token;
import com.ventyx.security.api.model.TokenFormat;
import com.ventyx.security.integration.configuration.TokenIntegrationService;
import com.ventyx.security.utils.TokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    TokenIntegrationService tokenIntegrationService;

    @Autowired
    @Qualifier("ldapIntegrationService")
    UserService userService;

    private final static Logger log = LoggerFactory.getLogger(TokenService.class);

    public TokenGenerationResponse generateToken(TokenGenerationRequest tokenGenerationRequest) {

        //validation
        if (tokenGenerationRequest == null) {
            return new TokenGenerationResponse("1000", "TokenGenerationRequest cannot be empty");
        }

        if (tokenGenerationRequest.getApplicationId() == null || tokenGenerationRequest.getApplicationId().isEmpty()) {
            return new TokenGenerationResponse("1001", "ApplicationId cannot be empty");
        }

        if (tokenGenerationRequest.getApplicationUser() == null || tokenGenerationRequest.getApplicationUser().isEmpty()) {
            return new TokenGenerationResponse("1001", "ApplicationUser cannot be empty");
        }

        if (tokenGenerationRequest.getApplicationPassword() == null || tokenGenerationRequest.getApplicationPassword().isEmpty()) {
            return new TokenGenerationResponse("1002", "ApplicationPassword cannot be empty");
        }

        //find the user in the configuration and see if they even exist before we proceed to permissions
        Authentication authentication = configurationService.getAuthentication(tokenGenerationRequest.getApplicationUser());

        if (authentication == null) {
            log.warn("User record not found for " + tokenGenerationRequest.getApplicationUser());
            return null;
        }

        if (authentication.getServiceConfigurations() == null || authentication.getServiceConfigurations().isEmpty()) {
            log.warn("User record " + tokenGenerationRequest.getApplicationUser() + " does not have any permissions.");
            return null;
        }

        for (ServiceConfiguration serviceConfiguration : authentication.getServiceConfigurations()) {
            if (serviceConfiguration.getId().equals(Integer.parseInt(tokenGenerationRequest.getApplicationId()))) {

                if (!serviceConfiguration.isEnabled()) {
                    log.warn("User " + tokenGenerationRequest.getApplicationUser() + " was found but the service is disabled.");
                    return null;
                }

                if (serviceConfiguration.isRequiresUserAuthentication()) {
                    //this is a case where the user authentication will be pushed to the proper component if present
                    if (tokenGenerationRequest.getUserPrincipal() == null || tokenGenerationRequest.getUserPassword() == null) {
                        log.warn("User authentication is required to complete generating this type of token.");
                        return null;
                    }

                    //TODO delegate to the proper component for user auth

                }

                //create a new token and return
                boolean encode = true;
                if (TokenFormat.SAML_PLAIN.equals(tokenGenerationRequest.getTokenFormat())) {
                    encode = false;
                }
                Token token = TokenGenerator.generateAssertion(serviceConfiguration, tokenGenerationRequest.getApplicationUser(), true, encode);
                tokenIntegrationService.createOrUpdateToken(token);

                TokenGenerationResponse response = new TokenGenerationResponse();
                response.setToken(token);
                return response;
            }
        }
        return null;
    }

    public boolean invalidateToken(String tokenId) {
        return true;
    }

    public boolean validateToken(String token)  {

        if (token == null || token.isEmpty()) {
            log.warn("Cannot validate token as no valid token was supplied");
            return false;
        }

        //base64 decode
        //String decodedToken = null;
        //try {
        //    decodedToken = new String(Base64.decode(token));
        //} catch (Exception ex) {
        //    log.error("Cannot decode token : " + token, ex);
        //    return false;
        //}

        return TokenGenerator.decryptAssertion(token);

    }

}
