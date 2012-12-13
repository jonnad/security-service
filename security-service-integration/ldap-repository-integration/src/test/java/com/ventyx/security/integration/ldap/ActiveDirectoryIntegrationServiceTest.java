package com.ventyx.security.integration.ldap;

import com.ventyx.security.api.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/ldap-repository-integration-test-context.xml")
public class ActiveDirectoryIntegrationServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    ActiveDirectoryIntegrationService activeDirectoryIntegrationService;

    @Test
    public void testValidateUser() throws Exception {

        User user = new User();
        user.setUserId("Jonathan Hamel");
        user.setCredential("Pazzw0rd");
        activeDirectoryIntegrationService.validateUser(user);

    }

    public void testGetUser() throws Exception {

    }

}
