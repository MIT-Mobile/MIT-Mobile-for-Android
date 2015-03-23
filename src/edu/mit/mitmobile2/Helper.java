package edu.mit.mitmobile2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;

// TODO make into interface and import Activity

public class Helper extends Activity{

	void sendEmail(String title, String text) {

		String[] addr = {""};

		Intent i = new Intent(Intent.ACTION_SEND);

		i.putExtra(Intent.EXTRA_SUBJECT, title);

		//i.putExtra(Intent.EXTRA_TEXT, text);
		i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(text));


		i.putExtra(Intent.EXTRA_EMAIL, addr);

		//i.setType("message/rfc822");
		//i.setType("text/plain");
		i.setType("*/*");
		//i.setType("text/html");

		startActivity(Intent.createChooser(i, "Email:"));

	}
	/**************************************************/
	void makeCall() {

		String number = "";

		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
		startActivity(intent);

	}

	/**************************************************/
	/*
	 private InputStream OpenHttpConnection(String urlString) 
	    throws IOException
	    {
	        InputStream in = null;
	        int response = -1;
	               
	        URL url = new URL(urlString); 
	        URLConnection conn = url.openConnection();
	                 
	        if (!(conn instanceof HttpURLConnection))                     
	            throw new IOException("Not an HTTP connection");
	        
	        try{
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            httpConn.setAllowUserInteraction(false);
	            httpConn.setInstanceFollowRedirects(true);
	            httpConn.setRequestMethod("GET");
	            httpConn.connect(); 

	            response = httpConn.getResponseCode();                 
	            if (response == HttpURLConnection.HTTP_OK) {
	                in = httpConn.getInputStream();                                 
	            }                     
	        }
	        catch (Exception ex)
	        {
	            throw new IOException("Error connecting");            
	        }
	        return in;     
	    }
	*/
}
