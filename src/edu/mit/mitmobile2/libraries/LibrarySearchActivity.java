package edu.mit.mitmobile2.libraries;

import android.os.Handler;
import android.widget.ArrayAdapter;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchActivity;
import edu.mit.mitmobile2.objs.SearchResults;

public class LibrarySearchActivity extends SearchActivity<BookItem> {

    @Override
    protected ArrayAdapter<BookItem> getListAdapter(SearchResults<BookItem> results) {
        BookListAdapter recentlyViewedListAdapter = new BookListAdapter(this, results.getResultsList(), R.layout.boring_action_row);
        recentlyViewedListAdapter.setLookupHandler(mSearchListView, results.getSearchTerm());
        return recentlyViewedListAdapter;
    }

    @Override
    protected String getSuggestionsAuthority() {
        return LibrarySearchSuggestionsProvider.AUTHORITY;
    }

    @Override
    protected void initiateSearch(String searchTerm, Handler uiHandler) {
        LibraryModel.searchBooks(searchTerm, this, uiHandler);
        
    }

    @Override
    protected String searchItemPlural() {
        return "books";
    }

    @Override
    protected String searchItemSingular() {
        return "book";
    }

    @Override
    protected Module getModule() {
        return new LibraryModule();
    }

}
