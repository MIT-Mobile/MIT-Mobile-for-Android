package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.HttpClientType;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.classes.FineData;
import edu.mit.mitmobile2.classes.HoldData;
import edu.mit.mitmobile2.classes.LoanData;
import edu.mit.mitmobile2.classes.RenewBookResponse;
import edu.mit.mitmobile2.libraries.LibraryActivity.LinkItem;
import edu.mit.mitmobile2.objs.LoanListItem;

public class LibraryModel {
    public static String MODULE_LIBRARY = "libraries";
	public static final String TAG = "LibraryModel";


    // private static HashMap<String, SearchResults<BookItem>> searchCache = new
    // FixedCache<SearchResults<BookItem>>(10);

    public static void fetchLocationsAndHours(final Context context, final Handler uiHandler) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("command", "locations");
        parameters.put("module", MODULE_LIBRARY);

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONArray(parameters, new MobileWebApi.JSONArrayResponseListener(
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
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("module", MODULE_LIBRARY);
        parameters.put("command", "locationDetail");
        parameters.put("library", libraryItem.library);

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONObject(parameters, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {
            @Override
            public void onResponse(JSONObject object) throws ServerResponseException, JSONException {
                LibraryParser.parseLibraryDetail(object, libraryItem);

                MobileWebApi.sendSuccessMessage(uiHandler, null);
            }
        });
    }

    public static void searchBooks(final String searchTerm, boolean showErrors, final LibrarySearchResults previousResults,
            final Context context, final Handler uiHandler) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("command", "search");
        parameters.put("module", MODULE_LIBRARY);
        parameters.put("q", searchTerm);
        if (previousResults != null) {
            parameters.put("startIndex", String.valueOf(previousResults.getNextIndex()));
        }

        MobileWebApi webApi = new MobileWebApi(false, showErrors, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONObject(parameters, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONObject object) throws JSONException {
                List<BookItem> books = LibraryParser.parseBooks(object.getJSONArray("items"));

                LibrarySearchResults searchResults;
                if (previousResults == null) {
                    searchResults = new LibrarySearchResults(searchTerm, books);
                } else {
                    searchResults = previousResults;
                    searchResults.addMoreResults(books);
                }

                if (object.has("nextIndex")) {
                    searchResults.setNextIndex(object.getInt("nextIndex"));
                    searchResults.markAsPartialWithTotalCount(object.getInt("totalResultsCount"));
                } else {
                    searchResults.setNextIndex(null);
                    searchResults.markAsComplete();
                }

                // searchCache.put(searchTerm, searchResults);
                MobileWebApi.sendSuccessMessage(uiHandler, searchResults);
            }
        });

    }

    public static void fetchLinks(final Context context, final Handler uiHandler) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("command", "links");
        parameters.put("module", MODULE_LIBRARY);

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONArray(parameters, new MobileWebApi.JSONArrayResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONArray array) {
                ArrayList<LinkItem> links = LibraryParser.parseLinks(array);

                MobileWebApi.sendSuccessMessage(uiHandler, links);
            }
        });
    }

    public static void sendAskUsInfo(final Context context, final Handler uiHandler, String topic, String status,
            String department, String subject, String question, String description, String askType) {

        HashMap<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put("command", "sendAskUsEmail");
        searchParameters.put("topic", topic);
        searchParameters.put("status", status);
        searchParameters.put("department", department);
        searchParameters.put("subject", subject);
        searchParameters.put("question", question);
        searchParameters.put("description", description);
        searchParameters.put("ask_type", askType);
        searchParameters.put("module", MODULE_LIBRARY);

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONObject(searchParameters, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONObject object) throws JSONException {
                if (object.has("success") && object.getBoolean("success")) {
                    MobileWebApi.sendSuccessMessage(uiHandler, object.getJSONObject("results").getString("contents"));
                }
            }
        });
    }

    public static void sendTellUsInfo(final Context context, final Handler uiHandler, String status, String feedback) {

        HashMap<String, String> searchParameters = new HashMap<String, String>();
        searchParameters.put("command", "sendTellUsEmail");
        if (status != null && !"".equals(status)) {
            searchParameters.put("status", status);
        }
        searchParameters.put("feedback", feedback);
        searchParameters.put("module", MODULE_LIBRARY);

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONObject(searchParameters, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONObject object) throws JSONException {
                if (object.has("success") && object.getBoolean("success")) {
                    MobileWebApi.sendSuccessMessage(uiHandler, object.getJSONObject("results").getString("contents"));
                }
            }
        });
    }

    public static void sendAppointmentEmail(final Context context, final Handler uiHandler, String topic,
            String timeframe, String information, String purpose, String course, String researchTopic, String status,
            String department, String phonenumber) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("command", "sendAskUsEmail");
        parameters.put("subject", topic);
        parameters.put("timeframe", timeframe);
        parameters.put("description", information);
        parameters.put("why", purpose);
        parameters.put("topic", researchTopic);
        parameters.put("status", status);
        parameters.put("department", department);
        parameters.put("course", course);
        parameters.put("ask_type", "consultation");
        parameters.put("module", MODULE_LIBRARY);

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONObject(parameters, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONObject object) throws JSONException {
                if (object.has("success") && object.getBoolean("success")) {
                    MobileWebApi.sendSuccessMessage(uiHandler, object.getJSONObject("results").getString("contents"));
                }
            }
        });
    }

	public static void fetchLoanDetail(final Context context, final Handler uiHandler) {
		Log.d(TAG,"getLoanData()");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "libraries");
		params.put("command", "loans");

    	MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler,HttpClientType.MIT);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
    	webApi.requestJSONObject(params, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler),
                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
			@Override
			public void onResponse(JSONObject object) throws JSONException {
	              LoanData loanData = LibraryParser.parseLoans(object);
	                MobileWebApi.sendSuccessMessage(uiHandler, loanData);
	        }
    	});			
	}
    
	public static void fetchHoldDetail(final Context context, final Handler uiHandler) {
		Log.d(TAG,"getHoldData()");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "libraries");
		params.put("command", "holds");

    	MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler,HttpClientType.MIT);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
    	webApi.requestJSONObject(params, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler),
                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
			@Override
			public void onResponse(JSONObject object) throws JSONException {
	              HoldData holdData = LibraryParser.parseHolds(object);
	                MobileWebApi.sendSuccessMessage(uiHandler, holdData);
	        }
    	});			
	}

	public static void fetchFineDetail(final Context context, final Handler uiHandler) {
		Log.d(TAG,"getFineData()");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "libraries");
		params.put("command", "fines");

    	MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler,HttpClientType.MIT);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
    	webApi.requestJSONObject(params, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler),
                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
			@Override
			public void onResponse(JSONObject object) throws JSONException {
	              FineData fineData = LibraryParser.parseFines(object);
	                MobileWebApi.sendSuccessMessage(uiHandler, fineData);
	        }
    	});			
	}

    public static void renewBook(final Context context, final Handler uiHandler,String barcode) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("command", "renewBooks");
        parameters.put("module", MODULE_LIBRARY);
        parameters.put("barcodes", barcode);
        MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler,HttpClientType.MIT);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONArray(parameters, new MobileWebApi.JSONArrayResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONArray array) {
                Log.d(TAG,"renew response = " + array.toString());
            	RenewBookResponse renewBookResponse = LibraryParser.parseRenewBookResponse(array);
                MobileWebApi.sendSuccessMessage(uiHandler, renewBookResponse);
            }
        });
    }

	
}


