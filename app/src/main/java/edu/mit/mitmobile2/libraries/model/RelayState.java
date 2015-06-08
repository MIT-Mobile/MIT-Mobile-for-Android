package edu.mit.mitmobile2.libraries.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "ecp:RelayState")
public class RelayState {

    @Namespace(prefix = "S")
    @Attribute(name = "actor")
    private String actor;

    @Namespace(prefix = "S")
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
