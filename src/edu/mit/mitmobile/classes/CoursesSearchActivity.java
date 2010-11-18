package edu.mit.mitmobile.classes;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SearchActivity;
import edu.mit.mitmobile.objs.CourseItem;
import edu.mit.mitmobile.objs.SearchResults;
import edu.mit.mitmobile.classes.MITCoursesDetailsSliderActivity;
import edu.mit.mitmobile.classes.CoursesDataModel;
import edu.mit.mitmobile.classes.CourseSearchSuggestionsProvider;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class CoursesSearchActivity extends SearchActivity<CourseItem> {

	@Override
	protected ArrayAdapter<CourseItem> getListAdapter(final SearchResults<CourseItem> results) {
		final CoursesArrayAdapter adapter = new CoursesArrayAdapter(this, R.layout.courses_row, results.getResultsList());
		adapter.setUseLongFormat(true);
		mSearchListView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {		
						CourseItem item = adapter.getItem(position);
						MITCoursesDetailsSliderActivity.launchActivity(CoursesSearchActivity.this, item, true, results.getSearchTerm());
					}
				}
			);
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
	protected Module getModule() {
		return new ClassesModule();
	}	
}
