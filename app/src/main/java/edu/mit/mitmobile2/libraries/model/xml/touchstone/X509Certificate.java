package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root
@Namespace(prefix = "ds")
public class X509Certificate {

    @Text
    String value;
}
