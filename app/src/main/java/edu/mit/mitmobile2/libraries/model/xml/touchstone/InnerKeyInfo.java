package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "KeyInfo")
@Namespace(prefix = "ds")
public class InnerKeyInfo {

    @Element(name = "X509Data")
    X509Data x509Data;
}
