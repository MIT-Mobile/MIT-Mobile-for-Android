package edu.mit.mitmobile2.events;

import android.os.Handler;
import android.widget.ArrayAdapter;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchActivity;
import edu.mit.mitmobile2.objs.EventDetailsItem;
import edu.mit.mitmobile2.objs.SearchResults;

public class EventsSearchActivity extends SearchActivity<EventDetailsItem> {

	@Override
	protected ArrayAdapter<EventDetailsItem> getListAdapter(final SearchResults<EventDetailsItem> results) {		
		return new EventsArrayAdapter(this, R.layout.events_row, 0, results.getResultsList(), EventDetailsItem.SHORT_DAY_TIME);
	}

	@Override
	protected String getSuggestionsAuthority() {
		return EventsSearchSuggestionsProvider.AUTHORITY;
	}

	@Override
	protected void initiateSearch(String searchTerm, Handler uiHandler) {
		EventsModel.executeSearch(searchTerm, this, uiHandler);		
	}

	@Override
	protected String searchItemPlural() {
		return "Event";
	}

	@Override
	protected String searchItemSingular() {
		return "Events";
	}


	@Override
	protected void onItemSelected(SearchResults<EventDetailsItem> results, EventDetailsItem event) {
		MITEventsSliderActivity.launchSearchResults(EventsSearchActivity.this, event.id, results.getSearchTerm());		
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new EventsModule();
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
