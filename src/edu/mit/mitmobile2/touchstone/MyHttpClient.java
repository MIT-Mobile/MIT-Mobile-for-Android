package edu.mit.mitmobile2.touchstone;

import edu.mit.mitmobile2.R;
import android.content.Context;
import android.util.Log;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.InputStream;
import java.security.Certificate;
import java.security.KeyStore;
import java.security.PublicKey;

public class MyHttpClient extends DefaultHttpClient {

	final Context context;
	public static final String TAG = "MyHttpClient";

	public MyHttpClient(Context context) {
		this.context = context;
	}

	@Override protected ClientConnectionManager createClientConnectionManager() {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", newSslSocketFactory(), 443));
		return new SingleClientConnManager(getParams(), registry);
	}

	private SSLSocketFactory newSslSocketFactory() {
		try {
			KeyStore trusted = KeyStore.getInstance("BKS");
			InputStream in = context.getResources().openRawResource(R.raw.trusted_key_store);

			try {
				trusted.load(in, "testpass".toCharArray());
			} 
			catch (Exception e) {
				Log.d(TAG,e.getMessage());
			}
			finally {
				in.close();
			}
			//DEBUG
			java.security.cert.Certificate cert = trusted.getCertificate("ca");
			byte[] encoded = cert.getEncoded();
			Log.d(TAG,"encoded = " + encoded);
			PublicKey publicKey = cert.getPublicKey();
			Log.d(TAG,"public key = " + publicKey.getEncoded());
			//DEBUG
			return new SSLSocketFactory(trusted);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
}