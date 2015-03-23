package edu.mit.mitmobile2;
import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import android.util.Log;


public class MITCookieStore implements CookieStore{

	private List<Cookie> cookies = null;
	
	@Override
	public void addCookie(Cookie arg0) {
		// TODO Auto-generated method stub
		Log.d("MITCookieStore","addCookie()");
		cookies.add(arg0);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		Log.d("MITCookieStore","clear()");
	}

	@Override
	public boolean clearExpired(Date arg0) {
		// TODO Auto-generated method stub
		Log.d("MITCookieStore","clearExpired()");
		return false;
	}

	@Override
	public List<Cookie> getCookies() {
		// TODO Auto-generated method stub
		return this.cookies;
	}

}
