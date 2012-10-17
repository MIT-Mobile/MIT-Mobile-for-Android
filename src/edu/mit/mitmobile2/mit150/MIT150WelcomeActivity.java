package edu.mit.mitmobile2.mit150;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.MobileWebApi.JSONObjectResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.about.BuildSettings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class MIT150WelcomeActivity extends Activity {

	public static final String CONTENT = "content";

	static final int MENU_LAUNCH = Menu.FIRST + 1;

    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    
    Context ctx;
    
    String hockvideo;
    
	String content;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		ctx = this;

    	//Bundle extras = getIntent().getExtras();
        //if (extras!=null){ 
        //	content = extras.getString(CONTENT);
        //}
		
		
		setContentView(R.layout.mit150_welcome);

		final WebView wv = (WebView) findViewById(R.id.welcomeWV);
		
		
		ImageView iv = (ImageView) findViewById(R.id.welcomeIV);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx,MIT150VideoActivity.class);
				startActivity(i);
				//playVideo();
			}
		});
	

		// Scale Welcome image...
		
		Display display = getWindowManager().getDefaultDisplay(); 
	    int screenWidth = display.getWidth();
	    
	    float scale = screenWidth / 320.0f;
	    int h = (int) (scale * 180.0f);
	    
	    LayoutParams params = new LayoutParams(screenWidth,h);
		iv.setLayoutParams(params);
		iv.requestLayout();

		final Handler uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.arg1 == MobileWebApi.SUCCESS) {
					if (content!=null)
						wv.loadDataWithBaseURL(null, StyledContentHTML.html(ctx, content), mimeType, encoding, null);	
					
					wv.requestLayout();
					wv.invalidate();
				} 
			}
		};
		fetchMIT150WelcomeContent(this,uiHandler);
		
	}
	/****************************************************/
	void playVideo() {
	
		
		String subpath = "/Android/data/" + BuildSettings.release_project_name + "/cache/";  

    	String state = Environment.getExternalStorageState();

    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    		
        	String root = Environment.getExternalStorageDirectory().toString() + subpath;
        	
        	// Cache exists?
	    	File file = new File(root, "");
	    	if (!file.exists()) {
		    	boolean success = file.mkdirs();  // makes whole path
		    	if (!success) {
		    		Log.e("Audio","Audio: mkdir failed");
		    		return;
		    	}
	    	}
			
	    	hockvideo = root + "hockfield_150.mp4";

	    	// FIXME
	    	//FileOutputStream fos = ctx.openFileOutput(hockvideo, Context.MODE_WORLD_READABLE);
	     	
	    	
	    	File newfile = new File(hockvideo, "");
	    	if (!newfile.exists()) {
	    		
	    		Resources res = ctx.getResources();
		    	//InputStream is = res.openRawResource(R.raw.hockfield_150); 
	    		InputStream is = res.openRawResource(-1); 
		    	FileOutputStream os;
		    	
		    	byte[] buffer = new byte[4096];  
		    	int bytesRead;
		    	try {  
		    		os = new FileOutputStream(newfile);
		    		while ((bytesRead = is.read(buffer)) != -1) {  
		    			os.write(buffer, 0, bytesRead);
		    		}  
		    		is.close();  
		    		os.close(); 
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    		return;
		    	}  
		    	
	    	}
	    	
	    	// Open external video player
	    	Intent i = new Intent(Intent.ACTION_VIEW ); 
	    	i.setDataAndType(Uri.fromFile(newfile), "video/*");  
			startActivity(i);
	    	
       	} 
		
	}
	/****************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case MENU_LAUNCH: 
			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		//menu.add(0, MENU_LAUNCH, Menu.NONE, "Launch");
		return true;
	}
	/********************************************************************/
	public void fetchMIT150WelcomeContent(final Context context, final Handler uiHandler) {	
		
		MobileWebApi webApi = new MobileWebApi(false, true, "MIT150", context, uiHandler);
		
		HashMap<String, String> query = new HashMap<String, String>();
		query.put("module", "anniversary");
		query.put("command", "welcome");
		
		webApi.requestJSONObject(query, new JSONObjectResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), null) {
			@Override
			public void onResponse(JSONObject object) throws ServerResponseException, JSONException {
				content  = object.getString("content");
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});
	}

}
