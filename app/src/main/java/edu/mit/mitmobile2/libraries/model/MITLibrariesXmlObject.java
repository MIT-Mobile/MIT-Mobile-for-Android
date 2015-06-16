package edu.mit.mitmobile2.libraries.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import edu.mit.mitmobile2.libraries.model.xml.user.XmlBody;
import edu.mit.mitmobile2.libraries.model.xml.user.XmlHeader;

@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/")
@Root(name = "Envelope")
public class MITLibrariesXmlObject {

    @Element(name = "Header", required = false)
    XmlHeader header;

    @Element(name = "Body")
    XmlBody body;

    public XmlHeader getHeader() {
        return header;
    }

    public void setHeader(XmlHeader header) {
        this.header = header;
    }

    public XmlBody getBody() {
        return body;
    }

    public void setBody(XmlBody body) {
        this.body = body;
    }

}
