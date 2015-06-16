package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Namespace(prefix = "saml2", reference = "urn:oasis:names:tc:SAML:2.0:assertion")
@Root(name = "Issuer")
public class Saml2Issuer {

    @Text
    private String value;

    @Attribute(name = "Format")
    private String format;

    public String getContent() {
        return value;
    }

    public void setContent(String content) {
        this.value = content;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String Format) {
        this.format = Format;
    }

    @Override
    public String toString() {
        return "ClassPojo [content = " + value + ", format = " + format + "]";
    }
}
