package edu.mit.mitmobile2.maps;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.http.GET;

public class MapManager extends RetrofitManager {
    private static final MitMapService MIT_MAP_SERVICE = MIT_REST_ADAPTER.create(MitMapService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitMapService.class, path, pathParams, queryParams, Callback.class);
        LoggingManager.Timber.d("Method = " + m);
        m.invoke(MIT_MAP_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitMapService.class, path, pathParams, queryParams);
        LoggingManager.Timber.d("Method = " + m);
        return m.invoke(MIT_MAP_SERVICE);
    }

    public static MapManagerCall getMapPlaces(Activity activity, Callback<ArrayList<MITMapPlace>> places) {
        MapManagerCallWrapper<?> returnValue = new MapManagerCallWrapper<>(new MITAPIClient(activity), places);

        returnValue.getClient().get(Constants.MAP, Constants.Map.MAP_PLACES, null, null, returnValue);

        return returnValue;
    }

    public interface MitMapService {
        @GET(Constants.Map.MAP_PLACES)
        void _getmapplaces(Callback<ArrayList<MITMapPlace>> callback);
    }

    public static class MapManagerCallWrapper<T> extends MITAPIClient.ApiCallWrapper<T> implements MapManagerCall, Callback<T> {
        public MapManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
            super(client, callback);
        }
    }

    public interface MapManagerCall extends MITAPIClient.ApiCall {
    }
}
