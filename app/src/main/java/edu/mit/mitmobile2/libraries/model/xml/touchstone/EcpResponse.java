package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Namespace(prefix = "ecp", reference = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp")
@Root(name = "Response")
public class EcpResponse {

    @Attribute(name = "AssertionConsumerServiceURL")
    private String assertionConsumerServiceURL;

    @Namespace(prefix = "soap11")
    @Attribute(name = "actor")
    private String actor;

    @Namespace(prefix = "soap11")
    @Attribute(name = "mustUnderstand")
    private String mustUnderstand;

    public String getAssertionConsumerServiceURL() {
        return assertionConsumerServiceURL;
    }

    public void setAssertionConsumerServiceURL(String AssertionConsumerServiceURL) {
        this.assertionConsumerServiceURL = AssertionConsumerServiceURL;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getMustUnderstand() {
        return mustUnderstand;
    }

    public void setMustUnderstand(String mustUnderstand) {
        this.mustUnderstand = mustUnderstand;
    }

    @Override
    public String toString() {
        return "ClassPojo [AssertionConsumerServiceURL = " + assertionConsumerServiceURL + "]";
    }
}
