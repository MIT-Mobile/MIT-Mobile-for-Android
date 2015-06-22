package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
@Namespace(prefix = "xenc")
public class EncryptedKey {

    @Attribute(name = "Id")
    private String id;

    @Element(name = "EncryptionMethod")
    @Namespace(prefix = "xenc", reference = "http://www.w3.org/2001/04/xmlenc#")
    EncryptionMethod encryptionMethod;

    @Element(name = "KeyInfo")
    @Namespace(prefix = "ds", reference = "http://www.w3.org/2000/09/xmldsig#")
    InnerKeyInfo keyInfo;

    @Element(name = "CipherData")
    @Namespace(prefix = "xenc", reference = "http://www.w3.org/2001/04/xmlenc#")
    CipherData cipherData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    public void setEncryptionMethod(EncryptionMethod encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + "]";
    }
}
