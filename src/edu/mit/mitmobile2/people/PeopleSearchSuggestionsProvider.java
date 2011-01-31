package edu.mit.mitmobile2.people;

import edu.mit.mitmobile2.MITSearchRecentsProvider;

public class PeopleSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile2.people.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public PeopleSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
