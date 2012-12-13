package com.ventyx.security.integration.configuration;

import com.ventyx.security.api.model.ServiceDefinition;
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
public class ConfigurationServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    ServiceConfigurationService serviceConfigurationService;

    @Test
    public void testGetServiceConfiguration() throws Exception {

        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setName("My test service configuration");
        serviceDefinition.setDescription("Very long description");
        serviceDefinition.setRequiresUserAuthentication(true);
        serviceDefinition.setSecured(true);
        serviceDefinition.setEnabled(true);
        serviceConfigurationService.createServiceConfiguration(serviceDefinition);

        logger.info("Here is the iD" + serviceDefinition.getId());

        //serviceConfigurationDao.delete(serviceConfiguration);



    }
}
