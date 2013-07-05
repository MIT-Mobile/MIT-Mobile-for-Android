package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;

public class SimpleSingleWebViewActivity extends NewModuleActivity {
	
	public static final String WEB_TEXT_HTML_KEY = "dining.webtext";
	
	public static void launch(Context context, String htmlText) {
		Intent intent = new Intent(context, SimpleSingleWebViewActivity.class);
		intent.putExtra(SimpleSingleWebViewActivity.WEB_TEXT_HTML_KEY, htmlText);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dining_simple_webview);
		
		String text = getIntent().getStringExtra(WEB_TEXT_HTML_KEY);
		
		WebView webView = (WebView) findViewById(R.id.diningWebText);
		webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);

	}
	
	@Override
	protected NewModule getNewModule() {
		return new DiningModule();
	}

	@Override
	protected boolean isScrollable() {
		return true;
	}
	
	@Override
	protected List<String> getMenuItemBlackList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(DiningModule.FILTER_ITEM_ID);
		list.add(DiningModule.LISTVIEW_ITEM_ID);
		list.add(DiningModule.MAPVIEW_ITEM_ID);
		return list;
	}

	@Override
	protected void onOptionSelected(String optionId) { }

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
