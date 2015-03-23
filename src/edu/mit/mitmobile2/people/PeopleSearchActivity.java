package edu.mit.mitmobile2.people;

import android.content.Context;
import android.os.Handler;
import android.widget.ArrayAdapter;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchActivity;
import edu.mit.mitmobile2.objs.PersonItem;
import edu.mit.mitmobile2.objs.PersonItem.PersonDetailViewMode;
import edu.mit.mitmobile2.objs.SearchResults;

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
		return new PeopleListAdapter(this, results.getResultsList(), R.layout.boring_action_row);
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
    protected boolean supportsMoreResult() {
        return false;
    }

	@Override
	protected void onItemSelected(SearchResults<PersonItem> searchResults, PersonItem item) {
		PeopleDetailActivity.launchActivity(this, item, PersonDetailViewMode.SEARCH, searchResults.getSearchTerm());		
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new PeopleModule();
	}

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
}
