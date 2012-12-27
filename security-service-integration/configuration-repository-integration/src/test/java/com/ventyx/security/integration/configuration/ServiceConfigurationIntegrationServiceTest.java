package com.ventyx.security.integration.configuration;

import com.ventyx.security.api.model.ServiceConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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

        logger.info("Here is the iD" + serviceConfiguration.getId());

        //serviceConfigurationDao.delete(serviceConfiguration);



    }
}
