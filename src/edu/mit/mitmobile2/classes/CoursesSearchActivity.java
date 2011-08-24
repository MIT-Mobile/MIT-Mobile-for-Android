package edu.mit.mitmobile2.classes;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchActivity;
import edu.mit.mitmobile2.objs.CourseItem;
import edu.mit.mitmobile2.objs.SearchResults;
import edu.mit.mitmobile2.classes.MITCoursesDetailsSliderActivity;
import edu.mit.mitmobile2.classes.CoursesDataModel;
import edu.mit.mitmobile2.classes.CourseSearchSuggestionsProvider;

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

    @Override
    protected void continueSearch(String searchTerm, Handler uiHandler, int nextIndex) {
    }

    @Override
    protected boolean supportsMoreResult() {
        return false;
    }	
}
