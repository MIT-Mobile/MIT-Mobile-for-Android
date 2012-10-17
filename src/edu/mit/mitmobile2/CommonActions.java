package edu.mit.mitmobile2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.events.EventsModule;
import edu.mit.mitmobile2.links.LinksModule;
import edu.mit.mitmobile2.maps.MITMapActivity;
import edu.mit.mitmobile2.maps.MapsModule;
import edu.mit.mitmobile2.mit150.MIT150Module;
import edu.mit.mitmobile2.qrreader.QRReaderModule;

public class CommonActions {

	public static void viewURL(Context context, String url) {
		Intent viewURLIntent = new Intent(Intent.ACTION_VIEW);
		viewURLIntent.setData(Uri.parse(url));
		context.startActivity(viewURLIntent);
	}
	/*
	public static void shareContent(Context context, String subject, String summary, String url) {
		String extraText = summary;
		if(extraText.length() > 100) {
			extraText = extraText.substring(0, 99);
			extraText = extraText.trim() + "...";
		}
		
		extraText += " " + url;
		
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, extraText);
		context.startActivity(Intent.createChooser(intent, "Share"));
	}
	*/
	public static void shareContent(Context context, String subject, String summary, String url) {
		
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, url);
		context.startActivity(Intent.createChooser(intent, "Share"));
		
	}
	
	public static void callPhone(Context context, String phoneNumber) {
		viewURL(context, "tel:" + phoneNumber);
	}
	
	public static void composeEmail(Context context, String email) {
		viewURL(context, "mailto:" + email);
	}
	
	public static void composeEmail(Context context, String email, String subject, String body) {
		try {
			String url = "mailto:";
			if (email != null) {
				url += email;
			}
			url += "?";
			
			if (subject != null) {			
				String subjectEncoded = URLEncoder.encode(subject, "UTF-8");
				subjectEncoded = subjectEncoded.replace("+", "%20");
				url += "subject=" + subjectEncoded;
			}
			
			if (body != null) {
				if (subject != null) {
					url += "&";
				}
				String bodyEncoded = URLEncoder.encode(body, "UTF-8");
				bodyEncoded = bodyEncoded.replace("+", "%20");
				url += "body=" + bodyEncoded;
			}
			
			viewURL(context, url);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void composeEmail(Context context, String email, String subject) {
		composeEmail(context, email, subject, null);
	}
	
	public static void searchMap(Context context, String mapQuery) {
		Intent intent = new Intent(context, MITMapActivity.class); 
		intent.setAction(Intent.ACTION_SEARCH);
		intent.putExtra(SearchManager.QUERY, mapQuery);
		context.startActivity(intent);
	}
	
	public static boolean hasProtocol(String url) {
		String[] prefixes = new String[] {"http://", "ftp://", "tel:", "mailto:"};
		
		for(int i = 0; i < prefixes.length; i++) {
			if(url.startsWith(prefixes[i])) {
				return true;
			}
		}
		
		return false;
	}

	
	/*************************************************************************************/
	static String FB = "com.facebook.katana.ShareLinkActivity";
	
	public static void shareCustomContent(final Context ctx, String subject, String summary, final String url) {
		
		
		String extraText = summary;
		if(extraText.length() > 100) {
			extraText = extraText.substring(0, 99);
			extraText = extraText.trim() + "...";
		}
		extraText += " " + url;
		
		// Prepare Intent...
		final Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		sendIntent.setType("text/plain");
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		sendIntent.putExtra(Intent.EXTRA_TEXT, extraText);
		
		// ListView rows...
		class shareRow {
			Drawable d;
			String app;
		}
		shareRow sr;
		final ArrayList<shareRow> srs = new ArrayList<shareRow>();
		
		// Get list of apps...
		PackageManager pm = ctx.getPackageManager(); 
		final List<ResolveInfo> activityList = pm.queryIntentActivities (sendIntent, 0); 
		for (int i = 0; i < activityList.size(); i++) { 
			sr = new shareRow();
			ResolveInfo app = activityList.get(i); 
			sr.app = app.activityInfo.loadLabel(pm).toString();
			sr.d = app.loadIcon(pm);
			srs.add(sr);
		} 

		
		final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		// #1
		//View layout = inflater.inflate(R.layout.dialog_share, (ViewGroup) ctx.findViewById(R.id.shareRoot));
		//ListView lv = (ListView) layout.findViewById(R.id.shareLV);
		// #2
		ListView lv = (ListView) inflater.inflate(R.layout.dialog_share, null);
		
		// Create Chooser dialog...
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle("Share via");

		// Adapter and Listener...
		ArrayAdapter<shareRow> adapter = new ArrayAdapter<shareRow>(ctx, 0, srs) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				if(convertView == null) {
					//convertView = inflater.inflate(xxx, null);
				}
				
				View row = inflater.inflate(R.layout.dialog_share_row, null);
				
				shareRow sr = srs.get(position);
				
				ImageView iv = (ImageView) row.findViewById(R.id.shareIV);
				iv.setImageDrawable(sr.d);
				
				TextView tv = (TextView) row.findViewById(R.id.shareTV);
				tv.setText(sr.app);
				
				return row;
				
			}
		};
		lv.setAdapter(adapter);
		
		OnItemClickListener listener = new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
		    {
				ResolveInfo launchable = activityList.get(position); 
				ActivityInfo act = launchable.activityInfo; 
				ComponentName name = new ComponentName(act.applicationInfo.packageName, act.name); 
				sendIntent.setComponent(name);
				//if (activity.name.equals(FB)) { 
				if (act.name.matches(".*facebook.*")) { 
					// only send url 
					sendIntent.putExtra(Intent.EXTRA_TEXT, url);  // overwrite
				} 
				ctx.startActivity(sendIntent);
		    }
		};
		lv.setOnItemClickListener(listener);
		
		
		AlertDialog alert = builder.create();
		alert.setView(lv, 0, 0, 0, 0);
		alert.show();
		
	}
	
	public static void doAction(Context context, String actionUrl) {
		// first check if this is a recognized URL
		if(SpecialActions.actionUrl(actionUrl) != null) {
			actionUrl = SpecialActions.actionUrl(actionUrl);
		}
		
		if(actionUrl.startsWith("http://") || actionUrl.startsWith("https://")) {
			viewURL(context, actionUrl);
		}
		
		if(actionUrl.startsWith("mitmobile://")) {
			Module module = null;
			if(actionUrl.startsWith("mitmobile://map/")) {
				module = new MapsModule();			
			} else if (actionUrl.startsWith("mitmobile://mit150/")) {
				module = new MIT150Module();
			} else if (actionUrl.startsWith("mitmobile://calendar/")) {
				module = new EventsModule();
			} else if (actionUrl.startsWith("mitmobile://qrreader/")) {
				module = new QRReaderModule();
			} else if (actionUrl.startsWith("mitmobile://links/")) {
				module = new LinksModule();
			}
			
			if(module != null) {
				module.handleUrl(context, actionUrl);
			}
		}
	}
	
	public static int getActionIconId(String actionUrl) {
		if(actionUrl.startsWith("http://")) {
			return R.drawable.action_external;
		}
		
		if(actionUrl.startsWith("mitmobile://map/search?")) {
			return R.drawable.action_map;
		}
		
		return -1;
	}
}
