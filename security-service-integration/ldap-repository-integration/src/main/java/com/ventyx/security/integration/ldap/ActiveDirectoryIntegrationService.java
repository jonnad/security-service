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
@Qualifier(value = "activeDirectoryIntegrationService")
public class ActiveDirectoryIntegrationService implements UserService {

    private @Value(value = "${ad.ldap.hostname}") String ldapHostName;
    private @Value(value = "${ad.ldap.hostport}") String ldapHostPort;
    private @Value(value = "${ad.ldap.bindsuffix}") String ldapBindSuffix;
    private @Value(value = "${ad.ldap.bindprefix}") String ldapBindPrefix;


    @Override
    public User validateUser(User user) {

        try {

            String userDistinguishedName = ldapBindPrefix + user.getUserId() + "," + ldapBindSuffix;

            LDAPConnection ldapConnection = new LDAPConnection(ldapHostName, Integer.parseInt(ldapHostPort), userDistinguishedName, user.getCredential());

            ReadOnlySearchRequest readOnlySearchRequest = new SearchRequest(ldapBindSuffix, SearchScope.SUB, ldapBindPrefix + user.getUserId());
            SearchResult searchResult = ldapConnection.search(readOnlySearchRequest);

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

    private String generateEncryptedPasswordForComparison(String userPassword) {

        try {

            // Calculate hash value
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(userPassword.getBytes());
            byte[] bytes = messageDigest.digest();

            // Print out value in Base64 encoding
            BASE64Encoder base64encoder = new BASE64Encoder();
            String hash = base64encoder.encode(bytes);
            System.out.println("{SHA}" + hash);
            return "{SHA}" + hash;

        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
