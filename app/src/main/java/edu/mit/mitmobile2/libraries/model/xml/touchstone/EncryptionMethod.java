package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
@Namespace(prefix = "xenc")
public class EncryptionMethod {

    @Attribute(name = "Algorithm")
    private String algorithm;

    @Element(name = "DigestMethod", required = false)
    @Namespace(prefix = "ds", reference = "http://www.w3.org/2000/09/xmldsig#")
    DigestMethod digestMethod;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String Algorithm) {
        this.algorithm = Algorithm;
    }

    public DigestMethod getDigestMethod() {
        return digestMethod;
    }

    public void setDigestMethod(DigestMethod digestMethod) {
        this.digestMethod = digestMethod;
    }

    @Override
    public String toString() {
        return "ClassPojo [algorithm = " + algorithm + "]";
    }
}
