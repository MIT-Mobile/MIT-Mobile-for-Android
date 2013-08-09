package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.ConnectionWrapper.ErrorType;
import edu.mit.mitmobile2.MobileWebApi.HttpClientType;
import edu.mit.mitmobile2.MobileWebApi.JSONObjectResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.MobileWebApi.DefaultCancelRequestListener;
import edu.mit.mitmobile2.MobileWebApi.DefaultErrorListener;

import edu.mit.mitmobile2.libraries.LibraryActivity.LinkItem;

public class LibraryModel {
    public static String MODULE_LIBRARY = "libraries";
	public static final String TAG = "LibraryModel";

	private static final String BASE_PATH = "/libraries";
	private static final String LINKS_PATH = "/links";
	private static final String LOCATIONS_PATH = "/locations";
	private static final String WORLDCAT_PATH = "/worldcat";
	
	private static final String LOANS_PATH = "/account/loans";
	private static final String HOLDS_PATH = "/account/holds";
	private static final String FINES_PATH = "/account/fines";
	private static final String SECURE_PATH = "/secure";
	
	private static UserIdentity identity = null;

    // private static HashMap<String, SearchResults<BookItem>> searchCache = new
    // FixedCache<SearchResults<BookItem>>(10);

    public static void fetchLocationsAndHours(final Context context, final Handler uiHandler) {
        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.requestJSONArray(BASE_PATH + LOCATIONS_PATH, null, new MobileWebApi.JSONArrayResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONArray array) throws JSONException {
                ArrayList<LibraryItem> libraries = LibraryParser.parseLibrary(array);

                MobileWebApi.sendSuccessMessage(uiHandler, libraries);
            }
        });
    }

    public static void fetchLibraryDetail(final LibraryItem libraryItem, final Context context, final Handler uiHandler) {
        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.requestJSONObject(BASE_PATH + LOCATIONS_PATH + "/" + libraryItem.library.replace(" ", "%20"), 
        		null, new MobileWebApi.JSONObjectResponseListener(
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

        HashMap<String, String> parameters = null;
        parameters = new HashMap<String, String>();
        parameters.put("q", searchTerm);
        if (previousResults != null) {
            parameters.put("startIndex", String.valueOf(previousResults.getNextIndex()));
        }
        
        String requestUrl = BASE_PATH + WORLDCAT_PATH;
        
        MobileWebApi webApi = new MobileWebApi(false, showErrors, "Library", context, uiHandler);
        webApi.setIsSearchQuery(true);
        webApi.setLoadingDialogType(MobileWebApi.LoadingDialogType.Search);
        webApi.requestJSONObject(requestUrl, parameters, 
        		new MobileWebApi.JSONObjectResponseListener(
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
    
    public static void fetchWorldCatBookDetails(final BookItem book, Context context, final Handler uiHandler) {
    	String requestUrl = BASE_PATH + WORLDCAT_PATH + "/" + book.id;
    	
    	MobileWebApi webApi = new MobileWebApi(false, true, "Book Details", context, uiHandler);
    	webApi.requestJSONObject(requestUrl, null, 
    			new JSONObjectResponseListener(
    		new DefaultErrorListener(uiHandler), new DefaultCancelRequestListener(uiHandler)) {
				@Override
				public void onResponse(JSONObject object) throws ServerResponseException, JSONException {
					LibraryParser.parseBookDetail(object, book);
					MobileWebApi.sendSuccessMessage(uiHandler);
				}
    		}
    	);
    }

    public static void fetchLinks(final Context context, final Handler uiHandler) {
        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler);
        webApi.requestJSONArray(BASE_PATH + LINKS_PATH, null, new MobileWebApi.JSONArrayResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONArray array) {
                ArrayList<LinkItem> links = LibraryParser.parseLinks(array);

                MobileWebApi.sendSuccessMessage(uiHandler, links);
            }
        });
    }

    public static class FormResult {
    	private String mEmailContent;
    	private String mThankYouText;
    	private String mContactAddress;
    	
    	public static FormResult factory(JSONObject json) throws JSONException {
    		JSONObject results = json.getJSONObject("results");
    		return new FormResult(
    				results.getString("contents"),
    				results.getString("thank_you_text"),
    				results.getString("email")
    		);
    	}
    	
    	public FormResult(String emailContent, String thankYouText, String contactAddress) {
    		mEmailContent = emailContent;
        	mThankYouText = thankYouText;
        	mContactAddress = contactAddress;
    	}
    	
    	public String getContent() {
    		return mEmailContent;
    	}
    	
    	public String getContactAddress() {
    		return mContactAddress;
    	}
    	
    	public String getThankYouText() {
    		return mThankYouText;
    	}
    	
    	public String getFeedbackString() {
    		return mThankYouText + "\n\nYou will be contacted at " + mContactAddress + ".";
    	}
    }

    public static void sendAskUsInfo(final Context context, final Handler uiHandler, String topic, String status,
            String department, String subject, String question, String phone, String usingVPN, String onCampus, String askType) {
    	
    	List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(7);
    	nameValuePairs.add(new BasicNameValuePair("topic", topic));
    	nameValuePairs.add(new BasicNameValuePair("status", status));
    	nameValuePairs.add(new BasicNameValuePair("department", department));
    	nameValuePairs.add(new BasicNameValuePair("subject", subject));
    	nameValuePairs.add(new BasicNameValuePair("question", question));
    	nameValuePairs.add(new BasicNameValuePair("ask_type", askType));
    	if (phone != null) {
    		nameValuePairs.add(new BasicNameValuePair("phone", phone));
        }
        if (usingVPN != null) {
        	nameValuePairs.add(new BasicNameValuePair("vpn", usingVPN));
        }
        if (onCampus != null) {
        	nameValuePairs.add(new BasicNameValuePair("on_campus", onCampus));
        }
	    
	    
        MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler, HttpClientType.MIT); 
        webApi.requestJSONObject(nameValuePairs, "POST", SECURE_PATH + BASE_PATH + "/forms/askUs", null, new MobileWebApi.JSONObjectResponseListener(
        		new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
        	                        uiHandler)) {

            @Override
            public void onResponse(JSONObject object) throws JSONException {
                if (object.has("success") && object.getBoolean("success")) {
                    MobileWebApi.sendSuccessMessage(uiHandler, FormResult.factory(object));
                }
            }
            
        });
    }
    
    public static void sendTellUsInfo(final Context context, final Handler uiHandler, String status, String feedback) {
      
    	List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(7);
        if (status != null && !"".equals(status)) {
        	nameValuePairs.add(new BasicNameValuePair("status", status));
        }
    	nameValuePairs.add(new BasicNameValuePair("feedback", feedback));

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler, HttpClientType.MIT);
        webApi.requestJSONObject(nameValuePairs, "POST", SECURE_PATH + BASE_PATH + "/forms/tellUs", null,
        		new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONObject object) throws JSONException {
                if (object.has("success") && object.getBoolean("success")) {
                    MobileWebApi.sendSuccessMessage(uiHandler, FormResult.factory(object));
                }
            }
        });
    }

    public static void sendAppointmentEmail(final Context context, final Handler uiHandler, String topic,
            String timeframe, String information, String purpose, String course, String researchTopic, String status,
            String department, String phonenumber) {

    	List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(7);
    	nameValuePairs.add(new BasicNameValuePair("subject", topic));
    	nameValuePairs.add(new BasicNameValuePair("timeframe", timeframe));
    	nameValuePairs.add(new BasicNameValuePair("description", information));
    	nameValuePairs.add(new BasicNameValuePair("why", purpose));
    	nameValuePairs.add(new BasicNameValuePair("topic", researchTopic));
    	nameValuePairs.add(new BasicNameValuePair("status", status));
    	nameValuePairs.add(new BasicNameValuePair("department", department));
    	nameValuePairs.add(new BasicNameValuePair("course", course));
    	nameValuePairs.add(new BasicNameValuePair("ask_type", "consultation"));

        MobileWebApi webApi = new MobileWebApi(false, true, "Library", context, uiHandler, HttpClientType.MIT);
        webApi.requestJSONObject(nameValuePairs, "POST", SECURE_PATH + BASE_PATH + "/forms/askUs", null,
        		new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONObject object) throws JSONException {
                if (object.has("success") && object.getBoolean("success")) {
                    MobileWebApi.sendSuccessMessage(uiHandler, FormResult.factory(object));
                }
            }
        });
    }

    public static class UserIdentity {
    	String mShibIdentity;
    	String mUsername;
    	boolean mIsMITIdentity;
    	
    	public UserIdentity(String shibIdentity, String username, boolean isMITIdentity) {
    		mShibIdentity = shibIdentity;
    		mUsername = username;
    		mIsMITIdentity = isMITIdentity;
    	}
    	
    	public String getShibIdentity() {
    		return mShibIdentity;
    	}
    	
    	public String getUsername() {
    		return mUsername;
    	}
    	
    	public boolean isMITIdentity() {
    		return mIsMITIdentity;
    	}
    }
    
    public static void getUserIdentity(final Context context, final Handler uiHandler) {
    	String requestUrl = "/secure/apps/user";
    	MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler, HttpClientType.MIT); 
    	webApi.requestJSONObject(requestUrl, null, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONObject object) throws JSONException {
            	identity = new UserIdentity(
            		object.optString("shib_identity"),
            		object.optString("username"),
            		object.optBoolean("is_mit_identity")
            	);
            	MobileWebApi.sendSuccessMessage(uiHandler, identity);
            }

         });
    }
    
	public static void fetchLoanDetail(final Context context, final Handler uiHandler) {
		String requestUrl = SECURE_PATH + BASE_PATH + LOANS_PATH;

    	MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler,HttpClientType.MIT);
    	webApi.requestJSONObject(requestUrl, null, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler),
                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
			@Override
			public void onResponse(JSONObject object) throws JSONException {
	            Log.d(TAG,"onResponse()");  
				LoanData loanData = LibraryParser.parseLoans(object);
	              if (loanData.isRequestCancelled()) {
		                MobileWebApi.sendCancelMessage(uiHandler);	            	  
	              }
	              else {
	                MobileWebApi.sendSuccessMessage(uiHandler, loanData);
	              } 	               
	        }

			@Override
			public void onError() {
				Log.d(TAG,"onError()");
			}
			
			
    	});			
	}
    
	public static void fetchHoldDetail(final Context context, final Handler uiHandler) {
		Log.d(TAG,"getHoldData()");
		String url = SECURE_PATH + BASE_PATH + HOLDS_PATH;

    	MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler,HttpClientType.MIT);
    	webApi.requestJSONObject(url, null, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler),
                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
			@Override
			public void onResponse(JSONObject object) throws JSONException {
	              HoldData holdData = LibraryParser.parseHolds(object);
	              if (holdData.isRequestCancelled()) {
		                MobileWebApi.sendCancelMessage(uiHandler);	            	  
	              }
	              else {
	                MobileWebApi.sendSuccessMessage(uiHandler, holdData);
	              } 	               
	        }
    	});			
	}

	public static void fetchFineDetail(final Context context, final Handler uiHandler) {
		Log.d(TAG,"getFineData()");
		String url = SECURE_PATH + BASE_PATH + FINES_PATH;
		
    	MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler,HttpClientType.MIT);
    	webApi.requestJSONObject(url, null, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler),
                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
			@Override
			public void onResponse(JSONObject object) throws JSONException {
	              FineData fineData = LibraryParser.parseFines(object);
	              if (fineData.isRequestCancelled()) {
		                MobileWebApi.sendCancelMessage(uiHandler);	            	  
	              }
	              else {
	                MobileWebApi.sendSuccessMessage(uiHandler, fineData);
	              } 	               
	        }
    	});			
	}

    public static void renewBook(final Context context, final Handler uiHandler,String barcode) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("command", "renewBooks");
        parameters.put("module", "libraries");
        parameters.put("barcodes", barcode);
        MobileWebApi webApi = new MobileWebApi(false, true, "Libraries", context, uiHandler,HttpClientType.MIT);
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


