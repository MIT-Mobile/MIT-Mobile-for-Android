package edu.mit.mitmobile2.libraries;

import android.app.Activity;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.libraries.model.MITLibrariesAskUsModel;
import edu.mit.mitmobile2.libraries.model.MITLibrariesLibrary;
import edu.mit.mitmobile2.libraries.model.MITLibrariesLink;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITIdentity;
import edu.mit.mitmobile2.libraries.model.MITLibrariesUser;
import edu.mit.mitmobile2.libraries.model.MITLibrariesWorldcatItem;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedString;

public class LibraryManager extends RetrofitManager {
    private static final MitLibraryService MIT_LIBRARY_SERVICE = MIT_REST_ADAPTER.create(MitLibraryService.class);
    private static final MitSecureService MIT_SECURE_SERVICE = MIT_REST_ADAPTER.create(MitSecureService.class);

    private static final int LIBRARY_ITEMS_SEARCH_LIMIT = 20;

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        if (apiType.equals(Constants.SECURE) && path.equals(Constants.Secure.SECURE_USER_PATH)) {
            Method m = findMethodViaDirectReflection(MitSecureService.class, path, pathParams, queryParams, Callback.class);
            LoggingManager.Timber.d("Method = " + m);

            m.invoke(MIT_SECURE_SERVICE, callback);
        } else {
            Method m = findMethodViaDirectReflection(MitLibraryService.class, path, pathParams, queryParams, Callback.class);
            LoggingManager.Timber.d("Method = " + m);

            m.invoke(MIT_LIBRARY_SERVICE, callback);
        }
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitLibraryService.class, path, pathParams, queryParams);
        LoggingManager.Timber.d("Method = " + m);
        return m.invoke(MIT_LIBRARY_SERVICE);
    }

    /* GET requests */

    public static LibraryManagerCall getLinks(Activity activity, Callback<List<MITLibrariesLink>> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        returnValue.getClient().get(Constants.LIBRARIES, Constants.Libraries.LIBRARIES_LINKS_PATH, null, null, returnValue);

        return returnValue;
    }

    public static LibraryManagerCall getLibraries(Activity activity, Callback<List<MITLibrariesLibrary>> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        returnValue.getClient().get(Constants.LIBRARIES, Constants.Libraries.LIBRARIES_LOCATIONS_PATH, null, null, returnValue);

        return returnValue;
    }

    public static LibraryManagerCall getAskUsTopics(Activity activity, Callback<MITLibrariesAskUsModel> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        returnValue.getClient().get(Constants.SECURE, Constants.Secure.SECURE_LIBRARIES_ASK_US, null, null, returnValue);

        return returnValue;
    }

    public static LibraryManagerCall search(Activity activity, String query, int startingIndex, Callback<List<MITLibrariesWorldcatItem>> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("offset", String.format("%d", startingIndex));
        queryParams.put("q", TextUtils.isEmpty(query) ? "" : query);
        queryParams.put("limit", String.format("%d", LIBRARY_ITEMS_SEARCH_LIMIT));

        returnValue.getClient().get(Constants.LIBRARIES, Constants.Libraries.LIBRARIES_WORLDCATS_PATH, null, queryParams, returnValue);

        return returnValue;
    }

    public static LibraryManagerCall getItemDetails(Activity activity, MITLibrariesWorldcatItem item, Callback<MITLibrariesWorldcatItem> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        HashMap<String, String> pathParams = new HashMap<>();
        pathParams.put("itemId", item.getIdentifier());

        returnValue.getClient().get(Constants.LIBRARIES, Constants.Libraries.LIBRARIES_WORLDCAT_PATH, pathParams, null, returnValue);

        return returnValue;
    }

    public static LibraryManagerCall getUser(Activity activity, Callback<MITLibrariesUser> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        returnValue.getClient().get(Constants.SECURE, Constants.Secure.SECURE_LIBRARIES_ACCOUNT_PATH, null, null, returnValue);

        return returnValue;
    }

    public static LibraryManagerCall getIdentity(Activity activity, Callback<MITLibrariesMITIdentity> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        returnValue.getClient().get(Constants.SECURE, Constants.Secure.SECURE_USER_PATH, null, null, returnValue);

        return returnValue;
    }

    public static LibraryManagerCall getLoginAuth(Activity activity, Callback<Response> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        returnValue.getClient().get(Constants.SECURE, Constants.Secure.SECURE_USER_PATH, null, null, returnValue);

        return returnValue;
    }

    public static LibraryManagerCall loginUser(Activity activity, Callback<Response> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        returnValue.getClient().get(Constants.LOGIN, "/", null, null, returnValue);

        return returnValue;
    }

    public static void postLoginToIdp(TypedString body, Callback<Response> callback) {
        MIT_SECURE_SERVICE._postloginuser(body, callback);
    }

    public static void postAuthToShibboleth(TypedString body, Callback<Response> callback) {
        MIT_SECURE_SERVICE._postloginuser2(body, callback);
    }


    public static void setUsernameAndPassword(String username, String password) {
        RetrofitManager.userName = username;
        RetrofitManager.password = password;
    }

    /* POST requests */

    public interface MitLibraryService {
        @GET(Constants.Libraries.LIBRARIES_LINKS_PATH)
        void _getlinks(Callback<List<MITLibrariesLink>> callback);

        @GET(Constants.Libraries.LIBRARIES_LOCATIONS_PATH)
        void _getlibraries(Callback<List<MITLibrariesLibrary>> callback);

        @GET(Constants.Secure.SECURE_LIBRARIES_ASK_US)
        void _getaskustopics(Callback<MITLibrariesAskUsModel> callback);

        @GET(Constants.Libraries.LIBRARIES_WORLDCATS_PATH)
        void _getsearch(Callback<List<MITLibrariesWorldcatItem>> callback);

        @GET(Constants.Libraries.LIBRARIES_WORLDCAT_PATH)
        void _getitemdetail(Callback<MITLibrariesWorldcatItem> callback);

        @GET(Constants.Secure.SECURE_LIBRARIES_ACCOUNT_PATH)
        void _getaccount(Callback<MITLibrariesUser> callback);

        @GET(Constants.Secure.SECURE_USER_PATH)
        void _getuser(Callback<MITLibrariesMITIdentity> callback);
    }

    public interface MitSecureService {
        @Headers({
                "Accept: application/vnd.paos+xml,*/*",
                "PAOS: ver=\"urn:liberty:paos:2003-08\"; \"urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp\";"
        })
        @GET(Constants.Secure.SECURE_USER_PATH)
        void _getsecure(Callback<Response> callback);

        @Headers({
                "Content-Type: application/vnd.paos+xml"
        })
        @POST("/idp/profile/SAML2/SOAP/ECP")
        void _postloginuser(@Body TypedString obj, Callback<Response> callback);

        @Headers({
                "Content-Type: application/vnd.paos+xml"
        })
        @POST("/SAML2/ECP")
        void _postloginuser2(@Body TypedString obj, Callback<Response> callback);
    }

    public static class LibraryManagerCallWrapper<T> extends MITAPIClient.ApiCallWrapper<T> implements LibraryManagerCall, Callback<T> {
        public LibraryManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
            super(client, callback);
        }
    }

    public interface LibraryManagerCall extends MITAPIClient.ApiCall {
    }
}
