package com.ventyx.security.service;

import com.ventyx.security.api.TokenGenerationRequest;
import com.ventyx.security.api.TokenGenerationResponse;
import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.api.model.ServiceDefinition;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TokenServiceTest extends TestCase {


    public void testGenerateToken() throws Exception {
        ServiceDefinition serviceDefinition = mock(ServiceDefinition.class);
        serviceDefinition.setId(1);
        serviceDefinition.setEnabled(true);
        serviceDefinition.setRequiresUserAuthentication(true);
        serviceDefinition.setSecured(true);
        List<ServiceDefinition> serviceDefinitions = new ArrayList<ServiceDefinition>();
        serviceDefinitions.add(serviceDefinition);

        Authentication authentication = mock(Authentication.class);
        authentication.setApplicationUser("client");
        authentication.setEnabled(true);
        authentication.setId(1);
        authentication.setServiceDefinitions(serviceDefinitions);

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
