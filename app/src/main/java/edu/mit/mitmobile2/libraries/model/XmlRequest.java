package edu.mit.mitmobile2.libraries.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class XmlRequest {

    @Attribute(required = false)
    private String service;

    @Attribute(required = false)
    private String responseConsumerURL;

    @Namespace(prefix = "S")
    @Attribute(name = "actor")
    private String actor;

    @Namespace(prefix = "S")
    @Attribute(name = "mustUnderstand")
    private String mustUnderstand;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getResponseConsumerURL() {
        return responseConsumerURL;
    }

    public void setResponseConsumerURL(String responseConsumerURL) {
        this.responseConsumerURL = responseConsumerURL;
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

    @Attribute(name = "IsPassive", required = false)
    private String isPassive;

    @Element(name = "Issuer", required = false)
    @Namespace(prefix = "saml")
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

    @Override
    public String toString() {
        return "ClassPojo [isPassive = " + isPassive + "]";
    }
}
