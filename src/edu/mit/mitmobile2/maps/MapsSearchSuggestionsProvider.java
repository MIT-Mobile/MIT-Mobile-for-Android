package edu.mit.mitmobile2.maps;

import edu.mit.mitmobile2.MITSearchRecentsProvider;
import edu.mit.mitmobile2.about.Config;

public class MapsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = Config.release_project_name + ".maps.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public MapsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
