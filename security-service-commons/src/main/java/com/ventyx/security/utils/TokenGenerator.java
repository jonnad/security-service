package com.ventyx.security.utils;

import com.ventyx.security.api.model.ServiceConfiguration;
import com.ventyx.security.api.model.Token;
import org.bouncycastle.util.encoders.Base64;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.saml2.encryption.Encrypter;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.encryption.EncryptionConstants;
import org.opensaml.xml.encryption.EncryptionParameters;
import org.opensaml.xml.encryption.InlineEncryptedKeyResolver;
import org.opensaml.xml.encryption.KeyEncryptionParameters;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.StaticKeyInfoCredentialResolver;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.util.*;

/**
 * Utility class for generating SAML tokens
 */
public class TokenGenerator {

    private static final Logger log = LoggerFactory.getLogger(TokenGenerator.class);

    private static XMLObjectBuilderFactory xmlObjectBuilderFactory = null;
    private static BasicX509Credential encrpytionCredential = null;
    private static BasicX509Credential decryptionCredential = null;

    static {
        try {
            DefaultBootstrap.bootstrap();
            xmlObjectBuilderFactory = Configuration.getBuilderFactory();

            //grab the KeyStore file
            File publicKeyStoreFile = new File("C:\\projects\\workspaces\\ssl\\ventyx.selfsigned.public.keystore.jks");
            File privateKeyStoreFile = new File("C:\\projects\\workspaces\\ssl\\ventyx.selfsigned.keystore.jks");

            KeyStore publicKeyStore = KeyStore.getInstance("JKS");
            publicKeyStore.load(new FileInputStream(publicKeyStoreFile), "password".toCharArray());

            KeyStore privateKeyStore = KeyStore.getInstance("JKS");
            privateKeyStore.load(new FileInputStream(privateKeyStoreFile), "password".toCharArray());

            //create the decryption credential
            KeyStore.PrivateKeyEntry entry = ((KeyStore.PrivateKeyEntry) privateKeyStore.getEntry("development", new KeyStore.PasswordProtection("password".toCharArray())));
            RSAPrivateKey privateKey = (RSAPrivateKey)entry.getPrivateKey();
            decryptionCredential = new BasicX509Credential();
            decryptionCredential.setPrivateKey(privateKey);

            //create the encryption credential
            java.security.cert.Certificate certificate = publicKeyStore.getCertificate("development");
            encrpytionCredential = new BasicX509Credential();
            encrpytionCredential.setPublicKey(certificate.getPublicKey());


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void main (String[] args) {
        Token token = generateAssertion(null, "123456787", false, false);
        System.out.println(token);

        try {
            decryptAssertion(token.getValue());
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public static Token generateAssertion(ServiceConfiguration serviceConfiguration, String nameId, boolean encrypt, boolean encode) {

        try {

            String tokenId = "_" + UUID.randomUUID().toString();

            Map<String, String> attributes = new HashMap<String, String>();
            Assertion assertion = buildDefaultAssertion("jhamel@ventyx.abb.com", attributes, tokenId, "http://customer.organization.com", "http://ventyx.abb.com/idp/SAML2", 10);

            EncryptedAssertion encryptedAssertion = encryptAssertion(assertion, encrpytionCredential);
            Response response = buildDefaultResponse(encryptedAssertion);

            Token token = new Token();
            token.setTokenId(tokenId);
            token.setServiceConfiguration(serviceConfiguration);

            //dump the response
            String responseString = transformResponse(response);

            if (encode) {
                token.setValue(new String(Base64.encode(responseString.getBytes())));
            } else {
                token.setValue(responseString);
            }

            return token;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean decryptAssertion(String serializedAssertion) {

        try {
            //base64 decode
            String decodedToken = null;
            try {
                decodedToken = new String(org.opensaml.xml.util.Base64.decode(serializedAssertion));
            } catch (Exception ex) {
                log.error("Cannot decode token : " + serializedAssertion, ex);
                return false;
            }

            BasicParserPool parser = new BasicParserPool();
            parser.setNamespaceAware(true);
            Document document = parser.parse(new StringReader(decodedToken));
            UnmarshallerFactory unmarshallerFactory =
                    Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller =
                    unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
            Response response = (Response)
                    unmarshaller.unmarshall(document.getDocumentElement());

            //PublicKey publicKey = encryptingKeyPair.getPublic();
            //PrivateKey privateKey = encryptingKeyPair.getPrivate();
            //Credential credential = SecurityHelper.getSimpleCredential(publicKey,privateKey);

            StaticKeyInfoCredentialResolver resolver = new StaticKeyInfoCredentialResolver(decryptionCredential);
            Decrypter decrypter = new Decrypter(null, resolver, new InlineEncryptedKeyResolver());
            Assertion assertion = decrypter.decrypt(response.getEncryptedAssertions().get(0));



            return true;
        } catch (Exception ex) {
            log.error("Error decrypting token", ex);
            return false;
        }
    }


    private static Response buildDefaultResponse(EncryptedAssertion encryptedAssertion) {

        SAMLObjectBuilder responseBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(Response.DEFAULT_ELEMENT_NAME);
        Response samlResponse = (Response) responseBuilder.buildObject();
        samlResponse.getEncryptedAssertions().add(encryptedAssertion);

        return samlResponse;
    }

    private static Assertion buildDefaultAssertion(String nameIdValue, Map<String, String> attributes, String tokenId, String restrictionValue, String issuerValue, Integer tokenExpirationInMinutes) {

        try {
            // Create the NameIdentifier
            SAMLObjectBuilder nameIdBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME);
            NameID nameId = (NameID) nameIdBuilder.buildObject();
            nameId.setValue(nameIdValue);

            // Create the SubjectConfirmation
            SAMLObjectBuilder confirmationMethodBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
            SubjectConfirmationData confirmationMethod = (SubjectConfirmationData) confirmationMethodBuilder.buildObject();
            DateTime now = new DateTime();

            SAMLObjectBuilder subjectConfirmationBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
            SubjectConfirmation subjectConfirmation = (SubjectConfirmation) subjectConfirmationBuilder.buildObject();

            //should be Bearer
            subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

            // Create the Subject
            SAMLObjectBuilder subjectBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(Subject.DEFAULT_ELEMENT_NAME);
            Subject subject = (Subject) subjectBuilder.buildObject();

            subject.setNameID(nameId);
            subject.getSubjectConfirmations().add(subjectConfirmation);

            // Builder Attributes
            SAMLObjectBuilder attrStatementBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(AttributeStatement.DEFAULT_ELEMENT_NAME);
            AttributeStatement attrStatement = (AttributeStatement) attrStatementBuilder.buildObject();

            // Create the attribute statement
            if (attributes != null) {
                Set<String> keySet = attributes.keySet();
                for (String key : keySet) {
                    Attribute attrFirstName = buildStringAttribute(key, attributes.get(key));
                    attrStatement.getAttributes().add(attrFirstName);
                }
            }

            // Create the audience restriction
            SAMLObjectBuilder audienceRestrictionBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(AudienceRestriction.DEFAULT_ELEMENT_NAME);
            AudienceRestriction audienceRestriction = (AudienceRestriction) audienceRestrictionBuilder.buildObject();

            // Create the audience 
            SAMLObjectBuilder audienceBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(Audience.DEFAULT_ELEMENT_NAME);
            Audience audience = (Audience) audienceBuilder.buildObject();
            audience.setAudienceURI(restrictionValue);
            //add in the audience
            audienceRestriction.getAudiences().add(audience);

            SAMLObjectBuilder conditionsBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
            Conditions conditions = (Conditions) conditionsBuilder.buildObject();
            conditions.getAudienceRestrictions().add(audienceRestriction);
            conditions.setNotBefore(now);

            DateTime notOnOrAfterTime = new DateTime();
            notOnOrAfterTime.plusMinutes(tokenExpirationInMinutes);
            conditions.setNotOnOrAfter(notOnOrAfterTime);

            // Create Issuer
            SAMLObjectBuilder issuerBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
            Issuer issuer = (Issuer) issuerBuilder.buildObject();
            issuer.setValue(issuerValue);

            // Create the assertion
            SAMLObjectBuilder assertionBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(Assertion.DEFAULT_ELEMENT_NAME);
            Assertion assertion = (Assertion) assertionBuilder.buildObject();
            assertion.setID(tokenId);
            assertion.setSubject(subject);
            assertion.setIssuer(issuer);
            assertion.setIssueInstant(now);
            assertion.setVersion(SAMLVersion.VERSION_20);
            assertion.setConditions(conditions);

            return assertion;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Encrypts a completely assembled assertion
     *
     * @param assertion
     * @param encrpytionCredential
     * @return encryptedAssertion
     */
    private static EncryptedAssertion encryptAssertion(Assertion assertion, BasicX509Credential encrpytionCredential) {

        try {
            Credential symmetricCredential = SecurityHelper.getSimpleCredential(
                    SecurityHelper.generateSymmetricKey(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));

            EncryptionParameters encParams = new EncryptionParameters();
            encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
            encParams.setEncryptionCredential(symmetricCredential);

            KeyEncryptionParameters kek = new KeyEncryptionParameters();
            kek.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
            kek.setEncryptionCredential(encrpytionCredential);

            Encrypter encrypter = new Encrypter(encParams, kek);
            encrypter.setKeyPlacement(Encrypter.KeyPlacement.INLINE);

            return encrypter.encrypt(assertion);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Pretty prints an assertion
     *
     * @param assertion
     */
    private static void dumpAssertion(Assertion assertion) {

        try {

            AssertionMarshaller marshaller = new AssertionMarshaller();
            Element plaintextElement = marshaller.marshall(assertion);
            String originalAssertionString = XMLHelper.prettyPrintXML(plaintextElement);

            System.out.println("Assertion String: " + originalAssertionString);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    /**
     * Pretty prints a response
     *
     * @param response
     */
    private static String transformResponse(Response response) {

        try {

            ResponseMarshaller marshaller = new ResponseMarshaller();
            Element plaintextElement = marshaller.marshall(response);
            String originalResponseString = XMLHelper.nodeToString(plaintextElement);

            return originalResponseString;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;

    }

    /**
     * Builds a SAML attribute set from a name/value pair
     *
     * @param name
     * @param value
     * @return attribute value
     * @throws ConfigurationException
     */
    private static Attribute buildStringAttribute(String name, String value) throws ConfigurationException {
        SAMLObjectBuilder attrBuilder = (SAMLObjectBuilder) xmlObjectBuilderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
        Attribute attrFirstName = (Attribute) attrBuilder.buildObject();
        attrFirstName.setName(name);

        // Set custom Attributes
        XMLObjectBuilder stringBuilder = xmlObjectBuilderFactory.getBuilder(XSString.TYPE_NAME);
        XSString attrValueFirstName = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attrValueFirstName.setValue(value);

        attrFirstName.getAttributeValues().add(attrValueFirstName);
        return attrFirstName;
    }

}
