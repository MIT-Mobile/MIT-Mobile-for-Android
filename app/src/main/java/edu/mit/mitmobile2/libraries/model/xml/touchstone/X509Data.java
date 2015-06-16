package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

import edu.mit.mitmobile2.libraries.model.xml.touchstone.X509Certificate;

public class X509Data {

    @Element(name = "X509Certificate")
    @Namespace(prefix = "ds")
    X509Certificate x509Certificate;
}
