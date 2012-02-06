package edu.mit.mitmobile2.shuttles;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SectionHeader;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.shuttles.ShuttleRouteArrayAdapter.SectionListItemView;

public class ShuttlesActivity extends ModuleActivity {
	
	Context ctx;
	
	ListView routeListView;
	ShuttleRouteArrayAdapter adapter;
	private View mFooterView;

	private FullScreenLoader shuttleRouteLoader;

	static final int MENU_HOME     = Menu.FIRST;
	static final int MENU_CALL_SAFERIDE = Menu.FIRST+1;
	static final int MENU_CALL_PARKING = Menu.FIRST+2;
	static final int MENU_REFRESH = Menu.FIRST+3;
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		ctx = this;
		
		createView();
		
	}
	/****************************************************/
	void createView() {
		
		setContentView(R.layout.shuttles);
		routeListView = (ListView) findViewById(R.id.routeLV);
		shuttleRouteLoader = (FullScreenLoader) findViewById(R.id.shuttleRoutesLoader);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFooterView = inflater.inflate(R.layout.shuttle_footer, null);
		
		TextView tv = (TextView) mFooterView.findViewById(R.id.shuttleParkingTV);
		tv.setText("Parking Office (617.258.6510)", TextView.BufferType.SPANNABLE);
		Spannable span = (Spannable) tv.getText();
		span.setSpan(new TextAppearanceSpan(this, R.style.ListItemPrimary),
				0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new TextAppearanceSpan(this, R.style.ListItemSecondary), 
                14, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:6172586510"));
				startActivity(intent);
			}
		});
		
		tv = (TextView) mFooterView.findViewById(R.id.shuttleSaferideTV);
		tv.setText("Saferide (617.253.2997)", TextView.BufferType.SPANNABLE);
		span = (Spannable) tv.getText();
		span.setSpan(new TextAppearanceSpan(this, R.style.ListItemPrimary),
				0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new TextAppearanceSpan(this, R.style.ListItemSecondary), 
                8, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:6172532997"));
				startActivity(intent);
			}
		});
		
		// add the external transit links
		mFooterView.findViewById(R.id.shuttleMbtaBusTimes).setOnClickListener(
			new View.OnClickListener() {	
				@Override
				public void onClick(View v) {
					CommonActions.viewURL(ctx, "http://www.nextbus.com/webkit");
				}
			}
		);
		mFooterView.findViewById(R.id.shuttleMbtaTrainTimes).setOnClickListener(
			new View.OnClickListener() {	
				@Override
				public void onClick(View v) {
					CommonActions.viewURL(ctx, "http://www.mbtainfo.com");
				}
			}
		);
		mFooterView.findViewById(R.id.shuttleGoogleTransit).setOnClickListener(
			new View.OnClickListener() {	
				@Override
				public void onClick(View v) {
					CommonActions.viewURL(ctx, "http://www.google.com/transit");
				}
			}
		);
		
		routeListView.addFooterView(mFooterView);
			
		
		getData(false);
	
	}
	/****************************************************/
	void updateView() {
		
		shuttleRouteLoader.setVisibility(View.GONE);
		
		List<RouteItem> dayRoutes = ShuttleModel.getRoutes(false);
		List<RouteItem> nightRoutes = ShuttleModel.getRoutes(true);
		
		SectionListItemView itemBuilder = new SectionListItemView() {
			public View getView(Object item, View convertView, ViewGroup parent) {
				View v = convertView;
				if (v == null) {
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = inflater.inflate(R.layout.shuttles_row, null);
				}
				
				RouteItem routeItem = (RouteItem) item;
				
				TextView routeTV = (TextView) v.findViewById(R.id.shuttleRowRouteTV);
				routeTV.setText(routeItem.title);
			
				ImageView routeIV = (ImageView) v.findViewById(R.id.shuttleRowRouteIV);
				if (routeItem.isRunning) {
					routeIV.setImageResource(R.drawable.shuttle);
				} else {
					routeIV.setImageResource(R.drawable.shuttle_off);
				}
				return v;
			}
		};
		
		ShuttleRouteArrayAdapter adapter = new ShuttleRouteArrayAdapter(this, itemBuilder);
		adapter.addSection(getString(R.string.daytime_shuttle), dayRoutes);
		adapter.addSection(getString(R.string.nighttime_shuttle), nightRoutes);
		
		routeListView.setVisibility(View.VISIBLE);
		routeListView.setAdapter(adapter);
		
		OnItemClickListener listener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (view == mFooterView) return;
				
				Integer routeInt = (Integer) view.getTag();
				
				Intent i = new Intent(ctx, MITRoutesSliderActivity.class);
				i.putExtra(SliderActivity.KEY_POSITION, routeInt);
					
				startActivity(i);
			}
		};
		
		routeListView.setOnItemClickListener(listener);

	}
	/****************************************************/
	protected void getData(boolean forceRefresh) {

		
		shuttleRouteLoader.setVisibility(View.VISIBLE);
		shuttleRouteLoader.showLoading();
		routeListView.setVisibility(View.GONE);
		
		// this Handler will run on this thread (UI)
		final Handler myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.arg1 == MobileWebApi.SUCCESS) {
					updateView();
				} else {
					shuttleRouteLoader.showError();
				}
			}
		};
		
		ShuttleModel.fetchRoutes(ctx, myHandler, forceRefresh);		
	}	
	/****************************************************/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH: 
			getData(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected Module getModule() {
		return new ShuttlesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_REFRESH, Menu.NONE, "Refresh")
		  .setIcon(R.drawable.menu_refresh);
	}
    
}
