package com.ventyx.security.service;

import com.ventyx.security.api.TokenGenerationRequest;
import com.ventyx.security.api.TokenGenerationResponse;
import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.api.model.ServiceConfiguration;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class TokenServiceTest extends TestCase {


    public void testGenerateToken() throws Exception {
        ServiceConfiguration serviceConfiguration = mock(ServiceConfiguration.class);
        serviceConfiguration.setId(1);
        serviceConfiguration.setEnabled(true);
        serviceConfiguration.setRequiresUserAuthentication(true);
        serviceConfiguration.setSecured(true);
        List<ServiceConfiguration> serviceConfigurations = new ArrayList<ServiceConfiguration>();
        serviceConfigurations.add(serviceConfiguration);

        Authentication authentication = mock(Authentication.class);
        authentication.setApplicationUser("client");
        authentication.setEnabled(true);
        authentication.setId(1);
        authentication.setServiceConfigurations(serviceConfigurations);

        TokenService tokenService = mock(TokenService.class);
        ConfigurationService configurationService = mock(ConfigurationService.class);

        when(configurationService.getAuthentication(anyString())).thenReturn(authentication);

        TokenGenerationRequest tokenGenerationRequest = mock(TokenGenerationRequest.class);
        tokenGenerationRequest.setApplicationId("1");
        tokenGenerationRequest.setApplicationUser("client");
        tokenGenerationRequest.setApplicationPassword("password");
        TokenGenerationResponse response = tokenService.generateToken(tokenGenerationRequest);


    }

    public void testInvalidateToken() throws Exception {

    }

    public void testValidateToken() throws Exception {

    }
}
