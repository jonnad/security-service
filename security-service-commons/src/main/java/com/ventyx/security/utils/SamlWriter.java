package com.ventyx.security.utils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * This is a demo class which creates a valid SAML 2.0 Assertion.
 */
public class SamlWriter
{

    String acsHostUrl = "accesscontrol.windows.net";
    String acsnamespace ="cisacssample";
    static String clientIdentity = "TestClient";
    String relyingpartyscope = "https://xxxx.cloudapp.net/";
    static String restriction = "https://xxxx.accesscontrol.windows.net";

    public static void main(String[] args) {
        try {


            SAMLInputContainer input = new SAMLInputContainer();
            input.strIssuer = clientIdentity;
            input.strNameID = clientIdentity;
            input.strNameQualifier = "";
            input.sessionId = "";
            input.restriction =restriction;


            Assertion assertion = SamlWriter.buildDefaultAssertion(input);
            AssertionMarshaller marshaller = new AssertionMarshaller();
            Element plaintextElement = marshaller.marshall(assertion);
            String originalAssertionString = XMLHelper.nodeToString(plaintextElement);

            //System.out.println("Assertion String: " + originalAssertionString);

            // TODO: now you can also add encryption....

        } catch (MarshallingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static XMLObjectBuilderFactory builderFactory;

    public static XMLObjectBuilderFactory getSAMLBuilder() throws ConfigurationException{

        if(builderFactory == null){
            // OpenSAML 2.3
            DefaultBootstrap.bootstrap();
            builderFactory = Configuration.getBuilderFactory();
        }

        return builderFactory;
    }

    /**
     * Builds a SAML Attribute of type String
     * @param name
     * @param value
     * @param builderFactory
     * @return
     * @throws ConfigurationException
     */
    public static Attribute buildStringAttribute(String name, String value, XMLObjectBuilderFactory builderFactory) throws ConfigurationException
    {
        SAMLObjectBuilder attrBuilder = (SAMLObjectBuilder) getSAMLBuilder().getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
        Attribute attrFirstName = (Attribute) attrBuilder.buildObject();
        attrFirstName.setName(name);

        // Set custom Attributes
        XMLObjectBuilder stringBuilder = getSAMLBuilder().getBuilder(XSString.TYPE_NAME);
        XSString attrValueFirstName = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attrValueFirstName.setValue(value);

        attrFirstName.getAttributeValues().add(attrValueFirstName);
        return attrFirstName;
    }

    /**
     * Helper method which includes some basic SAML fields which are part of almost every SAML Assertion.
     *
     * @param input
     * @return
     */
    public static Assertion buildDefaultAssertion(SAMLInputContainer input)
    {
        try
        {
            // Create the NameIdentifier
            SAMLObjectBuilder nameIdBuilder = (SAMLObjectBuilder) SamlWriter.getSAMLBuilder().getBuilder(NameID.DEFAULT_ELEMENT_NAME);
            NameID nameId = (NameID) nameIdBuilder.buildObject();
            nameId.setValue(input.getStrNameID());



            // Create the SubjectConfirmation

            SAMLObjectBuilder confirmationMethodBuilder = (SAMLObjectBuilder)  SamlWriter.getSAMLBuilder().getBuilder(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
            SubjectConfirmationData confirmationMethod = (SubjectConfirmationData) confirmationMethodBuilder.buildObject();
            DateTime now = new DateTime();



            SAMLObjectBuilder subjectConfirmationBuilder = (SAMLObjectBuilder) SamlWriter.getSAMLBuilder().getBuilder(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
            SubjectConfirmation subjectConfirmation = (SubjectConfirmation) subjectConfirmationBuilder.buildObject();

            subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
            //should be Bearer



            // Create the Subject
            SAMLObjectBuilder subjectBuilder = (SAMLObjectBuilder) SamlWriter.getSAMLBuilder().getBuilder(Subject.DEFAULT_ELEMENT_NAME);
            Subject subject = (Subject) subjectBuilder.buildObject();

            subject.setNameID(nameId);
            subject.getSubjectConfirmations().add(subjectConfirmation);



            // Builder Attributes
            SAMLObjectBuilder attrStatementBuilder = (SAMLObjectBuilder) SamlWriter.getSAMLBuilder().getBuilder(AttributeStatement.DEFAULT_ELEMENT_NAME);
            AttributeStatement attrStatement = (AttributeStatement) attrStatementBuilder.buildObject();

            // Create the attribute statement
            Map<String,String> attributes = input.getAttributes();
            if(attributes != null){
                Set<String> keySet = attributes.keySet();
                for (String key : keySet) {
                    Attribute attrFirstName = buildStringAttribute(key, attributes.get(key), getSAMLBuilder());
                    attrStatement.getAttributes().add(attrFirstName);
                }
            }


            // Create the audience restriction
            SAMLObjectBuilder audienceRestrictionnBuilder = (SAMLObjectBuilder) SamlWriter.getSAMLBuilder().getBuilder(AudienceRestriction.DEFAULT_ELEMENT_NAME);
            AudienceRestriction audienceRestriction = (AudienceRestriction)audienceRestrictionnBuilder.buildObject();

            // Create the audience 
            SAMLObjectBuilder audienceBuilder = (SAMLObjectBuilder) SamlWriter.getSAMLBuilder().getBuilder(Audience.DEFAULT_ELEMENT_NAME);
            Audience audience = (Audience)audienceBuilder.buildObject();
            audience.setAudienceURI(input.getRestriction());
            //add in the audience
            audienceRestriction.getAudiences().add(audience);

            SAMLObjectBuilder conditionsBuilder = (SAMLObjectBuilder) SamlWriter.getSAMLBuilder().getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
            Conditions conditions = (Conditions) conditionsBuilder.buildObject();

            // conditions.getConditions().add(condition);
            conditions.getAudienceRestrictions().add(audienceRestriction);
            conditions.setNotBefore(now);
            conditions.setNotOnOrAfter(new DateTime(9998, 12, 31, 23, 59, 59, 999) );


            // Create Issuer
            SAMLObjectBuilder issuerBuilder = (SAMLObjectBuilder) SamlWriter.getSAMLBuilder().getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
            Issuer issuer = (Issuer) issuerBuilder.buildObject();
            issuer.setValue(input.getStrIssuer());

            // Create the assertion
            SAMLObjectBuilder assertionBuilder = (SAMLObjectBuilder) SamlWriter.getSAMLBuilder().getBuilder(Assertion.DEFAULT_ELEMENT_NAME);
            Assertion assertion = (Assertion) assertionBuilder.buildObject();
            assertion.setID( "_"+UUID.randomUUID().toString());
            assertion.setSubject(subject);
            assertion.setIssuer(issuer);
            assertion.setIssueInstant(now);
            assertion.setVersion(SAMLVersion.VERSION_20);



            assertion.setConditions(conditions);


            return assertion;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static class SAMLInputContainer
    {

        private String restriction;
        private String strIssuer;
        private String strNameID;
        private String strNameQualifier;
        private String sessionId;
        private int maxSessionTimeoutInMinutes = 15; // default is 15 minutes

        private Map<String,String> attributes;

        /**
         * Returns the restriction.
         *
         * @return the restriction
         */
        public String getRestriction()
        {
            return restriction;
        }

        /**
         * Sets the restriction.
         *
         * @param restriction
         *            the restriction to set
         */
        public void setRestriction(String restriction)
        {
            this.restriction = restriction;
        }


        /**
         * Returns the strIssuer.
         *
         * @return the strIssuer
         */
        public String getStrIssuer()
        {
            return strIssuer;
        }

        /**
         * Sets the strIssuer.
         *
         * @param strIssuer
         *            the strIssuer to set
         */
        public void setStrIssuer(String strIssuer)
        {
            this.strIssuer = strIssuer;
        }

        /**
         * Returns the strNameID.
         *
         * @return the strNameID
         */
        public String getStrNameID()
        {
            return strNameID;
        }

        /**
         * Sets the strNameID.
         *
         * @param strNameID
         *            the strNameID to set
         */
        public void setStrNameID(String strNameID)
        {
            this.strNameID = strNameID;
        }

        /**
         * Returns the strNameQualifier.
         *
         * @return the strNameQualifier
         */
        public String getStrNameQualifier()
        {
            return strNameQualifier;
        }

        /**
         * Sets the strNameQualifier.
         *
         * @param strNameQualifier
         *            the strNameQualifier to set
         */
        public void setStrNameQualifier(String strNameQualifier)
        {
            this.strNameQualifier = strNameQualifier;
        }

        /**
         * Sets the attributes.
         *
         * @param attributes
         *            the attributes to set
         */
        public void setAttributes(Map<String,String> attributes)
        {
            this.attributes = attributes;
        }

        /**
         * Returns the attributes.
         *
         * @return the attributes
         */
        public Map<String,String> getAttributes()
        {
            return attributes;
        }

        /**
         * Sets the sessionId.
         * @param sessionId the sessionId to set
         */
        public void setSessionId(String sessionId)
        {
            this.sessionId = sessionId;
        }

        /**
         * Returns the sessionId.
         * @return the sessionId
         */
        public String getSessionId()
        {
            return sessionId;
        }

        /**
         * Sets the maxSessionTimeoutInMinutes.
         * @param maxSessionTimeoutInMinutes the maxSessionTimeoutInMinutes to set
         */
        public void setMaxSessionTimeoutInMinutes(int maxSessionTimeoutInMinutes)
        {
            this.maxSessionTimeoutInMinutes = maxSessionTimeoutInMinutes;
        }

        /**
         * Returns the maxSessionTimeoutInMinutes.
         * @return the maxSessionTimeoutInMinutes
         */
        public int getMaxSessionTimeoutInMinutes()
        {
            return maxSessionTimeoutInMinutes;
        }

    }

    public static Assertion getSamlAssertion() {

        Assertion answer = null;
        try {


            SAMLInputContainer input = new SAMLInputContainer();
            input.strIssuer = clientIdentity;
            input.strNameID = clientIdentity;
            input.strNameQualifier = "";
            input.sessionId = "";
            input.restriction =restriction;


            Assertion assertion = SamlWriter.buildDefaultAssertion(input);
            AssertionMarshaller marshaller = new AssertionMarshaller();
            Element plaintextElement = marshaller.marshall(assertion);
            String originalAssertionString = XMLHelper.nodeToString(plaintextElement);

            System.out.println("Assertion String: " + originalAssertionString);

            answer = assertion;
            // TODO: now you can also add encryption....


        } catch (MarshallingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return answer;
    }



}
