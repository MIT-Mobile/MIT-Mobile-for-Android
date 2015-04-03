package edu.mit.mitmobile2.shuttles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePredictionWrapper;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStop;
import edu.mit.mitmobile2.shuttles.model.MITShuttleVehiclesWrapper;
import retrofit.Callback;
import retrofit.http.GET;
import timber.log.Timber;

public class ShuttlesManager extends RetrofitManager {

    private static final MitShuttleService MIT_SHUTTLES_SERVICE = MIT_REST_ADAPTER.create(MitShuttleService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_SHUTTLES_SERVICE.getClass().getDeclaredMethod(methodName, Callback.class);
        m.invoke(MIT_SHUTTLES_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_SHUTTLES_SERVICE.getClass().getDeclaredMethod(methodName);
        return m.invoke(MIT_SHUTTLES_SERVICE);
    }

    public interface MitShuttleService {

        //Async calls for use outside the SyncAdapter

        @GET(Constants.Shuttles.ALL_ROUTES_PATH)
        void _getroutes(Callback<List<MITShuttleRoute>> callback);

        @GET(Constants.Shuttles.ROUTE_INFO_PATH)
        void _getroutes_(Callback<MITShuttleRoute> callback);

        @GET(Constants.Shuttles.STOP_INFO_PATH)
        void _getroutes_stops_(Callback<MITShuttleStop> callback);

        @GET(Constants.Shuttles.PREDICTIONS_PATH)
        void _getpredictions(Callback<List<MITShuttlePredictionWrapper>> callback);

        @GET(Constants.Shuttles.VEHICLES_PATH)
        void _getvehicles(Callback<List<MITShuttleVehiclesWrapper>> callback);

        // Real-time calls for use in the SyncAdapter

        @GET(Constants.Shuttles.ALL_ROUTES_PATH)
        List<MITShuttleRoute> _getroutes();

        @GET(Constants.Shuttles.ROUTE_INFO_PATH)
        MITShuttleRoute _getroutes_();

        @GET(Constants.Shuttles.STOP_INFO_PATH)
        MITShuttleStop _getroutes_stops_();

        @GET(Constants.Shuttles.PREDICTIONS_PATH)
        List<MITShuttlePredictionWrapper> _getpredictions();

        @GET(Constants.Shuttles.VEHICLES_PATH)
        List<MITShuttleVehiclesWrapper> _getvehicles();
    }
}
