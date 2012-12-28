package com.ventyx.security.service.proxy;

import com.meterware.httpunit.*;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import java.io.InputStream;

public class ProxyServletIntegrationTest  {

    private static final Logger log = LoggerFactory.getLogger(ProxyServletIntegrationTest.class);

    private static Server server = null;

    @BeforeClass
    public static void start() throws Exception {

        try {
            server = new Server();

            Connector connector=new SelectChannelConnector();
            connector.setPort(8980);
            server.setConnectors(new Connector[]{connector});

            InputStream datasourceConfiguration = ProxyServletIntegrationTest.class.getClassLoader().getResourceAsStream("jetty-test-datasource.xml");
            XmlConfiguration config = new XmlConfiguration(datasourceConfiguration);

            WebAppContext webapp = new WebAppContext();
            config.configure(webapp);
            webapp.setClassLoader(ProxyServletIntegrationTest.class.getClassLoader());
            webapp.setContextPath("/");

            String targetPath = ProxyServletIntegrationTest.class.getClassLoader().getResource("").getPath();
            targetPath = targetPath.substring(0, targetPath.lastIndexOf("/") - 12);
            webapp.setWar(targetPath + "webproxy.war");
            server.setHandler(webapp);
            server.start();

        } catch (Exception ex) {
            log.error("Error initializing embedded server", ex);
            fail();
        }
    }

    @Test
    public void testServerStart() {
        assertNotNull(server);
    }

    @Test
    public void testTestPage() throws Exception {

        HttpUnitOptions.setScriptingEnabled(false);
        WebConversation webConversation = new WebConversation();

        InputStream body = ProxyServletIntegrationTest.class.getClassLoader().getResourceAsStream("tokenRequest.xml");
        WebRequest webRequest = new PostMethodWebRequest( "http://localhost:8980/webproxy/test.html", body, "text/xml" );

        WebResponse webResponse = webConversation.getResponse(webRequest);

        assertEquals(webResponse.getTitle(), "Test");

    }

    @Test
    public void testUnsecuredConnection() throws Exception {

        HttpUnitOptions.setScriptingEnabled(false);
        WebConversation webConversation = new WebConversation();

        InputStream body = ProxyServletIntegrationTest.class.getClassLoader().getResourceAsStream("tokenRequest.xml");
        WebRequest webRequest = new PostMethodWebRequest( "http://localhost:8980/webproxy/some/business/url", body, "text/xml" );

        try {
            WebResponse webResponse = webConversation.getResponse(webRequest);
            fail("Invalid test case pass");
        } catch (HttpException ex) {
           assertTrue(ex.getMessage().toLowerCase().contains("403 forbidden"));
        }

    }

    @AfterClass
    public static void stop() throws Exception {

        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
