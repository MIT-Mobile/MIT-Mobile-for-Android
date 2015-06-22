package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root
@Namespace(prefix = "xenc")
public class CipherValue {

    @Text
    String value;
}
