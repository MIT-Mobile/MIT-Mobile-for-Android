package edu.mit.mitmobile2.classes;

import edu.mit.mitmobile2.MITSearchRecentsProvider;
import edu.mit.mitmobile2.about.BuildSettings;

public class CourseSearchSuggestionsProvider extends MITSearchRecentsProvider {
	public final static String AUTHORITY = BuildSettings.release_project_name + ".classes.SuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public CourseSearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
