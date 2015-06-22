package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import edu.mit.mitmobile2.libraries.model.xml.touchstone.X509Certificate;

@Root
@Namespace(prefix = "ds", reference = "http://www.w3.org/2000/09/xmldsig#")
public class X509Data {

    @Element(name = "X509Certificate")
    @Namespace(prefix = "ds", reference = "http://www.w3.org/2000/09/xmldsig#")
    X509Certificate x509Certificate;
}
