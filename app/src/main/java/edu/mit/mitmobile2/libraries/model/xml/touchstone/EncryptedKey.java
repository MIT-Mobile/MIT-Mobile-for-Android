package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class EncryptedKey {

    @Attribute(name = "Id")
    private String id;

    @Element(name = "EncryptionMethod")
    @Namespace(prefix = "xenc")
    EncryptionMethod encryptionMethod;

    @Element(name = "KeyInfo")
    @Namespace(prefix = "ds")
    InnerKeyInfo keyInfo;

    @Element(name = "CipherData")
    @Namespace(prefix = "xenc")
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
