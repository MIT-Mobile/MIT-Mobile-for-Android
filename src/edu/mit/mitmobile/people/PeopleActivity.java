package edu.mit.mitmobile.people;

import java.util.List;

import edu.mit.mitmobile.CommonActions;
import edu.mit.mitmobile.HighlightEffects;
import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.ModuleActivity;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SearchBar;
import edu.mit.mitmobile.TwoLineActionRow;
import edu.mit.mitmobile.emergency.EmergencyContactsActivity;
import edu.mit.mitmobile.objs.PersonItem;
import edu.mit.mitmobile.objs.PersonItem.PersonDetailViewMode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class PeopleActivity extends ModuleActivity {
	
	private SearchBar mSearchBar;
	private ListView mRecentlyViewed;
	private View mRecentlyViewedListViewHeader;
	private View mRecentlyViewedSectionHeader;
	private List<PersonItem> mRecents;

	private static final int MENU_CLEAR_RECENTS = MENU_SEARCH + 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		createViews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		populateRecentlyViewed();
	}
	
	private void createViews() {
		setContentView(R.layout.people_main);
		
		mSearchBar = (SearchBar) findViewById(R.id.peopleSearchBar);
		mSearchBar.setSearchHint(getString(R.string.people_search_hint));
		mSearchBar.setSystemSearchInvoker(this);
		
		

		mRecentlyViewed = (ListView) findViewById(R.id.peopleRecentlyViewed);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headerView = inflater.inflate(R.layout.people_main_header, null);		
		mRecentlyViewedListViewHeader = headerView;
		
		TwoLineActionRow mainDirectoryCall = (TwoLineActionRow) headerView.findViewById(R.id.peopleDirectoryNumberRow);
		TwoLineActionRow moreEmergencyContacts = (TwoLineActionRow) headerView.findViewById(R.id.moreEmergencyContactsRow);
		
		String title1 = "Phone Directory";
		String title2 = "(617.253.1000)";
		mainDirectoryCall.setTitle(title1 + " " + title2, TextView.BufferType.SPANNABLE);
		Spannable spannable = (Spannable) mainDirectoryCall.getTitle();
		int separator = title1.length() + 1;
		spannable.setSpan(
				new TextAppearanceSpan(this, R.style.ListItemPrimary),
				0, separator, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(
				new TextAppearanceSpan(this, R.style.ListItemSecondary), 
				separator,
				spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
		mainDirectoryCall.setActionIconResource(R.drawable.action_phone);
		mainDirectoryCall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonActions.callPhone(PeopleActivity.this, "6172531000");
			}
		});
		
		moreEmergencyContacts.setTitle("Emergency Contacts");
		final Context context = this;
		moreEmergencyContacts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, EmergencyContactsActivity.class);
				startActivity(intent);
			}
		});
		
		mRecentlyViewedSectionHeader = headerView.findViewById(R.id.peopleMainRecentlyViewedSectionHeader);
		HighlightEffects.turnOffHighlightingEffects(headerView);
		
		mRecentlyViewed.addHeaderView(mRecentlyViewedListViewHeader);
	}
	
	private void populateRecentlyViewed() {
		
		mRecents = PeopleModel.getRecentlyViewed(this);
		
		if(mRecents.size() > 0) {
			mRecentlyViewedSectionHeader.setVisibility(View.VISIBLE);
		} else {
			mRecentlyViewedSectionHeader.setVisibility(View.GONE);
		}
		
		PeopleListAdapter recentlyViewedListAdapter = new PeopleListAdapter(PeopleActivity.this, mRecents, R.layout.boring_action_row);
		recentlyViewedListAdapter.setHasHeader(true);
		mRecentlyViewed.setAdapter(recentlyViewedListAdapter);
		recentlyViewedListAdapter.setLookupHandler(mRecentlyViewed, PersonDetailViewMode.RECENT, null);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CLEAR_RECENTS:
			PeopleDB.getInstance(this).clearAll();
			populateRecentlyViewed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		if (mRecents.size() > 0) {
			menu.add(0, MENU_CLEAR_RECENTS, Menu.NONE, "Clear Recents")
				.setIcon(R.drawable.menu_clear_recent);
		}
		
		menu.add(0, MENU_SEARCH, Menu.NONE, MENU_SEARCH_TITLE)
			.setIcon(R.drawable.menu_search);
	}

	@Override
	protected Module getModule() {
		return new PeopleModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}
}
