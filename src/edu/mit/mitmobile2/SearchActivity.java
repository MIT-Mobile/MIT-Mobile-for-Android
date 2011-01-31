package edu.mit.mitmobile2;

import edu.mit.mitmobile2.MobileWebApi;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.SearchResults;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public abstract class SearchActivity<ResultItem> extends ModuleActivity {
	
	public static final String EXTRA_AUTHORITY = "authority";
	
	protected abstract ArrayAdapter<ResultItem> getListAdapter(SearchResults<ResultItem> results);
	protected abstract void initiateSearch(String searchTerm, Handler uiHandler);
	protected abstract String getSuggestionsAuthority();
	
	protected static int SUGGESTIONS_MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;
	
	protected ListView mSearchListView;
	protected SearchResultsHeader mSearchResultsHeader;
	protected View mLoadingView;
	
	
	protected boolean mSearching = true;
	protected boolean mResultsDisplayed = false;
	protected String mSearchTerm;
	
	protected static void launchSearch(Context context, String query, Class<? extends SearchActivity<?>> searchActivity) {
		Intent intent = new Intent(context, searchActivity);
		intent.setAction(Intent.ACTION_SEARCH);
		intent.putExtra(SearchManager.QUERY, query);
		context.startActivity(intent);
	}
	
	private Handler searchHandler(final String searchTerm) {
	
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(searchTerm.equals(mSearchTerm)) {
					mLoadingView.setVisibility(View.GONE);
					mSearching = false;
			
					if(msg.arg1 == MobileWebApi.SUCCESS) {
						mResultsDisplayed = true;
						@SuppressWarnings("unchecked")
						final SearchResults<ResultItem> searchResults = (SearchResults<ResultItem>) msg.obj;
				
						if(searchResults.getResultsList().size() == 0) {
							resetBackToSearch();
							Toast.makeText(SearchActivity.this, "No matches found", Toast.LENGTH_SHORT).show();
						}
				
						mSearchTerm = searchResults.getSearchTerm();
						showSummaryView(mSearchTerm, searchResults.getResultsList().size(), searchResults.isPartialResult(), searchResults.totalResultsCount());
						mSearchListView.setAdapter(getListAdapter(searchResults));
						mSearchListView.setVisibility(View.VISIBLE);
				
					} else if(msg.arg1 == MobileWebApi.ERROR) {
						mResultsDisplayed = false;
						resetBackToSearch();
					} else if(msg.arg1 == MobileWebApi.CANCELLED) {
						mResultsDisplayed = false;
						resetBackToSearch();
					}
				}
			}
		};
	}
	
	private void doSearch(Intent searchIntent) {
		if(Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
			String searchTerm = searchIntent.getStringExtra(SearchManager.QUERY);
			
			// save the search to the suggestions list
			Log.d(this.getClass().toString(), getSuggestionsAuthority());
			MITSearchRecentSuggestions suggestions = new MITSearchRecentSuggestions(this, getSuggestionsAuthority(), SUGGESTIONS_MODE);
			suggestions.saveRecentQuery(searchTerm.toLowerCase(), null);
			
			// hide the list view and header view
			mSearchListView.setVisibility(View.GONE);
			mSearchResultsHeader.setVisibility(View.GONE);
			
			// show the search indicator
			// do the search
			mLoadingView.setVisibility(View.VISIBLE);					

			mResultsDisplayed = false;
			mSearching = true;
			mSearchTerm = searchTerm;
			initiateSearch(searchTerm, searchHandler(searchTerm));
		}
	}
	
	@Override
	public void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
		doSearch(newIntent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.search_results);
		
		mSearchListView = (ListView) findViewById(R.id.searchResultsList);
		mSearchResultsHeader = (SearchResultsHeader) findViewById(R.id.searchResultsSummaryView);
		mLoadingView = findViewById(R.id.searchResultsLoading);
		
		doSearch(getIntent());
	}
	
	private void showSummaryView(String searchTerm, int resultsCount, boolean isPartial, Integer totalResults) {
		String summaryText;
		if(resultsCount == 0) {
			summaryText = "No matches for \"" + searchTerm + "\"";
		} else if(resultsCount == 1) {
			summaryText = "1 match for \""  + searchTerm + "\"";
		} else if(!isPartial) {
			summaryText = resultsCount + " " + searchItemPlural() + " matching \""  + searchTerm + "\"";
		} else {
			summaryText = "Many " + searchItemPlural() + " found showing " + resultsCount;
		}
		
		mSearchResultsHeader.setText(summaryText);
		mSearchResultsHeader.setVisibility(View.VISIBLE);
	}
	
	private void resetBackToSearch() {
		mResultsDisplayed = false;
		startSearch(mSearchTerm, false, null, false);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(!mSearching && !mResultsDisplayed && hasFocus) {
			finish();
		}
		super.onWindowFocusChanged(hasFocus);
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { }
	
	abstract protected String searchItemPlural();
	
	abstract protected String searchItemSingular();
}
