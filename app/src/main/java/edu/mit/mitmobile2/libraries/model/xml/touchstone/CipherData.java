package edu.mit.mitmobile2.libraries.model.xml.touchstone;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class CipherData {

    @Element(name = "CipherValue")
    @Namespace(prefix = "xenc")
    CipherValue cipherValue;
}
