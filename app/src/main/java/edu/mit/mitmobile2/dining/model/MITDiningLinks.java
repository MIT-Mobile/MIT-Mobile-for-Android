package edu.mit.mitmobile2.dining.model;

import java.io.Serializable;

public class MITDiningLinks implements Serializable {
    protected String name;
    protected String url;
//    protected MITDiningDining dining;

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

//	public MITDiningDining getDining() {
//		return dining;
//	}

	@Override
	public String toString() {
		return "MITDiningLinks{" +
			"name='" + name + '\'' +
			", url='" + url + '\'' +
//			", dining=" + dining +
			'}';
	}
}