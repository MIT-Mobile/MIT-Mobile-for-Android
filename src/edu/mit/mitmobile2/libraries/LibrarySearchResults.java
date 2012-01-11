package edu.mit.mitmobile2.libraries;

import java.util.List;

import edu.mit.mitmobile2.objs.SearchResults;

public class LibrarySearchResults extends SearchResults<BookItem> {

	private Integer mNextIndex = null;
	public LibrarySearchResults(String searchTerm, List<BookItem> resultsList) {
		super(searchTerm, resultsList);
	}
	
	public void setNextIndex(Integer nextIndex) {
		mNextIndex = nextIndex;
	}

	public Integer getNextIndex() {
		return mNextIndex;
	}
}
