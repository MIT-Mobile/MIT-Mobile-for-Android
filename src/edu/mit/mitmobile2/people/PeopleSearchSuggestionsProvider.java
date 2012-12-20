package edu.mit.mitmobile2.people;

import edu.mit.mitmobile2.MITSearchRecentsProvider;
import edu.mit.mitmobile2.about.Config;

public class PeopleSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = Config.release_project_name + ".people.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public PeopleSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
