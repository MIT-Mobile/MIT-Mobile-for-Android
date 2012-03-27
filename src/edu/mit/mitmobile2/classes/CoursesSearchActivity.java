package edu.mit.mitmobile2.classes;

import android.os.Handler;
import android.widget.ArrayAdapter;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchActivity;
import edu.mit.mitmobile2.objs.CourseItem;
import edu.mit.mitmobile2.objs.SearchResults;

public class CoursesSearchActivity extends SearchActivity<CourseItem> {

	@Override
	protected ArrayAdapter<CourseItem> getListAdapter(final SearchResults<CourseItem> results) {
		final CoursesArrayAdapter adapter = new CoursesArrayAdapter(this, R.layout.courses_row, results.getResultsList());
		adapter.setUseLongFormat(true);
		return adapter;
	}

	@Override
	protected String getSuggestionsAuthority() {
		return CourseSearchSuggestionsProvider.AUTHORITY;
	}

	@Override
	protected void initiateSearch(String searchTerm, Handler uiHandler) {
		CoursesDataModel.executeSearch(searchTerm, this, uiHandler);
	}

	@Override
	protected String searchItemPlural() {
		return "Classes";
	}

	@Override
	protected String searchItemSingular() {
		return "Class";
	}

	@Override
	protected void onItemSelected(SearchResults<CourseItem> results, CourseItem item) {
		MITCoursesDetailsSliderActivity.launchActivity(CoursesSearchActivity.this, item, true, results.getSearchTerm());	
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new ClassesModule();
	}

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
}
