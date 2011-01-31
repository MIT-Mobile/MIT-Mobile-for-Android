package edu.mit.mitmobile2.news;

import edu.mit.mitmobile2.MITSearchRecentsProvider;

public class NewsSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile2.news.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public NewsSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
