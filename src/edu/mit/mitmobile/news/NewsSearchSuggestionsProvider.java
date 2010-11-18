package edu.mit.mitmobile.news;

import edu.mit.mitmobile.MITSearchRecentsProvider;

public class NewsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile.news.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public NewsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
