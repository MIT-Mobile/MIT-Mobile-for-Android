package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Namespace(prefix = "soap11", reference = "http://schemas.xmlsoap.org/soap/envelope/")
@Root(name = "Header")
public class TouchstoneHeader {

    @Element(name = "Response", required = false)
    @Namespace(prefix = "ecp", reference = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp")
    EcpResponse response;

    @Element(name = "RelayState", required = false)
    @Namespace(prefix = "ecp", reference = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp")
    RelayState relayState;

    public EcpResponse getResponse() {
        return response;
    }

    public void setResponse(EcpResponse response) {
        this.response = response;
    }

    public RelayState getRelayState() {
        return relayState;
    }

    public void setRelayState(RelayState relayState) {
        this.relayState = relayState;
    }
}
