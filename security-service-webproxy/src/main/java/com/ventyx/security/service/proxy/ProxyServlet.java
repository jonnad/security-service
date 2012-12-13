package com.ventyx.security.service.proxy;

import com.ventyx.security.api.model.ServiceDefinition;
import com.ventyx.security.api.model.Endpoint;
import com.ventyx.security.service.ConfigurationService;
import com.ventyx.security.service.TokenService;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.HeaderGroup;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

/**
 * An HTTP reverse proxy servlet. It is designed to be extended for customization
 * if desired. Most of the work is handled by
 * <a href="http://hc.apache.org/httpcomponents-client-ga/">Apache HttpClient</a>.
 * <p>
 * There are alternatives to a servlet based proxy such as Apache mod_proxy if that is available to you. However
 * this servlet is easily customizable by Java, secure-able by your web application's security (e.g. spring-security),
 * portable across servlet engines, and is embeddable into another web application.
 * </p>
 * <p>
 * Inspiration: http://httpd.apache.org/docs/2.0/mod/mod_proxy.html
 * </p>
 */

@Component
public class ProxyServlet extends HttpServlet {

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    TokenService tokenService;

    private static final Logger log = LoggerFactory.getLogger(ProxyServlet.class);

    private static final String SECURITY_TOKEN_IDENTIFIER = "s_token";

    private HttpClient proxyClient;
    private static final Properties configuration = new Properties();
    private static final BitSet asciiQueryChars;

    /**
     * load the properties for the proxy configuration
     */
    static {

        InputStream inputStream = ProxyServlet.class.getClassLoader().getResourceAsStream("security-service-proxy.properties");
        if (inputStream == null) {
            log.error("Error loading security-service-proxy.properties, file not found.");
        } else {
            try {
                configuration.load(inputStream);
            } catch (IOException ex) {
                log.error("Error loading security-service-proxy.properties", ex);
            }
        }
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        ServletContext servletContext = servletConfig.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

        AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.configureBean(this, "configurationService");
        autowireCapableBeanFactory.configureBean(this, "tokenService");

        HttpParams httpParams = new BasicHttpParams();
        readConfigParam(httpParams, ClientPNames.HANDLE_REDIRECTS, Boolean.class);
        proxyClient = createHttpClient(httpParams);
    }

    @Override
    public void destroy() {
        //shutdown() must be called according to documentation.
        if (proxyClient != null)
            proxyClient.getConnectionManager().shutdown();
        super.destroy();
    }

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {

        URI targetUri = null;

        if (servletRequest.getRequestURI().toLowerCase().contains("test.html")) {
            servletResponse.getWriter().println("<!DOCTYPE html><html><head><title>Test</title></head><body><h1>Proxy is Running</h1><p>Thank you for stopping by!</p></body></html>");
            return;
        }

        try {

            List<ServiceDefinition> serviceDefinitions = configurationService.getServiceConfigurations();

            //iterate through all of the proxy endpoints
            if (serviceDefinitions != null && !serviceDefinitions.isEmpty()) {

                //note that configurations can have multiple endpoints
                for (ServiceDefinition serviceDefinition : serviceDefinitions) {

                    if (serviceDefinition.isEnabled()) {

                        if (serviceDefinition.getEndpoints() != null) {
                            for (Endpoint endpoint : serviceDefinition.getEndpoints()) {
                                if (servletRequest.getRequestURI().toLowerCase().contains(endpoint.getServiceUri())) {
                                    //we are only proxying the server/host
                                    targetUri = new URI(endpoint.getServiceHost() + ":" + endpoint.getServicePort() + servletRequest.getRequestURI());

                                    // check for the security header/token somewhere in the message
                                    if (serviceDefinition.isSecured() &&
                                            (servletRequest.getHeader(SECURITY_TOKEN_IDENTIFIER) == null || servletRequest.getHeader(SECURITY_TOKEN_IDENTIFIER).isEmpty())) {
                                        log.error("Unsecured access to URI/URL " + servletRequest.getRequestURL());
                                        servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                        return;
                                    }

                                    //decode, verify and decrypt the token
                                    if(!tokenService.validateToken(servletRequest.getHeader(SECURITY_TOKEN_IDENTIFIER))) {
                                        log.error("Invalid token supplied accessing URI/URL " + servletRequest.getRequestURL());
                                        servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                        return;
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (targetUri == null || targetUri.toString().isEmpty()) {
                log.error("Error locating mapping for URI/URL " + servletRequest.getRequestURL());
                servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

        } catch (Exception ex) {
            log.error("Error parsing URI/URL " + servletRequest.getRequestURL(), ex);
            servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Make the Request
        //note: we won't transfer the protocol version because I'm not sure it would truly be compatible
        BasicHttpEntityEnclosingRequest proxyRequest =
                new BasicHttpEntityEnclosingRequest(servletRequest.getMethod(), rewriteUrlFromRequest(servletRequest, targetUri));

        /*
        servletRequest.getRequestURI() = {java.lang.String@2907}"/service-proxy/test.html"
        servletRequest.getRequestURL() = {java.lang.StringBuffer@2912}"http://localhost:8980/service-proxy/test.html"
        servletRequest.getContextPath() = {java.lang.String@2916}"/service-proxy"
        servletRequest.getServletPath() = {java.lang.String@2917}"/test.html"
         */

        copyRequestHeaders(servletRequest, proxyRequest, targetUri);

        // Add the input entity (streamed) then execute the request.
        HttpResponse proxyResponse = null;
        InputStream servletRequestInputStream = servletRequest.getInputStream();
        try {
            try {
                proxyRequest.setEntity(new InputStreamEntity(servletRequestInputStream, servletRequest.getContentLength()));

                // Execute the request
                if (log.isDebugEnabled()) {
                    log.debug("proxy " + servletRequest.getMethod() + " uri: " + servletRequest.getRequestURI() + " -- " + proxyRequest.getRequestLine().getUri());
                }
                proxyResponse = proxyClient.execute(URIUtils.extractHost(targetUri), proxyRequest);
            } finally {
                closeQuietly(servletRequestInputStream);
            }

            // Process the response
            int statusCode = proxyResponse.getStatusLine().getStatusCode();

            if (doResponseRedirectOrNotModifiedLogic(servletRequest, servletResponse, proxyResponse, statusCode, targetUri)) {
                EntityUtils.consume(proxyResponse.getEntity());
                return;
            }

            // Pass the response code. This method with the "reason phrase" is deprecated but it's the only way to pass the
            //  reason along too.
            //noinspection deprecation
            servletResponse.setStatus(statusCode, proxyResponse.getStatusLine().getReasonPhrase());

            copyResponseHeaders(proxyResponse, servletResponse);

            // Send the content to the client
            copyResponseEntity(proxyResponse, servletResponse);

        } catch (Exception e) {
            //abort request, according to best practice with HttpClient
            if (proxyRequest instanceof AbortableHttpRequest) {
                AbortableHttpRequest abortableHttpRequest = (AbortableHttpRequest) proxyRequest;
                abortableHttpRequest.abort();
            }
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            if (e instanceof ServletException)
                throw (ServletException) e;
            throw new RuntimeException(e);
        }
    }

    private boolean doResponseRedirectOrNotModifiedLogic(HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpResponse proxyResponse, int statusCode, URI targetUri) throws ServletException, IOException {
        // Check if the proxy response is a redirect
        // The following code is adapted from org.tigris.noodle.filters.CheckForRedirect
        if (statusCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
                && statusCode < HttpServletResponse.SC_NOT_MODIFIED /* 304 */) {
            Header locationHeader = proxyResponse.getLastHeader(HttpHeaders.LOCATION);
            if (locationHeader == null) {
                throw new ServletException("Received status code: " + statusCode
                        + " but no " + HttpHeaders.LOCATION + " header was found in the response");
            }
            // Modify the redirect to go to this proxy servlet rather that the proxied host
            String locStr = rewriteUrlFromResponse(servletRequest, locationHeader.getValue(), targetUri);

            servletResponse.sendRedirect(locStr);
            return true;
        }
        // 304 needs special handling.  See:
        // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
        // We get a 304 whenever passed an 'If-Modified-Since'
        // header and the data on disk has not changed; server
        // responds w/ a 304 saying I'm not going to send the
        // body because the file has not changed.
        if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
            servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
            servletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }
        return false;
    }

    protected void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            log.trace(e.getMessage(), e);
        }
    }

    /**
     * These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
     * I use an HttpClient HeaderGroup class instead of Set<String> because this
     * approach does case insensitive lookup faster.
     */
    private static final HeaderGroup hopByHopHeaders;

    static {
        hopByHopHeaders = new HeaderGroup();
        String[] headers = new String[]{
                "Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization",
                "TE", "Trailers", "Transfer-Encoding", "Upgrade"};
        for (String header : headers) {
            hopByHopHeaders.addHeader(new BasicHeader(header, null));
        }
    }

    /**
     * Copy request headers from the servlet client to the proxy request.
     */
    protected void copyRequestHeaders(HttpServletRequest servletRequest, HttpRequest proxyRequest, URI targetUri) {
        // Get an Enumeration of all of the header names sent by the client
        Enumeration enumerationOfHeaderNames = servletRequest.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            String headerName = (String) enumerationOfHeaderNames.nextElement();
            if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH))
                continue;
            if (hopByHopHeaders.containsHeader(headerName))
                continue;
            // As per the Java Servlet API 2.5 documentation:
            //		Some headers, such as Accept-Language can be sent by clients
            //		as several headers each with a different value rather than
            //		sending the header as a comma separated list.
            // Thus, we get an Enumeration of the header values sent by the client
            Enumeration headers = servletRequest.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String headerValue = (String) headers.nextElement();
                // In case the proxy host is running multiple virtual servers,
                // rewrite the Host header to ensure that we get content from
                // the correct virtual server
                if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                    HttpHost host = URIUtils.extractHost(targetUri);
                    headerValue = host.getHostName();
                    if (host.getPort() != -1)
                        headerValue += ":" + host.getPort();
                }
                proxyRequest.addHeader(headerName, headerValue);
            }
        }
    }

    /**
     * Copy proxied response headers back to the servlet client.
     */
    protected void copyResponseHeaders(HttpResponse proxyResponse, HttpServletResponse servletResponse) {
        for (Header header : proxyResponse.getAllHeaders()) {
            if (hopByHopHeaders.containsHeader(header.getName()))
                continue;
            servletResponse.addHeader(header.getName(), header.getValue());
        }
    }

    /**
     * Copy response body data (the entity) from the proxy to the servlet client.
     */
    private void copyResponseEntity(HttpResponse proxyResponse, HttpServletResponse servletResponse) throws IOException {
        HttpEntity entity = proxyResponse.getEntity();
        if (entity != null) {
            OutputStream servletOutputStream = servletResponse.getOutputStream();
            try {
                entity.writeTo(servletOutputStream);
            } finally {
                closeQuietly(servletOutputStream);
            }
        }
    }

    private String rewriteUrlFromRequest(HttpServletRequest servletRequest, URI targetUri) {
        StringBuilder uri = new StringBuilder(500);
        uri.append(targetUri.toString());

        /*
        TODO Confirm this is not needed
        Handle the path given to the servlet
        if (servletRequest.getPathInfo() != null
                && !servletRequest.getPathInfo().equals(targetUri.toString())) {//ex: /my/path.html
                                                                                        //we do not want to append the same path
            uri.append(servletRequest.getPathInfo());
        }
         */

        // Handle the query string
        String queryString = servletRequest.getQueryString();//ex:(following '?'): name=value&foo=bar#fragment
        if (queryString != null && queryString.length() > 0) {
            uri.append('?');
            int fragIdx = queryString.indexOf('#');
            String queryNoFrag = (fragIdx < 0 ? queryString : queryString.substring(0, fragIdx));
            uri.append(encodeUriQuery(queryNoFrag));
            if (fragIdx >= 0) {
                uri.append('#');
                uri.append(encodeUriQuery(queryString.substring(fragIdx + 1)));
            }
        }
        return uri.toString();
    }

    private String rewriteUrlFromResponse(HttpServletRequest servletRequest, String theUrl, URI targetUri) {
        //TODO document example paths
        if (theUrl.startsWith(targetUri.toString())) {
            String curUrl = servletRequest.getRequestURL().toString();//no query
            String pathInfo = servletRequest.getPathInfo();
            if (pathInfo != null) {
                assert curUrl.endsWith(pathInfo);
                curUrl = curUrl.substring(0, curUrl.length() - pathInfo.length());//take pathInfo off
            }
            theUrl = curUrl + theUrl.substring(targetUri.toString().length());
        }
        return theUrl;
    }

    /**
     * <p>Encodes characters in the query or fragment part of the URI.
     * <p/>
     * <p>Unfortunately, an incoming URI sometimes has characters disallowed by the spec.  HttpClient
     * insists that the outgoing proxied request has a valid URI because it uses Java's {@link URI}. To be more
     * forgiving, we must escape the problematic characters.  See the URI class for the spec.
     *
     * @param in example: name=value&foo=bar#fragment
     */
    static CharSequence encodeUriQuery(CharSequence in) {
        //Note that I can't simply use URI.java to encode because it will escape pre-existing escaped things.
        StringBuilder outBuf = null;
        Formatter formatter = null;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            boolean escape = true;
            if (c < 128) {
                if (asciiQueryChars.get((int) c)) {
                    escape = false;
                }
            } else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {//not-ascii
                escape = false;
            }
            if (!escape) {
                if (outBuf != null)
                    outBuf.append(c);
            } else {
                //escape
                if (outBuf == null) {
                    outBuf = new StringBuilder(in.length() + 5 * 3);
                    outBuf.append(in, 0, i);
                    formatter = new Formatter(outBuf);
                }
                //leading %, 0 padded, width 2, capital hex
                formatter.format("%%%02X", (int) c);//TODO
            }
        }
        return outBuf != null ? outBuf : in;
    }


    static {
        char[] c_unreserved = "_-!.~'()*".toCharArray();//plus alphanum
        char[] c_punct = ",;:$&+=".toCharArray();
        char[] c_reserved = "?/[]@".toCharArray();//plus punct

        asciiQueryChars = new BitSet(128);
        for (char c = 'a'; c <= 'z'; c++) asciiQueryChars.set((int) c);
        for (char c = 'A'; c <= 'Z'; c++) asciiQueryChars.set((int) c);
        for (char c = '0'; c <= '9'; c++) asciiQueryChars.set((int) c);
        for (char c : c_unreserved) asciiQueryChars.set((int) c);
        for (char c : c_punct) asciiQueryChars.set((int) c);
        for (char c : c_reserved) asciiQueryChars.set((int) c);

        asciiQueryChars.set((int) '%');//leave existing percent escapes in place
    }

    /**
     * Called from {@link #init(javax.servlet.ServletConfig)}. HttpClient offers many opportunities for customization.
     *
     * @param hcParams value
     */
    private HttpClient createHttpClient(HttpParams hcParams) {

        //todo add lan proxy configuration
        //HttpHost proxy = new HttpHost("10.66.60.21", 8080);
        DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(), hcParams);
        //httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        return httpClient;
    }

    private void readConfigParam(HttpParams hcParams, String hcParamName, Class type) {
        String val_str = getServletConfig().getInitParameter(hcParamName);
        if (val_str == null)
            return;
        Object val_obj;
        if (type == String.class) {
            val_obj = val_str;
        } else {
            try {
                val_obj = type.getMethod("valueOf", String.class).invoke(type, val_str);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        hcParams.setParameter(hcParamName, val_obj);
    }
}

