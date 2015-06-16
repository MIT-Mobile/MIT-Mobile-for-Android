package edu.mit.mitmobile2.libraries.model.xml.touchstone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;

public class EncryptionMethod {

    @Attribute(name = "Algorithm")
    private String algorithm;

    @Element(name = "DigestMethod", required = false)
    @Namespace(prefix = "ds")
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
