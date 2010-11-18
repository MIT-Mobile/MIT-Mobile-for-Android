package edu.mit.mitmobile.maps;

import edu.mit.mitmobile.MITSearchRecentsProvider;

public class MapsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile.maps.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public MapsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
