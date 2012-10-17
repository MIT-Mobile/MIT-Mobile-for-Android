package edu.mit.mitmobile2;

import java.net.URI;

public class MITClientData {

	private URI targetUri;
	private String touchstoneState;
	
	public String getTouchstoneState() {
		return touchstoneState;
	}
	public void setTouchstoneState(String touchstoneState) {
		this.touchstoneState = touchstoneState;
	}
	public URI getTargetUri() {
		return targetUri;
	}
	public void setTargetUri(URI targetUri) {
		this.targetUri = targetUri;
	}

	
}
