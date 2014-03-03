package edu.mit.mitmobile2.news.view;

import edu.mit.mitmobile2.MITSearchRecentsProvider;
import edu.mit.mitmobile2.about.Config;

public class NewsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = Config.release_project_name + ".news.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public NewsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
