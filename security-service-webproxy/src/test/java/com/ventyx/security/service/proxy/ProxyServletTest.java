package com.ventyx.security.service.proxy;

import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.api.model.Endpoint;
import com.ventyx.security.api.model.ServiceConfiguration;
import com.ventyx.security.service.ConfigurationService;
import com.ventyx.security.service.TokenService;
import org.junit.Test;

import static org.mockito.Mockito.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *  Proxy testing
 */
public class ProxyServletTest  {

    @Test
    public void testTestPageAccess() throws IOException, ServletException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("test.html");
        PrintWriter writer = new PrintWriter("somefile.txt");
        when(response.getWriter()).thenReturn(writer);

        ProxyServlet proxyServlet = new ProxyServlet();
        proxyServlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }


    @Test
    public void testUnsecuredAccess() throws IOException, ServletException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/business/service");
        PrintWriter writer = new PrintWriter("somefile.txt");
        when(response.getWriter()).thenReturn(writer);

        Authentication authentication = mock(Authentication.class);
        authentication.setApplicationUser("client");
        authentication.setApplicationPassword("password");
        authentication.setEnabled(true);
        authentication.setId(1);

        ServiceConfiguration serviceConfiguration = new ServiceConfiguration();
        serviceConfiguration.setId(1);
        serviceConfiguration.setEnabled(true);
        serviceConfiguration.setRequiresUserAuthentication(true);
        serviceConfiguration.setSecured(true);
        List<ServiceConfiguration> serviceConfigurations = new ArrayList<ServiceConfiguration>();
        serviceConfigurations.add(serviceConfiguration);
        authentication.setServiceConfigurations(serviceConfigurations);

        Endpoint endpoint = new Endpoint();
        endpoint.setId(1);
        endpoint.setEnabled(true);
        endpoint.setMatchPartialUri(true);
        endpoint.setServiceHost("localhost");
        endpoint.setServicePort(8888);
        endpoint.setServiceUri("/business/service");        //matching value
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        endpoints.add(endpoint);
        serviceConfiguration.setEndpoints(endpoints);

        TokenService tokenService = mock(TokenService.class);
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getAuthentication(anyString())).thenReturn(authentication);
        when(configurationService.getServiceConfigurations()).thenReturn(serviceConfigurations);

        ProxyServlet proxyServlet = new ProxyServlet();
        proxyServlet.setConfigurationService(configurationService);
        proxyServlet.setTokenService(tokenService);
        proxyServlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }



    @Test
    public void testUnsecuredAccessWithBadToken() throws IOException, ServletException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/business/service");
        PrintWriter writer = new PrintWriter("somefile.txt");
        when(response.getWriter()).thenReturn(writer);
        when(request.getHeader("s_token")).thenReturn("THISISNOTAVALIDTOKEN");

        Authentication authentication = mock(Authentication.class);
        authentication.setApplicationUser("client");
        authentication.setApplicationPassword("password");
        authentication.setEnabled(true);
        authentication.setId(1);

        ServiceConfiguration serviceConfiguration = new ServiceConfiguration();
        serviceConfiguration.setId(1);
        serviceConfiguration.setEnabled(true);
        serviceConfiguration.setRequiresUserAuthentication(true);
        serviceConfiguration.setSecured(true);
        List<ServiceConfiguration> serviceConfigurations = new ArrayList<ServiceConfiguration>();
        serviceConfigurations.add(serviceConfiguration);
        authentication.setServiceConfigurations(serviceConfigurations);

        Endpoint endpoint = new Endpoint();
        endpoint.setId(1);
        endpoint.setEnabled(true);
        endpoint.setMatchPartialUri(true);
        endpoint.setServiceHost("localhost");
        endpoint.setServicePort(8888);
        endpoint.setServiceUri("/business/service");        //matching value
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        endpoints.add(endpoint);
        serviceConfiguration.setEndpoints(endpoints);

        TokenService tokenService = mock(TokenService.class);
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getAuthentication(anyString())).thenReturn(authentication);
        when(configurationService.getServiceConfigurations()).thenReturn(serviceConfigurations);

        ProxyServlet proxyServlet = new ProxyServlet();
        proxyServlet.setConfigurationService(configurationService);
        proxyServlet.setTokenService(tokenService);
        proxyServlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testSecuredAccessWithGoodToken() throws IOException, ServletException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/business/service");
        PrintWriter writer = new PrintWriter("somefile.txt");
        when(response.getWriter()).thenReturn(writer);
        when(request.getHeader("s_token")).thenReturn("SOMETOKENVALUE");
        when(request.getMethod()).thenReturn("POST");

        Authentication authentication = mock(Authentication.class);
        authentication.setApplicationUser("client");
        authentication.setApplicationPassword("password");
        authentication.setEnabled(true);
        authentication.setId(1);

        ServiceConfiguration serviceConfiguration = new ServiceConfiguration();
        serviceConfiguration.setId(1);
        serviceConfiguration.setEnabled(true);
        serviceConfiguration.setRequiresUserAuthentication(true);
        serviceConfiguration.setSecured(true);
        List<ServiceConfiguration> serviceConfigurations = new ArrayList<ServiceConfiguration>();
        serviceConfigurations.add(serviceConfiguration);
        authentication.setServiceConfigurations(serviceConfigurations);

        Endpoint endpoint = new Endpoint();
        endpoint.setId(1);
        endpoint.setEnabled(true);
        endpoint.setMatchPartialUri(true);
        endpoint.setServiceHost("localhost");
        endpoint.setServicePort(8888);
        endpoint.setServiceUri("/business/service");        //matching value
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        endpoints.add(endpoint);
        serviceConfiguration.setEndpoints(endpoints);

        TokenService tokenService = mock(TokenService.class);
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getAuthentication(anyString())).thenReturn(authentication);
        when(configurationService.getServiceConfigurations()).thenReturn(serviceConfigurations);
        when(tokenService.validateToken(anyString())).thenReturn(true);

        ProxyServlet proxyServlet = new ProxyServlet();
        proxyServlet.setConfigurationService(configurationService);
        proxyServlet.setTokenService(tokenService);
        //proxyServlet.service(request, response);

        //verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
