package com.ventyx.security.integration.ldap;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/ldap-repository-integration-test-context.xml")
public class LdapIntegrationServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    LdapIntegrationService ldapIntegrationService;

    @Test
    public void testValidateUser() throws Exception {

        ldapIntegrationService.validateUser(null);

    }

    public void testGetUser() throws Exception {

    }

    public void setupEmbedded() {

    }

}
