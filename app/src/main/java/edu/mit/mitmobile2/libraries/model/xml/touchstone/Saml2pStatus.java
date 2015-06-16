package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class Saml2pStatus {

    @Element(name = "StatusCode")
    @Namespace(prefix = "saml2p")
    Saml2pStatusCode statusCode;

    public Saml2pStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Saml2pStatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
