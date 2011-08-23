package edu.mit.mitmobile2.libraries;

import edu.mit.mitmobile2.MITSearchRecentsProvider;
import edu.mit.mitmobile2.about.BuildSettings;

public class LibrarySearchSuggestionsProvider extends MITSearchRecentsProvider {
    
    public final static String AUTHORITY = BuildSettings.release_project_name + ".libraries.SuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;
    
    public LibrarySearchSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
