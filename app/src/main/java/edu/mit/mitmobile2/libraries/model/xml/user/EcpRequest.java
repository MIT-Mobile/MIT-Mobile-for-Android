package edu.mit.mitmobile2.libraries.model.xml.user;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Namespace(prefix = "ecp", reference = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp")
@Root(name = "Request")
public class EcpRequest {

    @Attribute(name = "IsPassive")
    private String isPassive;

    @Namespace(prefix = "S")
    @Attribute(name = "actor")
    private String actor;

    @Namespace(prefix = "S")
    @Attribute(name = "mustUnderstand")
    private String mustUnderstand;

    @Namespace(prefix = "saml")
    @Element(name = "saml:Issuer")
    Issuer issuer;

    public String getIsPassive() {
        return isPassive;
    }

    public void setIsPassive(String IsPassive) {
        this.isPassive = IsPassive;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
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
        return "ClassPojo [isPassive = " + isPassive + "]";
    }
}
