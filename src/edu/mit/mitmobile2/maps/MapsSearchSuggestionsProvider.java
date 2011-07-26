package edu.mit.mitmobile2.maps;

import edu.mit.mitmobile2.MITSearchRecentsProvider;
import edu.mit.mitmobile2.about.BuildSettings;

public class MapsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = BuildSettings.release_project_name + ".maps.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public MapsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
