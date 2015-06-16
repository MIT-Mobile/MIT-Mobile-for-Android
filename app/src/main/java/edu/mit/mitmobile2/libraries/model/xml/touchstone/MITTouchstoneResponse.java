package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class MITTouchstoneResponse {

    @Element(name = "Header")
    @Namespace(prefix = "soap11")
    TouchstoneHeader header;

    @Element(name = "Body")
    @Namespace(prefix = "soap11")
    TouchstoneBody touchstoneBody;

    public TouchstoneHeader getHeader() {
        return header;
    }

    public void setHeader(TouchstoneHeader header) {
        this.header = header;
    }

}
