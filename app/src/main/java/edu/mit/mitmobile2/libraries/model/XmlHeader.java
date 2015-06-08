package edu.mit.mitmobile2.libraries.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@NamespaceList(
        {
                @Namespace(prefix = "paos", reference = "urn:liberty:paos:2003-08"),
                @Namespace(prefix = "ecp", reference = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp")
        }
)
@Root(name = "Header")
public class XmlHeader {

    @ElementList(inline = true, entry = "Request")
    List<XmlRequest> Request;

    @Namespace(reference = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp")
    @Element(name = "RelayState")
    @Text
    RelayState relayState;

    public RelayState getRelayState() {
        return relayState;
    }

    public void setRelayState(RelayState relayState) {
        this.relayState = relayState;
    }

    public List<XmlRequest> getRequest() {
        return Request;
    }

    public void setRequest(List<XmlRequest> request) {
        Request = request;
    }
}
