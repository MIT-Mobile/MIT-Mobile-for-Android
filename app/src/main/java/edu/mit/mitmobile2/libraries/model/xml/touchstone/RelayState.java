package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Text;

public class RelayState {


    public RelayState(String actor, String mustUnderstand, String value) {
        this.actor = actor;
        this.mustUnderstand = mustUnderstand;
        this.value = value;
    }

    @Namespace(prefix = "soap11")
    @Attribute(name = "actor")
    private String actor;

    @Namespace(prefix = "soap11")
    @Attribute(name = "mustUnderstand")
    private String mustUnderstand;

    @Text
    String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
