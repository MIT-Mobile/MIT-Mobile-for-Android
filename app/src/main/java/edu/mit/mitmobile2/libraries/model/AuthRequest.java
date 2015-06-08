package edu.mit.mitmobile2.libraries.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Namespace(reference = "urn:oasis:names:tc:SAML:2.0:protocol", prefix = "samlp")
@Root(name = "AuthnRequest")
public class AuthRequest {

    @Attribute(name = "ProtocolBinding")
    private String protocolBinding;

    @Attribute(name = "ID")
    private String id;

    @Attribute(name = "AssertionConsumerServiceURL")
    private String consumerServiceUrl;

    @Attribute(name = "Version")
    private String version;

    @Attribute(name = "IssueInstant")
    private String issueInstant;

    @Element(name = "Issuer")
    @Namespace(prefix = "saml", reference = "urn:oasis:names:tc:SAML:2.0:assertion")
    Issuer issuer;

    @Element(name = "NameIDPolicy")
    @Namespace(prefix = "samlp", reference = "urn:oasis:names:tc:SAML:2.0:protocol")
    NameIdPolicy nameIdPolicy;

    public String getProtocolBinding() {
        return protocolBinding;
    }

    public void setProtocolBinding(String ProtocolBinding) {
        this.protocolBinding = ProtocolBinding;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsumerServiceUrl() {
        return consumerServiceUrl;
    }

    public void setConsumerServiceUrl(String AssertionConsumerServiceURL) {
        this.consumerServiceUrl = AssertionConsumerServiceURL;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String Version) {
        this.version = Version;
    }

    public String getIssueInstant() {
        return issueInstant;
    }

    public void setIssueInstant(String IssueInstant) {
        this.issueInstant = IssueInstant;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public NameIdPolicy getNameIdPolicy() {
        return nameIdPolicy;
    }

    public void setNameIdPolicy(NameIdPolicy nameIdPolicy) {
        this.nameIdPolicy = nameIdPolicy;
    }

    @Override
    public String toString() {
        return "ClassPojo [protocolBinding = " + protocolBinding + ", id = " + id + ", consumerServiceUrl = " + consumerServiceUrl + ", version = " + version + ", issueInstant = " + issueInstant + "]";
    }
}
