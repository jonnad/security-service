package com.ventyx.security.service.proxy;

import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.service.ConfigurationService;
import com.ventyx.security.service.TokenService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *  Proxy testing
 */
public class ProxyServletTest  {

    @Test
    public void shouldDecodeSearchParameters() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("username")).thenReturn("me");
        when(request.getParameter("password")).thenReturn("secret");
        when(request.getRequestURI()).thenReturn("secret");
        PrintWriter writer = new PrintWriter("somefile.txt");
        when(response.getWriter()).thenReturn(writer);

        Authentication authentication = mock(Authentication.class);
        authentication.setApplicationUser("client");
        authentication.setEnabled(true);
        authentication.setId(1);
        //authentication.setServiceDefinitions(serviceDefinitions);

        TokenService tokenService = mock(TokenService.class);
        ConfigurationService configurationService = mock(ConfigurationService.class);

        when(configurationService.getAuthentication(anyString())).thenReturn(authentication);

        new ProxyServlet().service(request, response);

        verify(request, atLeast(1)).getParameter("username"); // only if you want to verify username was called...
        writer.flush(); // it may not have been flushed yet...
        assertTrue(FileUtils .readFileToString(new File("somefile.txt"), "UTF-8").contains("My Expected String"));
    }

}
