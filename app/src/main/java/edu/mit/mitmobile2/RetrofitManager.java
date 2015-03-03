package edu.mit.mitmobile2;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import timber.log.Timber;

public class RetrofitManager {
    public static final String PROD_ENDPOINT = "http://m.mit.edu/apis";
    public static final String DEV_ENDPOINT = "";
    public static final String TEST_ENDPOINT = "";

//    private HashMap<String, BaseService> servicesMap = new HashMap<>();

    public static HashMap<String, String> paths;
    public static HashMap<String, String> queries;

    public RetrofitManager() {
//        servicesMap.put(Constants.RESOURCES, MIT_RESOURCES_SERVICE);
//        servicesMap.put(Constants.SHUTTLES, MIT_SHUTTLE_SERVICE);
    }

    public void reflectOnObjects(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) {

        // path = "/shuttles/routes";
        String[] pathSections = path.split("/");
        String methodName = "get";

        paths = pathParams;
        queries = queryParams;

        for (int i = 1; i < pathSections.length; i++) {
            // Skip the first
            if (!pathSections[i].contains("{")) {
                methodName += pathSections[i];
            }
        }

        try {
            Method m = MIT_SHUTTLE_SERVICE.getClass().getDeclaredMethod(methodName, Callback.class);
            m.setAccessible(true);
            m.invoke(MIT_SHUTTLE_SERVICE, callback);
        } catch (NoSuchMethodException e) {
            Log.e("A", e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("B", e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("C", e.getMessage());
        }
    }


    private static RestAdapter MBTA_RT_REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(PROD_ENDPOINT)
            .setLog(new RestAdapter.Log() {
                @Override
                public void log(String message) {
                    Timber.d(message);
                }
            })
            .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    if (paths != null) {
                        for (Map.Entry<String, String> set : paths.entrySet()) {
                            request.addPathParam(set.getKey(), set.getValue());
                        }
                    }

                    if (queries != null) {
                        for (Map.Entry<String, String> set : queries.entrySet()) {
                            request.addQueryParam(set.getKey(), set.getValue());
                        }
                    }
                }
            })
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

    private static final MitShuttleService MIT_SHUTTLE_SERVICE = MBTA_RT_REST_ADAPTER.create(MitShuttleService.class);
    private static final MitResourcesService MIT_RESOURCES_SERVICE = MBTA_RT_REST_ADAPTER.create(MitResourcesService.class);


    public interface MitShuttleService {

        //TODO: Add Callbacks

        @GET("/shuttles/routes")
        void getroutes();

        @GET("/shuttles/routes/{route}")
        void getrouteDetail(@Path("route") String route);

        @GET("/shuttles/routes/{route}/stops/{stop}")
        void getroutestops(@Path("route") String route, @Path("stop") String stop);

//        @GET("/shuttles/predictions")
//        void getpredictions(@Query("agency") String agency, @Query("stop_number") String stopNumber, @Query("stops") String stops);

        @GET("/shuttles/predictions")
        void getpredictions(Callback<Response> callback);

        @GET("/shuttles/vehicles")
        void getvehicles(@Query("agency") String agency, @Query("routes") String routes);
    }

    public interface MitResourcesService {

    }
}
