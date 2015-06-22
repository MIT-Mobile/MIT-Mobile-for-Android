package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Namespace(prefix = "soap11", reference = "http://schemas.xmlsoap.org/soap/body/")
@Root(name = "Body")
public class TouchstoneBody {

    @Element(name = "Response")
    @Namespace(prefix = "saml2p", reference = "urn:oasis:names:tc:SAML:2.0:protocol")
    Saml2pResponse response;

    public Saml2pResponse getResponse() {
        return response;
    }

    public void setResponse(Saml2pResponse response) {
        this.response = response;
    }
}
