package edu.mit.mitmobile2;

import org.apache.http.Header;
import org.apache.http.impl.cookie.BasicClientCookie;

public class MITCookie extends BasicClientCookie {

	public MITCookie(String name, String value) {
		super(name, value);
		// TODO Auto-generated constructor stub
	}

	public void getCookieFromHeader(String domain,Header header) {
		this.setDomain(domain);
		
	}
}