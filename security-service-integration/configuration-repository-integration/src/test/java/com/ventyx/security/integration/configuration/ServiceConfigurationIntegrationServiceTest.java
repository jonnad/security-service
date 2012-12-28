package com.ventyx.security.integration.configuration;

import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.api.model.ServiceConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/configuration-repository-integration-test-context.xml")
@Transactional
public class ServiceConfigurationIntegrationServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    ServiceConfigurationIntegrationService serviceConfigurationIntegrationService;

    @Test
    public void testGetServiceConfiguration() throws Exception {

        ServiceConfiguration serviceConfiguration = new ServiceConfiguration();
        serviceConfiguration.setName("My test service configuration");
        serviceConfiguration.setDescription("Very long description");
        serviceConfiguration.setRequiresUserAuthentication(true);
        serviceConfiguration.setSecured(true);
        serviceConfiguration.setEnabled(true);
        serviceConfigurationIntegrationService.createOrUpdateServiceConfiguration(serviceConfiguration);

        assertTrue(serviceConfiguration.getId() != null);
    }

    @Test
    public void testGetServiceConfigurationCache() {

        ServiceConfiguration serviceConfiguration = new ServiceConfiguration();
        serviceConfiguration.setName("My test service configuration");
        serviceConfiguration.setDescription("Very long description");
        serviceConfiguration.setRequiresUserAuthentication(true);
        serviceConfiguration.setSecured(true);
        serviceConfiguration.setEnabled(true);
        serviceConfigurationIntegrationService.createOrUpdateServiceConfiguration(serviceConfiguration);

        int id = serviceConfiguration.getId();
        ServiceConfiguration cachedServiceConfiguration = serviceConfigurationIntegrationService.getServiceConfiguration(id);

        assertTrue(cachedServiceConfiguration.equals(serviceConfiguration));

    }

    @Test
    public void testGetAuthentication() throws Exception {


        ServiceConfiguration serviceConfiguration = new ServiceConfiguration();
        serviceConfiguration.setName("My test service configuration");
        serviceConfiguration.setDescription("Very long description");
        serviceConfiguration.setRequiresUserAuthentication(true);
        serviceConfiguration.setSecured(true);
        serviceConfiguration.setEnabled(true);

        Authentication authentication = new Authentication();
        authentication.setApplicationUser("user");
        authentication.setApplicationPassword("password");
        authentication.setEnabled(true);

        List<Authentication> authentications = new ArrayList<Authentication>();
        authentications.add(authentication);
        serviceConfiguration.setAuthentications(authentications);

        serviceConfigurationIntegrationService.createOrUpdateServiceConfiguration(serviceConfiguration);

        assertTrue(serviceConfiguration.getId() != null);
        assertTrue(authentication.getId() != null);

    }

    @Test
    public void testGetAuthenticationCache() throws Exception {


        ServiceConfiguration serviceConfiguration = new ServiceConfiguration();
        serviceConfiguration.setName("My test service configuration");
        serviceConfiguration.setDescription("Very long description");
        serviceConfiguration.setRequiresUserAuthentication(true);
        serviceConfiguration.setSecured(true);
        serviceConfiguration.setEnabled(true);

        Authentication authentication = new Authentication();
        authentication.setApplicationUser("user");
        authentication.setApplicationPassword("password");
        authentication.setEnabled(true);

        List<Authentication> authentications = new ArrayList<Authentication>();
        authentications.add(authentication);
        serviceConfiguration.setAuthentications(authentications);

        serviceConfigurationIntegrationService.createOrUpdateServiceConfiguration(serviceConfiguration);

        assertTrue(serviceConfiguration.getId() != null);
        assertTrue(authentication.getId() != null);

        String userName = authentication.getApplicationUser();
        Authentication cachedAuthentication = serviceConfigurationIntegrationService.getAuthentication(userName);

        assertTrue(cachedAuthentication.equals(authentication));

    }

}
