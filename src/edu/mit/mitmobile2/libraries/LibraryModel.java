package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import edu.mit.mitmobile2.FixedCache;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.objs.SearchResults;

public class LibraryModel {
    public static String MODULE_LIBRARY = "libraries";
    private static HashMap<String, SearchResults<BookItem>> searchCache = new FixedCache<SearchResults<BookItem>>(10);
    
    public static void fetchLocationsAndHours(final Context context, final Handler uiHandler) {

        HashMap<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put("command", "locations");
        searchParameters.put("module", MODULE_LIBRARY);

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONArray(searchParameters, new MobileWebApi.JSONArrayResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONArray array) {
                ArrayList<LibraryItem> libraries = LibraryParser.parseLibrary(array);

                MobileWebApi.sendSuccessMessage(uiHandler, libraries);
            }
        });
    }
    
    
    
    public static void fetchLibraryDetail(final LibraryItem libraryItem, final Context context, final Handler uiHandler) {
        HashMap<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put("module", MODULE_LIBRARY);
        searchParameters.put("command", "locationDetail");
        searchParameters.put("library", libraryItem.library);

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONObject(searchParameters, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {
            @Override
            public void onResponse(JSONObject object) throws ServerResponseException, JSONException {
                LibraryParser.parseLibraryDetail(object, libraryItem);
                
                MobileWebApi.sendSuccessMessage(uiHandler, null);
            }
        });
    }
    
    public static void searchBooks(final String searchTerm, final Context context, final Handler uiHandler) {
        searchBooks(searchTerm, context, uiHandler, 0);
    }
    
    public static void searchBooks(final String searchTerm, final Context context, final Handler uiHandler, int startIndex) {
//        if (searchCache.containsKey(searchTerm)) {
//            MobileWebApi.sendSuccessMessage(uiHandler, searchCache.get(searchTerm));
//            return;
//        }
        
        HashMap<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put("command", "search");
        searchParameters.put("module", MODULE_LIBRARY);
        searchParameters.put("q", searchTerm);
        if(startIndex != 0) {
            searchParameters.put("startIndex", String.valueOf(startIndex));
        }
        
        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONObject(searchParameters, new MobileWebApi.JSONObjectResponseListener(
            new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(uiHandler) ) {
            
            @Override
            public void onResponse(JSONObject object) {
                try {
                    List<BookItem> books = LibraryParser.parseBooks(object.getJSONArray("items"));
                    SearchResults<BookItem> searchResults = new SearchResults<BookItem>(searchTerm, books);
                    
                    searchResults.setNextIndex(0);
                    if(object.has("nextIndex")) {
                        int nextIndex = object.getInt("nextIndex");
                        if(nextIndex > 0) {
                            searchResults.markAsPartial(null);
                        }
                        
                        searchResults.setNextIndex(nextIndex);
                    } 
                    
                    searchCache.put(searchTerm, searchResults);
                    
                    MobileWebApi.sendSuccessMessage(uiHandler, searchResults);              
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error parsing search results");
                }
                
            }
        });
        
        
    }
}
