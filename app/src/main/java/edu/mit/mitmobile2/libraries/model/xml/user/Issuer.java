package edu.mit.mitmobile2.libraries.model.xml.user;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "saml:Issuer")
public class Issuer {

    @Attribute(required = false)
    private String content;

    @Text
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ClassPojo [content = " + content + "]";
    }
}
