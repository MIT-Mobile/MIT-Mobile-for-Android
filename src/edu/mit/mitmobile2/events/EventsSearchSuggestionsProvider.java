package edu.mit.mitmobile2.events;

import edu.mit.mitmobile2.MITSearchRecentsProvider;


public class EventsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile2.events.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public EventsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
