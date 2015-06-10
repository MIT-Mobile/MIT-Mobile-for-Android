package edu.mit.mitmobile2.facilities;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.facilities.model.FacilitiesCategory;
import edu.mit.mitmobile2.libraries.model.MITLibrariesLink;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.http.GET;

public class FacilitiesManager extends RetrofitManager {
    private static final MitFacilityService MIT_FACILITY_SERVICE = MIT_REST_ADAPTER.create(MitFacilityService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitFacilityService.class, path, pathParams, queryParams, Callback.class);
        LoggingManager.Timber.d("Method = " + m);
        m.invoke(MIT_FACILITY_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitFacilityService.class, path, pathParams, queryParams);
        LoggingManager.Timber.d("Method = " + m);
        return m.invoke(MIT_FACILITY_SERVICE);
    }

    /* GET requests */

    public static FacilityManagerCall getLocationCategories(Activity activity, Callback<HashMap<String, FacilitiesCategory>> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        returnValue.getClient().get(Constants.FACILITIES, Constants.Facilities.FACILITIES_LOCATION_CATEGORIES_PATH, null, null, returnValue);

        return returnValue;
    }

    public static FacilityManagerCall getProblemTypes(Activity activity, Callback<List<String>> callback) {
        LibraryManagerCallWrapper<?> returnValue = new LibraryManagerCallWrapper<>(new MITAPIClient(activity), callback);

        returnValue.getClient().get(Constants.FACILITIES, Constants.Facilities.FACILITES_PROBLEM_TYPES, null, null, returnValue);

        return returnValue;
    }

    /* POST requests */

    public interface MitFacilityService {
        @GET(Constants.Facilities.FACILITIES_LOCATION_CATEGORIES_PATH)
        void _getfacilities(Callback<HashMap<String, FacilitiesCategory>> callback);

        @GET(Constants.Facilities.FACILITES_PROBLEM_TYPES)
        void _getproblemtypes(Callback<List<String>> callback);

    }

    public static class LibraryManagerCallWrapper<T> extends MITAPIClient.ApiCallWrapper<T> implements FacilityManagerCall, Callback<T> {
        public LibraryManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
            super(client, callback);
        }
    }

    public interface FacilityManagerCall extends MITAPIClient.ApiCall {
    }
}
