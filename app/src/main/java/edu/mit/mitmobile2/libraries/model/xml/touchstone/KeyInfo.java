package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class KeyInfo {

    @Element(name = "EncryptedKey")
    @Namespace(prefix = "xenc", reference = "http://www.w3.org/2001/04/xmlenc#")
    EncryptedKey encryptedKey;
}
