package edu.mit.mitmobile2;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import edu.mit.mitmobile2.objs.SearchResults;

public abstract class SearchActivity<ResultItem> extends NewModuleActivity {
	
	public static final String EXTRA_AUTHORITY = "authority";
	
	protected abstract ArrayAdapter<ResultItem> getListAdapter(SearchResults<ResultItem> results);
	protected abstract void initiateSearch(String searchTerm, Handler uiHandler);
	protected abstract String getSuggestionsAuthority();
	
	protected static int SUGGESTIONS_MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;
	
	private ListView mSearchListView;
	protected SearchResultsHeader mSearchResultsHeader;
	protected FullScreenLoader mLoadingView;
	protected TwoLineActionRow mLoadMore;
	private static final String LOAD_MORE = "Load more";
	private static final String LOADING = "Loading...";
	private static final String TRY_AGAIN = "Loading Fail (Try Again)";
	
	
	protected boolean mSearching = true;
	private Long mLastFailedSearchTime = null;
	private final static long MINIMUM_TRY_AGAIN_WAIT = 5000; // 5 seconds
	protected boolean mResultsDisplayed = false;
	private boolean mResetSearchScheduled = false;
	private String mSearchTerm;
	private SearchResults<ResultItem> mSearchResults;
	
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
						
						// use a temp variable (to allow use of @SuppressWarnings on variable)
						@SuppressWarnings("unchecked")
						final SearchResults<ResultItem> tempSearchResults = (SearchResults<ResultItem>) msg.obj;
						mSearchResults = tempSearchResults;
				
						if(mSearchResults.getResultsList().size() == 0) {
							resetBackToSearch();
							Toast.makeText(SearchActivity.this, "No matches found", Toast.LENGTH_SHORT).show();
						}
				
						mSearchTerm = mSearchResults.getSearchTerm();
						showSummaryView();
						
						if(supportsMoreResult() && mSearchResults.isPartialResult()) {
							mSearchListView.addFooterView(mLoadMore);
						}
						mSearchListView.setAdapter(getListAdapter(mSearchResults));
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
			mLoadingView.showLoading();			

			mResultsDisplayed = false;
			mSearching = true;
			mResetSearchScheduled = false;
			mSearchTerm = searchTerm;
			initiateSearch(searchTerm, searchHandler(searchTerm));
		}
	}
	
	private void doContinueSearch() {
		mLoadMore.setEnabled(false);
		mLoadMore.setTitle(LOADING);
		mSearching = true;
		mLastFailedSearchTime = null;
		
		final SearchResults<ResultItem> currentSearchResults = mSearchResults;		
		continueSearch(currentSearchResults, new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				if(currentSearchResults == mSearchResults) {
					mLoadMore.setEnabled(true);
					if (msg.arg1 == MobileWebApi.SUCCESS) {
						final SearchResults<ResultItem> tempSearchResults = (SearchResults<ResultItem>) msg.obj;
						mSearchResults = tempSearchResults;
						mLoadMore.setTitle(LOAD_MORE);
						
						showSummaryView();
						if(!mSearchResults.isPartialResult()) {
							mSearchListView.removeFooterView(mLoadMore);
						}
					} else {
						mLoadMore.setTitle(TRY_AGAIN);
						mLastFailedSearchTime = System.currentTimeMillis();
					}
					// the list views are not properly responding to data being changed.
					// requesting layout seems to fix this (my only guess is the special
					// magic used for footerViews is buggy
					mSearching = false;
					mSearchListView.requestLayout();
				}
			}
		});
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
		mLoadingView = (FullScreenLoader) findViewById(R.id.searchResultsLoading);
		mLoadMore = new TwoLineActionRow(this);
		mLoadMore.setTitle(LOAD_MORE);
		
		mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(view == mLoadMore) {
					doContinueSearch();
				} else {
					@SuppressWarnings("unchecked")
					ResultItem item = (ResultItem) parent.getItemAtPosition(position);
					SearchActivity.this.onItemSelected(mSearchResults, item);
				}
			}
		});
		
		// add a scroll listener to retrieve more results prememptively
		if(supportsMoreResult()) {
			mSearchListView.setOnScrollListener(new OnScrollListener() { 
				private static final int MINIMUM_REMAINING_ROWS = 10;
			
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					continueSearchFromScroll();		
				}

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					continueSearchFromScroll();
				}
			
				private void continueSearchFromScroll() {
					if(mSearchResults != null && mSearchResults.isPartialResult()) {
						if(mSearchResults.getResultsList().size() - mSearchListView.getFirstVisiblePosition() <  MINIMUM_REMAINING_ROWS) {	
							if(!mSearching) {
								if((mLastFailedSearchTime == null) ||
										(mLastFailedSearchTime - System.currentTimeMillis() > MINIMUM_TRY_AGAIN_WAIT)) {
								
									doContinueSearch();
								}
							}
						}
					}
				}
			});
		}
		
		doSearch(getIntent());
	}
	
	private void showSummaryView() {
		String summaryText;
		int resultsCount = mSearchResults.getResultsList().size();
		if(resultsCount == 0) {
			summaryText = "No matches for \"" + mSearchTerm + "\"";
		} else if(resultsCount == 1) {
			summaryText = "Results for "+ mSearchTerm;
		} else if(!mSearchResults.isPartialResult() || supportsMoreResult()) {
			/*String totalCount;
			if (mSearchResults.totalResultsCount() != null) {
				totalCount = "" + mSearchResults.totalResultsCount();
			} else {
				totalCount = "" + mSearchResults.getResultsList().size();
			}
			summaryText = totalCount + " results";*/
			summaryText = "Results for "+ mSearchTerm;
		} else {
			if(mSearchResults.totalResultsCount() != null) {
				// total known
				summaryText = mSearchResults.totalResultsCount() + " ";
			} else {
				// total unknown
				summaryText = "Many ";
			}
			summaryText += searchItemPlural() + " found showing " + resultsCount;
		}
		
		mSearchResultsHeader.setText(summaryText);
		mSearchResultsHeader.setVisibility(View.VISIBLE);
	}
	
	private void resetBackToSearch() {
		if (hasWindowFocus()) {
			mResetSearchScheduled = false;
			mResultsDisplayed = false;
			startSearch(mSearchTerm, false, null, false);
		} else {
			mResetSearchScheduled = true;
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			if (mResetSearchScheduled) {
				resetBackToSearch();
			} else if(!mSearching && !mResultsDisplayed) {
				finish();
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}
	
	
	protected SearchResults<ResultItem> getSearchResults() {
		return mSearchResults;
	}
	
	
	protected ListView getListView() {
		return mSearchListView;
	}
	
	abstract protected String searchItemPlural();
	
	abstract protected String searchItemSingular();
	
	protected boolean supportsMoreResult() {
		return false;
	}
	
	protected void continueSearch(SearchResults<ResultItem> previousResults, Handler uiHandler) {}
	
	abstract protected void onItemSelected(SearchResults<ResultItem> results, ResultItem item);
	
	
}
