package edu.mit.mitmobile2.libraries.model.xml.touchstone;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class EncryptedAssertion {

    @Element(name = "EncryptedData")
    @Namespace(prefix = "xenc", reference = "http://www.w3.org/2001/04/xmlenc#")
    EncryptedData encryptedData;

}
