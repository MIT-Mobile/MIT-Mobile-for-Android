package edu.mit.mitmobile.events;

import edu.mit.mitmobile.MITSearchRecentsProvider;


public class EventsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile.events.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public EventsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
