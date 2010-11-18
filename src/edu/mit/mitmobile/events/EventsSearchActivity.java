package edu.mit.mitmobile.events;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SearchActivity;
import edu.mit.mitmobile.objs.EventDetailsItem;
import edu.mit.mitmobile.objs.SearchResults;

public class EventsSearchActivity extends SearchActivity<EventDetailsItem> {

	@Override
	protected ArrayAdapter<EventDetailsItem> getListAdapter(final SearchResults<EventDetailsItem> results) {
		mSearchListView.setOnItemClickListener(		
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View row, int position, long arg3) {
					EventDetailsItem event = (EventDetailsItem) adapterView.getItemAtPosition(position);
					MITEventsSliderActivity.launchSearchResults(EventsSearchActivity.this, event.id, results.getSearchTerm());
				}
			}
		);
		
		return new EventsArrayAdapter(this, R.layout.events_row, 0, results.getResultsList());
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
	protected Module getModule() {
		return new EventsModule();
	}

}
