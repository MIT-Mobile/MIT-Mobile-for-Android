package edu.mit.mitmobile2.libraries.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Namespace(prefix = "S", reference = "http://schemas.xmlsoap.org/soap/envelope/")
@Root(name = "Body")
public class XmlBody {

    @Element(name = "AuthnRequest")
    @Namespace(prefix = "samlp", reference = "urn:oasis:names:tc:SAML:2.0:protocol")
    AuthRequest authRequest;

    public AuthRequest getAuthRequest() {
        return authRequest;
    }

    public void setAuthRequest(AuthRequest authRequest) {
        this.authRequest = authRequest;
    }
}