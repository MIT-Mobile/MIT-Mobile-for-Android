package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class EncryptedData {

    @Attribute(name = "Type")
    private String type;

    @Attribute(name = "Id")
    private String id;

    @Element(name = "EncryptionMethod")
    @Namespace(prefix = "xenc", reference = "http://www.w3.org/2001/04/xmlenc#")
    EncryptionMethod encryptionMethod;

    @Element(name = "KeyInfo")
    @Namespace(prefix = "ds", reference = "http://www.w3.org/2000/09/xmldsig#")
    KeyInfo keyInfo;

    @Element(name = "CipherData")
    @Namespace(prefix = "xenc", reference = "http://www.w3.org/2001/04/xmlenc#")
    CipherData cipherData;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ClassPojo [type = " + type + ", id = " + id + "]";
    }

}
