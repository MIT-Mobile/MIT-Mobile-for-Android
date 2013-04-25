package edu.mit.mitmobile2.news;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.SearchActivity;
import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.SearchResults;

public class NewsSearchActivity extends SearchActivity<NewsItem> {

	protected NewsModel mNewsModel;
	private String mSearchTerm;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mNewsModel = new NewsModel(this);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected ArrayAdapter<NewsItem> getListAdapter(final SearchResults<NewsItem> results) {
		List<NewsItem> newsItems = results.getResultsList();		
		return new NewsArrayAdapter(this, 0, newsItems, new NewsModel(this), getListView());
	}

	@Override
	protected String getSuggestionsAuthority() {
		return NewsSearchSuggestionsProvider.AUTHORITY;
	}

	@Override
	protected void initiateSearch(String searchTerm, Handler uiHandler) {
		mSearchTerm = searchTerm;
		mNewsModel.executeSearch(searchTerm, uiHandler, 0);
	}

	@Override
	protected String searchItemPlural() {
		return "stories";
	}

	@Override
	protected String searchItemSingular() {
		return "story";
	}

    @Override
    protected boolean supportsMoreResult() {
        return true;
    }

	@Override
	protected void onItemSelected(SearchResults<NewsItem> results, NewsItem item) {
		Intent intent = new Intent(NewsSearchActivity.this, NewsDetailsActivity.class);
		intent.putExtra(NewsDetailsActivity.KEY_POSITION, results.getItemPosition(item));
		intent.putExtra(NewsDetailsActivity.SEARCH_TERM_KEY, results.getSearchTerm());
		startActivity(intent);
		
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new NewsModule();
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

	@Override
	protected void continueSearch(SearchResults<NewsItem> previousResults,
			Handler uiHandler) {
		// TODO Auto-generated method stub
		mNewsModel.executeSearch(mSearchTerm, uiHandler, previousResults.getResultsList().size());
	}
}
