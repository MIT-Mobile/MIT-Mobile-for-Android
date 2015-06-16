package edu.mit.mitmobile2.libraries.model.xml.user;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "samlp:NameIDPolicy")
public class NameIdPolicy {

    @Attribute(name = "AllowCreate")
    private String allowCreate;

    public String getAllowCreate() {
        return allowCreate;
    }

    public void setAllowCreate(String AllowCreate) {
        this.allowCreate = AllowCreate;
    }

    @Override
    public String toString() {
        return "ClassPojo [allowCreate = " + allowCreate + "]";
    }
}
