package edu.mit.mitmobile2.links;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;

public class LinksActivity extends ModuleActivity {

	Context mContext;
	FullScreenLoader mLoader;
	@Override
	protected Module getModule() {
		// TODO Auto-generated method stub
		return new LinksModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.links_main);
		LinksModel.fetchLinks(this, uiHandler);
		mContext = this;
		mLoader = (FullScreenLoader) findViewById(R.id.links_loader);
		mLoader.showLoading();
	}
	
	private Handler uiHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == MobileWebApi.SUCCESS) {
				ListView linkList = (ListView) findViewById(R.id.links_list);
				@SuppressWarnings("unchecked")
				ArrayList<LinkItem> links = (ArrayList<LinkItem>) msg.obj;
				
				SimpleArrayAdapter<LinkItem> adapter = new SimpleArrayAdapter<LinkItem>(app, links, R.layout.boring_action_row) {
					@Override
					public void updateView(final LinkItem item, View view) {
						// TODO Auto-generated method stub
						TwoLineActionRow row = (TwoLineActionRow) view;
						
						
						row.setTitle(item.name);
						row.setSubtitle(item.url);
						
						row.setOnTouchListener(new OnTouchListener() {
							@Override
							public boolean onTouch(View v, MotionEvent event) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.url));
								startActivity(intent);
								return true;
							}
							
						});
					}
				};
				
				linkList.setAdapter(adapter);
				
				mLoader.setVisibility(View.GONE);
				
			}
		}
	};

}
