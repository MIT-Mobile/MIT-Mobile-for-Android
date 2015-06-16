package edu.mit.mitmobile2.libraries.model.xml.user;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Attribute;

@Namespace(prefix = "paos", reference = "urn:liberty:paos:2003-08")
@Root(name = "Request")
public class PaosRequest {

    @Attribute
    private String service;

    @Attribute
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

    @Override
    public String toString() {
        return "ClassPojo [service = " + service + ", responseConsumerURL = " + responseConsumerURL + "]";
    }
}
