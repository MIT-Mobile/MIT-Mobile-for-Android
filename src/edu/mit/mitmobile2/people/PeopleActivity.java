package edu.mit.mitmobile2.people;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.HighlightEffects;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.emergency.EmergencyContactsActivity;
import edu.mit.mitmobile2.objs.PersonItem;
import edu.mit.mitmobile2.objs.PersonItem.PersonDetailViewMode;

public class PeopleActivity extends NewModuleActivity {
	
	private ListView mRecentlyViewed;
	private View mRecentlyViewedListViewHeader;
	private View mRecentlyViewedSectionHeader;
	private List<PersonItem> mRecents;
	
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
	
	private static String MENU_CLEAR_RECENTS = "clear_recents";
	
	@Override
	protected List<MITMenuItem> getSecondaryMenuItems() {
		return Arrays.asList(
			new MITMenuItem(MENU_CLEAR_RECENTS, "Clear Recents")
		);
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected NewModule getNewModule() {
		return new PeopleModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String id) {
	    if (id.equals(MENU_CLEAR_RECENTS)) {
		PeopleDB.getInstance(this).clearAll();
		populateRecentlyViewed();
	    }
	}
}
