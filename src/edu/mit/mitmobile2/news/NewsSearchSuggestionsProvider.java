package edu.mit.mitmobile2.news;

import edu.mit.mitmobile2.MITSearchRecentsProvider;
import edu.mit.mitmobile2.about.BuildSettings;

public class NewsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = BuildSettings.release_project_name + ".news.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public NewsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
