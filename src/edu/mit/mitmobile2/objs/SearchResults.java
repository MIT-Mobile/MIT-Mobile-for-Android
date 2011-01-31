package edu.mit.mitmobile2.objs;

import java.util.List;

public class SearchResults<ResultItem> {
	private List<ResultItem> mResultsList;
	private String mSearchTerm;
	
	// used if there are more total results than shown in the results Lists
	private Integer mTotalResultsCount = null;
	
	// used to indicate the result is only partial
	private boolean mIsPartialResult = false;
	
	public SearchResults(String searchTerm, List<ResultItem> resultsList) {
		mResultsList = resultsList;
		mSearchTerm = searchTerm;
	}

	public void markAsPartial(Integer totalResultsCount) {
		mTotalResultsCount = totalResultsCount;
		mIsPartialResult = true;
	}
	
	public List<ResultItem> getResultsList() {
		return mResultsList;
	}

	public String getSearchTerm() {
		return mSearchTerm;
	}
	
	public boolean isPartialResult() {
		return mIsPartialResult;
	}
	
	public Integer totalResultsCount() {
		return mTotalResultsCount;
	}
}
