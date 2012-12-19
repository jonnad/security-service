package com.ventyx.security.integration.ldap;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.DN;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import com.ventyx.security.api.model.ServiceDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
