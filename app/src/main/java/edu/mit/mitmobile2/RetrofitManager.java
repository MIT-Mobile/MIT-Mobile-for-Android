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
import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import timber.log.Timber;

public class RetrofitManager {

    private static class MitEndpoint implements Endpoint {

        public static MitEndpoint create() {
            return new MitEndpoint();
        }

        public MitEndpoint() {
        }

        private String url;

        public void setUrl(String url) {
            // Remove the last backslash because retrofit also requires a backslash at the beginning of the HTTP call path
            String modifiedUrl = url.substring(0, url.length() - 1);
            this.url = modifiedUrl;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getName() {
            return "default";
        }
    }

    private static HashMap<String, String> paths;
    private static HashMap<String, String> queries;

    private static MitEndpoint mitEndpoint = MitEndpoint.create();

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

    private static RestAdapter MIT_REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(mitEndpoint)
            .setLog(new RestAdapter.Log() {
                @Override
                public void log(String message) {
                    Timber.d(message);
                }
            })
            .setRequestInterceptor(requestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

    // These require specific naming conventions: "MIT_(MODULENAME)_SERVICE"
    private static final MitShuttleService MIT_SHUTTLES_SERVICE = MIT_REST_ADAPTER.create(MitShuttleService.class);
    private static final MitResourcesService MIT_RESOURCES_SERVICE = MIT_REST_ADAPTER.create(MitResourcesService.class);

    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) {
        Method m;
        String[] pathSections = path.split("/");
        String methodName = "get";

        paths = pathParams;
        queries = queryParams;

        for (int i = 1; i < pathSections.length; i++) {
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

    public static void changeEndpoint(String url) {
        if (mitEndpoint.getUrl() != null) {
            if (!(mitEndpoint.getUrl() + "/").equals(url)) {
                mitEndpoint.setUrl(url);
            }
        } else {
            mitEndpoint.setUrl(url);
        }
    }

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
