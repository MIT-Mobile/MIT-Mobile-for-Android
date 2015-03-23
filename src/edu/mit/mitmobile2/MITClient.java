package edu.mit.mitmobile2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
//import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import edu.mit.mitmobile2.touchstone.TouchstoneActivity;

public class MITClient extends DefaultHttpClient {

	private static final String TAG = "MITClient";
	public static final String PREFS_STATE = "prefs";
	public static final String OK_STATE = "ok";
	public static final String ECP_STATE = "ecp";
	public static final String IDP_STATE = "idp";
	public static final String AUTH_STATE = "auth";
	public static final String ERROR_STATE = "error";	
	public static final String AUTH_ERROR_STATE = "auth_error";
	public static final String CANCELLED_STATE = "cancelled";	
	public static final String AUTH_ERROR_KERBEROS = "Error: Please enter a valid username and password"; // error message from invalid kerberos login
	public static final String AUTH_ERROR_CAMS = "Error: Enter your email address and password"; // error message from invaid cams login
	
	public static SharedPreferences prefs;
	public static final String TOUCHSTONE_REQUEST = "TOUCHSTONE_REQUEST";
	public static final String TOUCHSTONE_LOGIN = "TOUCHSTONE_LOGIN";    
	public static final String TOUCHSTONE_CANCEL = "TOUCHSTONE_CANCEL";    
	public static final String TOUCHSTONE_DIALOG = "TOUCHSTONE_DIALOG";    
	
	private static final String PAOS_MIME_TYPE = "text/html; application/vnd.paos+xml";
	private static final String PAOS_HEADER = "PAOS";
	private static final String PAOS_HEADER_VALUE = "ver=\"urn:liberty:paos:2003-08\";\"urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp\"";
	private static final String PAOS_CONTENT_TYPE = "application/vnd.paos+xml";
	private static final String MITIDP = "https://idp.mit.edu/idp/profile/SAML2/SOAP/ECP";
	private static final String CAMSIDP = "https://idp.touchstonenetwork.net/idp/profile/SAML2/SOAP/ECP";
	final SharedPreferences.Editor prefsEditor;

	HttpGet mHttpGet;
	String requestKey;
	URI targetUri;
	String ecpTarget;
	
	// Hashmap for keeping track of the status of requests made by the HttpClient 
	// because this is not an activity, there is no context for startActivityForResult or UI handlers
	public static Map<String, MITClientData> requestMap = new HashMap<String, MITClientData>();
	
	// Cookies
	//public static List<Cookie> cookies = new ArrayList();
	public static CookieStore cookieStore;
	
	protected Context mContext;
	
	private static String user;
	private static String password;
	
	boolean rememberLogin;
	URI uri;
	//URI targetUri;
	String uriString;
	HttpGet get;
	HttpResponse response;
	HttpEntity responseEntity;
	HttpPost post;
	WebView webview;
	Document document;
	String responseString = "";
	String state;
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@SuppressWarnings("static-access")
	public MITClient(Context context) {
		super();		
		Log.d(TAG,"MITClient()");
		this.mContext = context;
		
		//Log.d(TAG,"MITClient.cookieStore = " + MITClient.cookieStore);
		if (MITClient.cookieStore == null) {
			MITClient.cookieStore = this.getCookieStore();
		}

		this.setCookieStore(this.cookieStore);
		
		// get user name and password from preferences file
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		prefsEditor = prefs.edit();
		MITClient.user = prefs.getString("PREF_TOUCHSTONE_USERNAME", null);
		MITClient.password = prefs.getString("PREF_TOUCHSTONE_PASSWORD", null);
		rememberLogin = prefs.getBoolean("PREF_TOUCHSTONE_REMEMBER_LOGIN", false);
		Log.d(TAG,"user = " + user);
				
		this.setRedirectHandler(new DefaultRedirectHandler() {
			String host;
			@Override
			public URI getLocationURI(HttpResponse response, HttpContext context) {
				//Log.d(TAG,"redirectHandler");
								
				Header[] locations = response.getHeaders("Location");
				
				if (locations.length > 0) {
					Header location = locations[0];
					String uriString = location.getValue();
					//Log.d(TAG,"uriString from redirect = " + uriString);
                    /*
                    if (state == null && state != OK_STATE) {
                        if (getEcpTarget() == null || !getEcpTarget().equalsIgnoreCase(uriString)) {
                            setEcpTarget(uriString);
                        }
                    }
                    */
					try {
						uri = new URI(uriString);
						host = uri.getHost();
                        /*
						if (host.equalsIgnoreCase("wayf.mit.edu")) {
							state = ECP_STATE;
							Log.d(TAG,"state = " + state);
						}
						else {
							state = OK_STATE;
						}
						*/

					} catch (URISyntaxException use) {
						Log.e(TAG, "Invalid Location URI: "+uriString);
					}

				}
				return uri;
			}
		});

	}

	public static String responseContentToString(HttpResponse response) {
		try {
		InputStream inputStream = response.getEntity().getContent();
		ByteArrayOutputStream content = new ByteArrayOutputStream();
		// Read response into a buffered stream
		int readBytes = 0;
		byte[] sBuffer = new byte[512];
		while ((readBytes = inputStream.read(sBuffer)) != -1) {
			content.write(sBuffer, 0, readBytes);
		}
	
		// Return result from buffered stream
		String dataAsString = new String(content.toByteArray());
		return dataAsString;
		}
		catch (IOException e) {
			return null;
		}
	}

	
	public HttpResponse getResponse(HttpGet httpGet) {
		try {
			this.mHttpGet = httpGet;
			this.targetUri = httpGet.getURI();
			MITClientData clientData = new MITClientData();
			clientData.setTargetUri(this.targetUri);
			requestKey = System.currentTimeMillis()/1000 + "";
			Log.d(TAG,"requestKey " + requestKey + " created");
			requestMap.put(requestKey, clientData);

            httpGet.addHeader("accept", PAOS_MIME_TYPE);
            httpGet.addHeader(PAOS_HEADER, PAOS_HEADER_VALUE);
            //httpGet.addHeader("Content-Type", PAOS_CONTENT_TYPE);

			response = this.execute(httpGet);
			responseEntity = response.getEntity();

            Header contentType = response.getFirstHeader("Content-Type");
            String mimeType = contentType.getValue();
            if (response.getStatusLine().getStatusCode() == 200 && mimeType.equalsIgnoreCase(PAOS_CONTENT_TYPE)) {
            	setEcpTarget(uri.toString());
                state = ECP_STATE;
            } else if (response.getStatusLine().getStatusCode() == 200 && !mimeType.equalsIgnoreCase(PAOS_CONTENT_TYPE)) {
                state = OK_STATE;
            } else {
                	Log.d(TAG, "sp response statu = " + response.getStatusLine().getStatusCode());
            }

                if (state == OK_STATE) {
                    saveLogin();
                    return response;
                }
                if (state == ECP_STATE) {
                    Log.d(TAG, "ecp state");
                    ecp();
                }
                if (state == AUTH_ERROR_STATE) {
                    Log.d(TAG, "auth error state");
                    authError();
                }
                if (state == CANCELLED_STATE) {
                    Log.d(TAG, "status in cancelled state = " + response.getStatusLine().getStatusCode());
                    MITHttpEntity entity = new MITHttpEntity();
                    entity.setContent(MITHttpEntity.JSON_CANCEL);
                    response.setStatusCode(200);
                    response.setEntity(entity);
                    return response;
                }
                if (state == OK_STATE) {
                    saveLogin();
                    return response;
                }
			return null;
		} catch (IOException e) {
			Log.d(TAG,"get response exception = " + e.getMessage());
			return null;
		}
	}

	private void ecp() {
		
		// Launch preferences activity if user or password are not set
		if (MITClient.user == null || MITClient.user.length() == 0 || MITClient.password == null || MITClient.password.length() == 0) {
			//requestKey = System.currentTimeMillis()/1000 + "";
			Log.d(TAG,"requestKey = " + requestKey);
			((MITClientData)requestMap.get(requestKey)).setTouchstoneState(TOUCHSTONE_REQUEST);
			Intent touchstoneIntent = new Intent(mContext, TouchstoneActivity.class);
			touchstoneIntent.putExtra("requestKey",requestKey);
			((Activity) mContext).startActivity(touchstoneIntent);
		
			Log.d(TAG,"requestKey " + requestKey + " value = " + MITClient.requestMap.get(requestKey));
            // do stuff,  don't burn the CPU
            while( 	((MITClientData)MITClient.requestMap.get(requestKey)).getTouchstoneState().equalsIgnoreCase(TOUCHSTONE_REQUEST) )
                try {
                    Log.d(TAG, "requestMap " + requestKey + " = " + requestMap.get(requestKey));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
					
		}
		Log.d(TAG,"request key in ecp = " + requestKey);
		MITClientData clientData = (MITClientData)MITClient.requestMap.get(requestKey);
		Log.d(TAG,"touchstone state = " + clientData.getTouchstoneState());
		if ( clientData.getTouchstoneState() == null || clientData.getTouchstoneState().equalsIgnoreCase(TOUCHSTONE_LOGIN) ) {
			//ECP profile, paos httpget to sp
			HttpGet paosGet = new HttpGet();
			try {
				uri = new URI(getEcpTarget());
                
			} catch (URISyntaxException e) {
				Log.d(TAG, " paos sp httpget uri exception = " + e.getMessage());
			}
			paosGet.setURI(uri);
			paosGet.addHeader("accept", PAOS_MIME_TYPE);
			paosGet.addHeader(PAOS_HEADER, PAOS_HEADER_VALUE);
			paosGet.addHeader("Content-Type", PAOS_CONTENT_TYPE);
			
			try {
				response = this.execute(paosGet); 
			} catch (IOException ioe) {
				Log.d(TAG, getEcpTarget() + "paos sp httpget IO exception = "+ ioe.getMessage());
			}

			if (response.getStatusLine().getStatusCode() != 200) { 
				Log.d(TAG, "ECP PAOS reponse from sp, request failed! status = " + response.getStatusLine().getStatusCode());
                //state = ERROR_STATE;
			}

			HttpEntity paosEntity = response.getEntity();
			String xmlString = null;
			try {
				xmlString = EntityUtils.toString(paosEntity);
			} catch (IOException ioe) {
				Log.d(TAG,
						"paos sp httpget response, converting to string failed, IO exception =" + ioe.getMessage());
			}
			//Log.d(TAG, "paos sp httpget response  =" + xmlString);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException pce) {
				Log.d(TAG,
                        " paos sp httpget response, creating dom parser,  exception = " + pce.getMessage());
			}
			Document doc = null;
			try {
				doc = builder.parse(new InputSource(new ByteArrayInputStream(
						xmlString.getBytes("utf-8"))));
			} catch (SAXException se) {
				Log.d(TAG, "paos sp httpget response, create DOM document, SAXException = " + se.getMessage());
			} catch (IOException ioe) {
				Log.d(TAG, "paos sp httpget response, create DOM document, IOException = " + ioe.getMessage());
			}
			Element spRespnseRoot = doc.getDocumentElement();
			String spSoapPrefix = spRespnseRoot.getNodeName();
			if (spSoapPrefix != null && spSoapPrefix.contains(":")) {
				spSoapPrefix = spSoapPrefix.substring(0, spSoapPrefix.indexOf(":"));
			}
			//error checking in paos soap message
			String spSoapFault = spSoapPrefix + ":Fault";
			NodeList faultList = null;
			faultList = doc.getElementsByTagName(spSoapFault);
			if (faultList != null && faultList.getLength() > 0) {
				Log.d(TAG, " sp paos response error, SOAP FAULT in soap message");
                //state = ERROR_STATE;
			}
			// relayState
			NodeList rnds = doc.getElementsByTagName("ecp:RelayState");
			Node relayStateNode = rnds.item(0);		
			//Node newRelayStateNode = relayStateNode.cloneNode(true);
			//Node newRelayStateNode = doc.importNode(relayStateNode, true);
			//use doc.adoptNode, both cloneNode() and importNode() don't work for android 2.3.4
			Node newRelayStateNode = doc.adoptNode(relayStateNode);		
			Log.d(TAG, "relay_state = " + relayStateNode.getTextContent());

			// responseConsumerURL
			NodeList cnds = doc.getElementsByTagName("paos:Request");
			//Node responseConsumerURLNode = cnds.item(0);
			Element ce = (Element) cnds.item(0);
			String responseConsumerURL = ce.getAttribute("responseConsumerURL");
			Log.d(TAG, " getting - responseConsumerURL " + responseConsumerURL);

			// remove S:Header node
			Element element = (Element) doc.getElementsByTagName(spSoapPrefix + ":Header").item(0);
			element.getParentNode().removeChild(element);
			doc.normalize();

			// Document To Byte Array
			DOMSource source = new DOMSource(doc);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ElementToStream(doc.getDocumentElement(), out);

			Log.d(TAG, "user = " + user);
			String tmpUser = user.toUpperCase();
			String user_idp = "";
			if (tmpUser.contains("@") && !tmpUser.contains("@MIT.EDU")) {
				user_idp = CAMSIDP;
			} else {
				user_idp = MITIDP;
				// remove "@MIT.EDU from user name
				if (tmpUser.contains("@MIT.EDU")) {
					user = user.substring(0, user.length() - 8);
					Log.d(TAG, "user = " + user);
				}
			}
			Log.d(TAG, "user_idp = " + user_idp);
			HttpPost paosPost = new HttpPost(user_idp);
			CredentialsProvider provider = new BasicCredentialsProvider();
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, password);
			provider.setCredentials(AuthScope.ANY, creds);
			this.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
			ByteArrayEntity be = new ByteArrayEntity(out.toByteArray());
			paosPost.setEntity(be);
			paosPost.setHeader("Content-Type", PAOS_CONTENT_TYPE);
			try {
				response = this.execute(paosPost); // post to idp			
			} catch (ClientProtocolException cpe) {
				Log.d(TAG, " paos idp post client protocol exception = " + cpe.getMessage());
			} catch (IOException ioe) {
				Log.d(TAG, " paos idp post io exception = " + ioe.getMessage());
			}
			
			paosEntity = response.getEntity();
			try {
				xmlString = EntityUtils.toString(paosEntity);
			} catch (IOException ioe) {
				Log.d(TAG, "paos response from idp, ioexception = " + ioe.getMessage());
			}
			//Log.d(TAG, "paos httpost response from idp =" + xmlString);
			if (response.getStatusLine().getStatusCode() != 200) {
                if (response.getStatusLine().getStatusCode() == 401) {
                    Log.d(TAG, "paos httpost response from idp status code  =" + response.getStatusLine().getStatusCode());
                    state = AUTH_ERROR_STATE;
                    return;
                } else {
                    Log.d(TAG, "paos httpost response from idp != 200, status code  =" + response.getStatusLine().getStatusCode());
                }
			} else {
				try {
					doc = builder.parse(new InputSource(new ByteArrayInputStream(
						xmlString.getBytes("utf-8"))));
				} catch (SAXException se) {
					Log.d(TAG, " idp paos post response, create DOM document, SAXException =  "+se.getMessage());
				} catch (IOException ioe) {
					Log.d(TAG, " idp paos post response, create DOM document, IOException" + ioe.getMessage());
				}
				//get prefix and do error checking paos soap from idp
				Element idpRespnseRoot = doc.getDocumentElement();
				String idpSoapPrefix = idpRespnseRoot.getNodeName();
				if (idpSoapPrefix != null && idpSoapPrefix.contains(":")) {
					idpSoapPrefix = idpSoapPrefix.substring(0, idpSoapPrefix.indexOf(":"));
				}
				
				String idpSoapFault = idpSoapPrefix + ":Fault";
				faultList = doc.getElementsByTagName(idpSoapFault);
				if (faultList.getLength() != 0) {
					Log.d(TAG, " idp paos response error, SOAP FAULT  in soap message");
				}
				NodeList ands = doc.getElementsByTagName("ecp:Response");
				//Node assertionConsumerServiceURLNode = ands.item(0);
				Element ae = (Element) ands.item(0);
				String assertionConsumerServiceURL = ae.getAttribute("AssertionConsumerServiceURL");
				//check if assertionConsumerServiceURL ==  responseConsumerURL
				if (!responseConsumerURL.equalsIgnoreCase(assertionConsumerServiceURL)) {
					Log.d(TAG,
						"Error!  responseConsumerURL from sp != assertionConsumerServiceURL from idp");
					//state = AUTH_ERROR_STATE;
					return;
				}
				// remove all child nodes from headerNode.
				Node headerNode = doc.getElementsByTagName(idpSoapPrefix + ":Header").item(0);
				NodeList childNodes = headerNode.getChildNodes();
				int length = childNodes.getLength();
				for (int i = 0; i < length; i++) {
					Node child = childNodes.item(i);
					headerNode.removeChild(child);
				}
				doc.normalize();
				// insert relayState
				Element ecpnd = doc.createElement("ecp:RelayState");
				NamedNodeMap attributes = newRelayStateNode.getAttributes();
				int numAttrs = attributes.getLength();
				for (int i = 0; i < numAttrs; i++) {
					Attr attr = (Attr) attributes.item(i);
					String attrName = attr.getNodeName();
					String attrValue = attr.getNodeValue();

					if (attrName.contains(spSoapPrefix + ":")) {
						String newAttrName = attrName.replace(spSoapPrefix + ":",
							idpSoapPrefix + ":");
						ecpnd.setAttribute(newAttrName, attrValue);
						Log.d(TAG, " newRelayStateNode attribute name = " + newAttrName);
					} else {
						ecpnd.setAttribute(attrName, attrValue);
					}
					Log.d(TAG, "attr = " + attr.toString());
				}
				ecpnd.setTextContent(newRelayStateNode.getTextContent());
				headerNode.appendChild(ecpnd);
				newRelayStateNode.normalize();
				doc.normalize();
				source = new DOMSource(doc);
				out = new ByteArrayOutputStream();
				ElementToStream(doc.getDocumentElement(), out);
				// post to SP
				String spuri = responseConsumerURL;
				paosPost = new HttpPost(spuri);
				provider = new BasicCredentialsProvider();
				creds = new UsernamePasswordCredentials(user, password);
				provider.setCredentials(AuthScope.ANY, creds);
				this.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
				be = new ByteArrayEntity(out.toByteArray());
				paosPost.setEntity(be);
				paosPost.setHeader("Content-Type", PAOS_CONTENT_TYPE);
			
				try {
					response = this.execute(paosPost); // assertionComsumerURL, post to sp
				} catch (ClientProtocolException cpe) {
					Log.d(TAG, " paos sp post client protocol exception = " + cpe.getMessage());
				} catch (IOException ioe) {
					Log.d(TAG, " paos sp post io exception = " + ioe.getMessage());
				}
                if (response.getStatusLine().getStatusCode() != 200 ) {
                    Log.d(TAG, "paos httpost response error from sp, status code  = " + response.getStatusLine().getStatusCode());
                    //state = ERROR_STATE;
                } else {
                    Header contentType = response.getFirstHeader("Content-Type");
                    String mimeType = contentType.getValue();
                    if (mimeType.equalsIgnoreCase(PAOS_CONTENT_TYPE)) {
                        Log.d(TAG, "paos httpost response error from sp, MIME type = " + mimeType);
                    }
					//clearing pref
					state = OK_STATE;
					if (!rememberLogin) {
						prefsEditor.putString("PREF_TOUCHSTONE_USERNAME", null);
						prefsEditor.putString("PREF_TOUCHSTONE_PASSWORD", null);
						prefsEditor.putBoolean("PREF_TOUCHSTONE_REMEMBER_LOGIN", false);
					}
				}
			}
		} else {
			state = CANCELLED_STATE;
		}
	}
		
	public void debugCookies() {
		Log.d(TAG,"debugCookies()");
		Log.d(TAG,"cookieStore = " + this.getCookieStore());
		List<Cookie> cookies = this.getCookieStore().getCookies();
		Iterator<Cookie> c = cookies.iterator();
		while (c.hasNext()) {
			Cookie cookie = c.next();
			Log.d(TAG,"cookie domain = " + cookie.getDomain() + " name = " + cookie.getName() + " value = " + cookie.getValue() + " expires = " + cookie.getExpiryDate());
		}
	}

	class MITRequestInterceptor implements HttpRequestInterceptor {

		@Override
		public void process(HttpRequest arg0, HttpContext arg1)
				throws HttpException, IOException {
			// TODO Auto-generated method stub
			Log.d(TAG,"request intercept");
			Log.d(TAG,arg0.getRequestLine().getUri());
		}
		
	}
	
	class MITInterceptor implements HttpResponseInterceptor {
		
		public MITInterceptor() {
			super();
			Log.d(TAG,"intercept");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
			// TODO Auto-generated method stub
			Header[] headers = response.getHeaders("Set-Cookie");
			for (int h = 0; h < headers.length; h++) {
				Header header = (Header)headers[h];
				HeaderElement[] headerElements = header.getElements();
				for (int e = 0; e < headerElements.length; e++) {
					HeaderElement headerElement = headerElements[e];
					Log.d(TAG,"Header Element " + e);
					Log.d(TAG,"name = " + headerElement.getName());
					Log.d(TAG,"value = " + headerElement.getValue());
					NameValuePair[] parameters = headerElement.getParameters();
					for (int p = 0; p < parameters.length; p++) {
						NameValuePair parameter = parameters[p];
						Log.d(TAG,"parameter " + p + " " + parameter.getName() + " = " + parameter.getValue());
					}
				}
			}
		}
	}
	
	public static void clearCookies() {
		cookieStore = null;
	}

	private void authError() {
		Log.d(TAG,"authError()");

		MITClientData clientData = (MITClientData)MITClient.requestMap.get(requestKey);
		clientData.setTouchstoneState(TOUCHSTONE_REQUEST);
		Intent touchstoneIntent = new Intent(mContext, TouchstoneActivity.class);
		touchstoneIntent.putExtra("requestKey",requestKey);
		touchstoneIntent.putExtra("touchstoneState",AUTH_ERROR_STATE);

		((Activity) mContext).startActivity(touchstoneIntent);
	
		Log.d(TAG,"Sending auth error state to touchstone" + requestKey);
		//while (MITClient.requestMap.get(requestKey) == TOUCHSTONE_REQUEST) {
		while (	clientData.getTouchstoneState().equalsIgnoreCase(TOUCHSTONE_REQUEST)) {
			// do stuff
		    // don't burn the CPU
		    try {
		    	Log.d(TAG,"requestMap " + requestKey + " = " + requestMap.get(requestKey));
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// If the touchstone request was not cancelled, retry the login
		if (!clientData.getTouchstoneState().equals(TOUCHSTONE_CANCEL)) {
	        // retry login
			response = getResponse(new HttpGet(targetUri));
		}
		else {
			state = CANCELLED_STATE;
		}
		
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		MITClient.user = user;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		MITClient.password = password;
	}
	
	public String getEcpTarget() {
		return ecpTarget;
	}

	public void setEcpTarget(String ecpTarget) {
		this.ecpTarget = ecpTarget;
	}

	public void saveLogin() {
		rememberLogin = prefs.getBoolean("PREF_TOUCHSTONE_REMEMBER_LOGIN", false);
		if (rememberLogin) {
			prefsEditor.putString("PREF_TOUCHSTONE_USERNAME", MITClient.getUser());
			prefsEditor.putString("PREF_TOUCHSTONE_PASSWORD", MITClient.getPassword());
			prefsEditor.commit();
		}
	}
	
	public static void ElementToStream(Element element, OutputStream out) {
		try {
			DOMSource source = new DOMSource(element);
			StreamResult result = new StreamResult(out);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			transformer.transform(source, result);
		} catch (Exception ex) {
			Log.d(TAG, "XML element to stream error = " + ex.getMessage());
		}
	}
}
