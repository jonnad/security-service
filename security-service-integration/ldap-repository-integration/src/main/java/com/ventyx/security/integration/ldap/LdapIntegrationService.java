package com.ventyx.security.integration.ldap;

import com.unboundid.ldap.sdk.*;
import com.ventyx.security.api.UserService;
import com.ventyx.security.api.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * LDAP integration component
 */
@Service
@Qualifier("ldapIntegrationService")
public class LdapIntegrationService implements UserService {

    private @Value(value = "${ldap.hostname}") String ldapHostName;
    private @Value(value = "${ldap.hostport}") String ldapHostPort;
    private @Value(value = "${ldap.binddn}") String ldapBindDn;
    private @Value(value = "${ldap.bindpassword}") String ldapBindPassword;
    private @Value(value = "${ldap.connectionpool.size}") String ldapConnectionPoolSize;

    private LDAPConnectionPool ldapConnectionPool;

    @PostConstruct
    public void createConnectionPool() {
        try {
            LDAPConnection ldapConnection = new LDAPConnection(ldapHostName, Integer.parseInt(ldapHostPort), ldapBindDn, ldapBindPassword);
            ldapConnectionPool = new LDAPConnectionPool(ldapConnection, Integer.parseInt(ldapConnectionPoolSize));

        } catch (LDAPException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public User validateUser(User user) {

        try {
            ReadOnlySearchRequest readOnlySearchRequest = new SearchRequest("dc=example,dc=com", SearchScope.SUB, "uid=hnelson");
            SearchResult searchResult = ldapConnectionPool.search((ReadOnlySearchRequest)readOnlySearchRequest);

            if (searchResult != null && searchResult.getEntryCount() > 0) {
                System.out.println("Found something!");

                SearchResultEntry searchResultEntry = searchResult.getSearchEntries().get(0);
                String encryptedPassword = searchResultEntry.getAttribute("userPassword").getValue();

                String userPassword = generateEncryptedPasswordForComparison("password");

                userPassword.equals(encryptedPassword);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public User getUser(String userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private String generateEncryptedPasswordForComparison(String userPassword) throws NoSuchAlgorithmException{

        // Calculate hash value
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(userPassword.getBytes());
        byte[] bytes = messageDigest.digest();

        // Print out value in Base64 encoding
        BASE64Encoder base64encoder = new BASE64Encoder();
        String hash = base64encoder.encode(bytes);
        return "{SHA}" + hash;

    }

}
