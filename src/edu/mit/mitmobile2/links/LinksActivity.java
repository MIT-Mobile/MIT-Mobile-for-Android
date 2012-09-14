package edu.mit.mitmobile2.links;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SectionHeader;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.links.LinkListItem.LinkItem;

public class LinksActivity extends NewModuleActivity {

	Context mContext;
	FullScreenLoader mLoader;
	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new LinksModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		// TODO Auto-generated method stub
		return true;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.links_main);
		setTitle("Links");
		LinksModel.fetchLinks(this, uiHandler);
		mContext = this;
		mLoader = (FullScreenLoader) findViewById(R.id.links_loader);
		mLoader.showLoading();
	}
	
	private Handler uiHandler = new Handler() {

		
		public void handleMessage(Message msg) {
			ArrayList<LinkListItem> linkSections = null;
			LinearLayout container = (LinearLayout) findViewById(R.id.link_list_container);
			if (msg.arg1 == MobileWebApi.SUCCESS) {
				@SuppressWarnings("unchecked")
				ArrayList<LinkListItem> unsafeLinkSections = (ArrayList<LinkListItem>) msg.obj;
				linkSections = unsafeLinkSections;
			} else {
				linkSections = LinksModel.getCachedLinks(mContext);
			}
			
			
			if (linkSections != null) {
				for (LinkListItem section : linkSections) {
					SectionHeader header = new SectionHeader(mContext, section.title);
					container.addView(header);
					for (final LinkItem link : section.links) {
						TwoLineActionRow row = new TwoLineActionRow(mContext);
						row.setTitle(link.name);
						row.setSubtitle(link.url);
						row.setBackgroundColor(Color.WHITE);
						row.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.url));
								startActivity(intent);
							}
						});
						container.addView(row);
						
						DividerView divider = new DividerView(mContext, null);
						container.addView(divider);
						
					}
				}
				mLoader.setVisibility(View.GONE);
			} else {
				mLoader.showError();
			}
		}
	};

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}

}
