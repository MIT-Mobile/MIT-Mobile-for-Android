package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Namespace(prefix = "saml2p", reference = "urn:oasis:names:tc:SAML:2.0:protocol")
@Root(name = "Response")
public class Saml2pResponse {

    @Attribute(name = "ID")
    private String id;

    @Attribute(name = "InResponseTo")
    private String inResponseTo;

    @Attribute(name = "Version")
    private String version;

    @Attribute(name = "IssueInstant")
    private String issueInstant;

    @Attribute(name = "Destination")
    private String destination;

    @Element(name = "Issuer")
    @Namespace(prefix = "saml2", reference = "urn:oasis:names:tc:SAML:2.0:assertion")
    Saml2Issuer issuer;

    @Element(name = "Status")
    @Namespace(prefix = "saml2p")
    Saml2pStatus status;

    @Namespace(prefix = "saml2", reference = "urn:oasis:names:tc:SAML:2.0:assertion")
    @Element(name = "EncryptedAssertion")
    EncryptedAssertion encryptedAssertion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInResponseTo() {
        return inResponseTo;
    }

    public void setInResponseTo(String InResponseTo) {
        this.inResponseTo = InResponseTo;
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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String Destination) {
        this.destination = Destination;
    }

    public Saml2Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Saml2Issuer issuer) {
        this.issuer = issuer;
    }

    public Saml2pStatus getStatus() {
        return status;
    }

    public void setStatus(Saml2pStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", inResponseTo = " + inResponseTo + ", version = " + version + ", issueInstant = " + issueInstant + ", destination = " + destination + "]";
    }

}
