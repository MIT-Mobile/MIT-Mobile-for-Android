package edu.mit.mitmobile2.dining.model;

public class MITDiningLinks {
	String name;
	String url;
	MITDiningDining dining;

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public MITDiningDining getDining() {
		return dining;
	}

	@Override
	public String toString() {
		return "MITDiningLinks{" +
			"name='" + name + '\'' +
			", url='" + url + '\'' +
			", dining=" + dining +
			'}';
	}
}