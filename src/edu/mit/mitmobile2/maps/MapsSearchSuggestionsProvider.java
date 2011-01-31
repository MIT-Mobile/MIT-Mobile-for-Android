package edu.mit.mitmobile2.maps;

import edu.mit.mitmobile2.MITSearchRecentsProvider;

public class MapsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile2.maps.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public MapsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
