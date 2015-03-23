package edu.mit.mitmobile2.events;

import edu.mit.mitmobile2.MITSearchRecentsProvider;
import edu.mit.mitmobile2.about.Config;


public class EventsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = Config.release_project_name + ".events.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public EventsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
