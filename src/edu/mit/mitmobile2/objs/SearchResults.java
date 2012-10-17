package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.List;

public class SearchResults<ResultItem> {
	private ArrayList<ResultItem> mResultsList = new ArrayList<ResultItem>();
	private String mSearchTerm;
	
	// used if there are more total results than shown in the results Lists
	private Integer mTotalResultsCount = null;
	
	// used to indicate the result is only partial
	private boolean mIsPartialResult = false;
	
	public SearchResults(String searchTerm, List<ResultItem> resultsList) {
		mResultsList.addAll(resultsList);
		mSearchTerm = searchTerm;
	}

	public void markAsComplete() {
		mIsPartialResult = false;
	}
	
	public void markAsPartialWithUnknownTotal() {
		mTotalResultsCount = null;
		mIsPartialResult = true;
	}
	
	public void markAsPartialWithTotalCount(Integer totalResultsCount) {
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
	
	public int getItemPosition(ResultItem item) {
		return mResultsList.indexOf(item);
	}
	
	public void addMoreResults(List<ResultItem> moreResults) {
		mResultsList.addAll(moreResults);
		if (mTotalResultsCount != null) {
			if (mResultsList.size() == mTotalResultsCount) {
				mIsPartialResult = false;
			}
		}
	}
}
