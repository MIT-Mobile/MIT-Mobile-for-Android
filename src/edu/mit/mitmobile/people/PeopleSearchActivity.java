package edu.mit.mitmobile.people;

import android.content.Context;
import android.os.Handler;
import android.widget.ArrayAdapter;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SearchActivity;
import edu.mit.mitmobile.objs.PersonItem;
import edu.mit.mitmobile.objs.SearchResults;
import edu.mit.mitmobile.objs.PersonItem.PersonDetailViewMode;

public class PeopleSearchActivity extends SearchActivity<PersonItem> {
	
	public static void peopleSearch(Context context, String name) {
		launchSearch(context, name, PeopleSearchActivity.class);
	}
	
	@Override
	protected String getSuggestionsAuthority() {
		return PeopleSearchSuggestionsProvider.AUTHORITY;
	}

	@Override
	protected ArrayAdapter<PersonItem> getListAdapter(SearchResults<PersonItem> results) {
		PeopleListAdapter recentlyViewedListAdapter = new PeopleListAdapter(this, results.getResultsList(), R.layout.boring_action_row);
		recentlyViewedListAdapter.setLookupHandler(mSearchListView, PersonDetailViewMode.SEARCH, results.getSearchTerm());
		return recentlyViewedListAdapter;
	}

	@Override
	protected void initiateSearch(String searchTerm, Handler uiHandler) {
		PeopleModel.executeSearch(searchTerm, this, uiHandler);	
	}

	@Override
	protected String searchItemPlural() {
		return "people";
	}

	@Override
	protected String searchItemSingular() {
		return "person";
	}

	@Override
	protected Module getModule() {
		return new PeopleModule();
	}
}
