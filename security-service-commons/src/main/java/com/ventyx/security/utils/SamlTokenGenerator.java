package com.ventyx.security.utils;

import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.SecurityConfiguration;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class SamlTokenGenerator {

    private final static Logger logger = LoggerFactory.getLogger(SamlTokenGenerator.class);
    private static Credential signingCredential = null;
    private final static Signature signature = null;
    private final static String password = "secret";
    private final static String certificateAliasName = "selfsigned";
    final static String fileName = "C:\\projects\\workspaces\\security-service\\security-service-web\\src\\main\\resources\\keystore.jks";

    @SuppressWarnings("static-access")
    private static void intializeCredentials() {
        KeyStore ks = null;
        FileInputStream fis = null;
        String password = "password";

        // Get Default Instance of KeyStore
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            logger.error("Error while Intializing Keystore", e);
        }

        // Read Ketstore as file Input Stream
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            logger.error("Unable to found KeyStore with the given keystoere name ::" + fileName, e);
        }

        // Load KeyStore
        try {
            ks.load(fis, password.toCharArray());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to Load the KeyStore:: ", e);
        } catch (CertificateException e) {
            logger.error("Failed to Load the KeyStore:: ", e);
        } catch (IOException e) {
            logger.error("Failed to Load the KeyStore:: ", e);
        }

        // Close InputFileStream
        try {
            fis.close();
        } catch (IOException e) {
            logger.error("Failed to close file stream:: ", e);
        }

        // Get Private Key Entry From Certificate
        KeyStore.PrivateKeyEntry pkEntry = null;
        try {
            pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(certificateAliasName, new KeyStore.PasswordProtection(password.toCharArray()));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to Get Private Entry From the keystore:: " + fileName, e);
        } catch (UnrecoverableEntryException e) {
            logger.error("Failed to Get Private Entry From the keystore:: " + fileName, e);
        } catch (KeyStoreException e) {
            logger.error("Failed to Get Private Entry From the keystore:: " + fileName, e);
        }
        PrivateKey pk = pkEntry.getPrivateKey();

        X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
        BasicX509Credential credential = new BasicX509Credential();
        credential.setEntityCertificate(certificate);
        credential.setPrivateKey(pk);
        signingCredential = credential;

        logger.info("Private Key" + pk.toString());

    }

    public static void main (String[] args) {
        intializeCredentials();

        Signature signature = null;
        try
        {
            DefaultBootstrap.bootstrap();
        }
        catch (ConfigurationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        signature = (Signature) Configuration.getBuilderFactory().getBuilder(Signature.DEFAULT_ELEMENT_NAME)
                .buildObject(Signature.DEFAULT_ELEMENT_NAME);

        signature.setSigningCredential(signingCredential);

        // This is also the default if a null SecurityConfiguration is specified
        SecurityConfiguration secConfig = Configuration.getGlobalSecurityConfiguration();
        // If null this would result in the default KeyInfoGenerator being used
        String keyInfoGeneratorProfile = "XMLSignature";

        try
        {
            SecurityHelper.prepareSignatureParams(signature, signingCredential, secConfig, null);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (org.opensaml.xml.security.SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Assertion assertion = SamlWriter.getSamlAssertion();

        assertion.setSignature(signature);

        try
        {
            Configuration.getMarshallerFactory().getMarshaller(assertion).marshall(assertion);
        }
        catch (MarshallingException e)
        {
            e.printStackTrace();
        }

        try
        {
            Signer.signObject(signature);
        }
        catch (SignatureException e)
        {
            e.printStackTrace();
        }

        try {
            AssertionMarshaller marshaller = new AssertionMarshaller();;
            Element plain = marshaller.marshall(assertion);
            // response.setSignature(sign);
            String samlResponse = XMLHelper.nodeToString(plain);
            System.out.println("********************\n*\n***********::" + samlResponse);

        } catch (MarshallingException ex) {
            ex.printStackTrace();
        }
    }

    public String generateSamlToken(String user, String credential, String applicationId, String serviceName) {

        //validate the calling user and service information

        //build a SAML token with the proper expiry etc. for this service

        //sign the assertion and encrypt etc.

        //base64 encode

        return "";
    }


}
