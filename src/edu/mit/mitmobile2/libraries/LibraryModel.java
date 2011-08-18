package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.objs.SearchResults;

public class LibraryModel {
    private static int MAX_RESULTS = 100;

    public static void executeSearch(final Context context, final Handler uiHandler) {

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
                ArrayList<LibraryItem> libraries = new ArrayList<LibraryItem>();

                try {
                    for (int index = 0; index < array.length(); index++) {
                        JSONObject object = array.getJSONObject(index);
                        LibraryItem library = new LibraryItem();
                        library.library = object.getString("library");
                        library.status = object.getString("status");

                        libraries.add(library);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SearchResults<LibraryItem> searchResults = new SearchResults<LibraryItem>(null, libraries);
                if (searchResults.getResultsList().size() >= MAX_RESULTS) {
                    searchResults.markAsPartial(null);
                }

                MobileWebApi.sendSuccessMessage(uiHandler, searchResults);
            }
        });
    }
}
