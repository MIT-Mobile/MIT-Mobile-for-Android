package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;

public class LibraryModel {
    public static void fetchLocationsAndHours(final Context context, final Handler uiHandler) {

        HashMap<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put("command", "locations");
        searchParameters.put("module", "libraries");

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONArray(searchParameters, new MobileWebApi.JSONArrayResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONArray array) {
                ArrayList<LibraryItem> libraries = LibraryParser.parseLibrary(array);

//                SearchResults<LibraryItem> searchResults = new SearchResults<LibraryItem>(null, libraries);
//                if (searchResults.getResultsList().size() >= MAX_RESULTS) {
//                    searchResults.markAsPartial(null);
//                }

                MobileWebApi.sendSuccessMessage(uiHandler, libraries);
            }
        });
    }
    
    
    
    public static void fetchLibraryDetail(final LibraryItem libraryItem, final Context context, final Handler uiHandler) {
        HashMap<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put("module", "libraries");
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
}
