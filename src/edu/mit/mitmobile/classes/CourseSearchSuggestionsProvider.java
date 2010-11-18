package edu.mit.mitmobile.classes;

import edu.mit.mitmobile.MITSearchRecentsProvider;

public class CourseSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile.classes.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public CourseSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
