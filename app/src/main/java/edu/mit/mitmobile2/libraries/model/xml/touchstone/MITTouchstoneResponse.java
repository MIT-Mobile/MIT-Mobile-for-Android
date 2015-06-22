package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap11")
@Root(name = "Envelope")
public class MITTouchstoneResponse {

    @Element(name = "Header")
    @Namespace(prefix = "soap11", reference = "http://schemas.xmlsoap.org/soap/header/")
    TouchstoneHeader header;

    @Element(name = "Body")
    @Namespace(prefix = "soap11", reference = "http://schemas.xmlsoap.org/soap/body/")
    TouchstoneBody touchstoneBody;

    public TouchstoneHeader getHeader() {
        return header;
    }

    public void setHeader(TouchstoneHeader header) {
        this.header = header;
    }

}
