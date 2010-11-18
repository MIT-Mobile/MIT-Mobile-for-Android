package edu.mit.mitmobile.people;

import edu.mit.mitmobile.MITSearchRecentsProvider;

public class PeopleSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile.people.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public PeopleSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
