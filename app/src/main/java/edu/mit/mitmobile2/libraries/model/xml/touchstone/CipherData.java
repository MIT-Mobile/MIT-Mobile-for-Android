package edu.mit.mitmobile2.libraries.model.xml.touchstone;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
@Namespace(prefix = "xenc")
public class CipherData {

    @Element(name = "CipherValue")
    @Namespace(prefix = "xenc", reference = "http://www.w3.org/2001/04/xmlenc#")
    CipherValue cipherValue;
}
