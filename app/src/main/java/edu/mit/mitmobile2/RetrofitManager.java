package edu.mit.mitmobile2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.mitmobile2.shuttles.model.MITShuttlePredictionWrapper;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRouteWrapper;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;
import edu.mit.mitmobile2.shuttles.model.MITShuttleVehiclesWrapper;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import timber.log.Timber;

public class RetrofitManager {

    //TODO: Add logic for switching endpoints
    public static final String PROD_ENDPOINT = "http://m.mit.edu/apis";
    public static final String DEV_ENDPOINT = "";
    public static final String TEST_ENDPOINT = "";

    public static HashMap<String, String> paths;
    public static HashMap<String, String> queries;

    public RetrofitManager() {
    }

    private static RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            if (paths != null) {
                for (Map.Entry<String, String> set : paths.entrySet()) {
                    request.addPathParam(set.getKey(), set.getValue());
                }
                paths.clear();
            }

            if (queries != null) {
                for (Map.Entry<String, String> set : queries.entrySet()) {
                    request.addQueryParam(set.getKey(), set.getValue());
                }
                queries.clear();
            }
        }
    };

    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) {
        Method m;
        String[] pathSections = path.split("/");
        String methodName = "get";

        paths = pathParams;
        queries = queryParams;

        for (int i = 2; i < pathSections.length; i++) {
            // Skip the first
            if (!pathSections[i].contains("{")) {
                methodName += pathSections[i];
            } else {
                methodName += "_";
            }
        }

        String serviceName = "MIT_" + apiType.toUpperCase() + "_SERVICE";
        try {
            Field f = RetrofitManager.class.getDeclaredField(serviceName);
            m = f.getType().getDeclaredMethod(methodName, Callback.class);
            Object o = f.get(Class.forName(f.getType().getName()));
            m.invoke(o, callback);
        } catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            Timber.e(e, "Reflection");
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
            .setRequestInterceptor(requestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

    // These require specific naming conventions: "MIT_(Module Name)_SERVICE"
    private static final MitShuttleService MIT_SHUTTLES_SERVICE = MBTA_RT_REST_ADAPTER.create(MitShuttleService.class);
    private static final MitResourcesService MIT_RESOURCES_SERVICE = MBTA_RT_REST_ADAPTER.create(MitResourcesService.class);

    public interface MitShuttleService {

        @GET(Constants.Shuttles.ALL_ROUTES_PATH)
        void getroutes(Callback<List<MITShuttleRouteWrapper>> callback);

        @GET(Constants.Shuttles.ROUTE_INFO_PATH)
        void getroutes_(Callback<MITShuttleRouteWrapper> callback);

        @GET(Constants.Shuttles.STOP_INFO_PATH)
        void getroutes_stops_(Callback<MITShuttleStopWrapper> callback);

        @GET(Constants.Shuttles.PREDICTIONS_PATH)
        void getpredictions(Callback<List<MITShuttlePredictionWrapper.Predictions>> callback);

        @GET(Constants.Shuttles.VEHICLES_PATH)
        void getvehicles(Callback<List<MITShuttleVehiclesWrapper>> callback);
    }

    public interface MitResourcesService {
        //TODO: Add resources calls here
    }
}
