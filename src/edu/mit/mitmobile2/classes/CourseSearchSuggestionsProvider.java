package edu.mit.mitmobile2.classes;

import edu.mit.mitmobile2.MITSearchRecentsProvider;

public class CourseSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = "edu.mit.mitmobile2.classes.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public CourseSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
