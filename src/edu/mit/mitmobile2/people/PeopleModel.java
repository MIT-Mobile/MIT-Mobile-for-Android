package edu.mit.mitmobile2.people;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import edu.mit.mitmobile2.FixedCache;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.objs.PersonItem;
import edu.mit.mitmobile2.objs.SearchResults;

import android.content.Context;
import android.os.Handler;

public class PeopleModel {
	
	private static int MAX_RESULTS = 100;
	
	private static HashMap<String, SearchResults<PersonItem>> searchCache = new FixedCache<SearchResults<PersonItem>>(10);
	
	public static void executeSearch(final String searchTerm, Context context, final Handler uiHandler) {
		if(searchCache.containsKey(searchTerm)) {
			MobileWebApi.sendSuccessMessage(uiHandler, searchCache.get(searchTerm)); 
			return;
		}
		
		HashMap<String, String> searchParameters = new HashMap<String, String>();
		searchParameters.put("module", "people");
		searchParameters.put("q", searchTerm);

		MobileWebApi webApi = new MobileWebApi(false, true, "People", context, uiHandler);
		webApi.setIsSearchQuery(true);
		webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
		webApi.requestJSONArray(searchParameters, new MobileWebApi.JSONArrayResponseListener(
			new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(uiHandler) ) {
			
			@Override
			public void onResponse(JSONArray array) {
				List<PersonItem> people = PeopleParser.parseJSONArray(array);
				
				SearchResults<PersonItem> searchResults = new SearchResults<PersonItem>(searchTerm, people);
				if(searchResults.getResultsList().size() >= MAX_RESULTS) {
					searchResults.markAsPartialWithUnknownTotal();
				}

				searchCache.put(searchTerm, searchResults);
				
				MobileWebApi.sendSuccessMessage(uiHandler, searchResults);				
			}
		});
		
	}
	
	public static List<PersonItem>executeLocalSearch(String searchTerms) {
		return searchCache.get(searchTerms).getResultsList();
	}
	
	public static int getPosition(List<PersonItem> people, String uid) {
		for(int index = 0; index < people.size(); index++) {
			if(people.get(index).uid.equals(uid)) {
				return index;
			}
		}
		return -1;
	}
	
	public static void markAsRecentlyViewed(PersonItem person, Context context) {
		person.lastViewed = new Date();
		PeopleDB.getInstance(context).addPerson(person);
	}
	
	public static List<PersonItem> getRecentlyViewed(Context context) {
		List<PersonItem> people = PeopleDB.getInstance(context).getAllAsList();
		return people;		
	}
}
