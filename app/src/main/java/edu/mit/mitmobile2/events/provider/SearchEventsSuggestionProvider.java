package edu.mit.mitmobile2.events.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by serg on 5/1/15.
 */
public class SearchEventsSuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "edu.mit.mitmobile2.events.provider.SearchEventsSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchEventsSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
