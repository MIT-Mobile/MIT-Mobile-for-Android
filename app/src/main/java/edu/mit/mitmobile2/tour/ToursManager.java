package edu.mit.mitmobile2.tour;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.tour.model.MITTourStop;
import edu.mit.mitmobile2.tour.model.MITTour;
import retrofit.Callback;
import retrofit.http.GET;
import timber.log.Timber;

public class ToursManager extends RetrofitManager {

    private static final MitTourService  MIT_TOUR_SERVICE = MIT_REST_ADAPTER.create(MitTourService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_TOUR_SERVICE.getClass().getDeclaredMethod(methodName, Callback.class);
        m.invoke(MIT_TOUR_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_TOUR_SERVICE.getClass().getDeclaredMethod(methodName);
        return m.invoke(MIT_TOUR_SERVICE);
    }

    public interface MitTourService {
        @GET(Constants.Tours.TOUR_PATH)
        void _get(Callback<List<MITTour>> callback);

        @GET(Constants.Tours.ALL_TOUR_STOPS_PATH)
        void _get_(Callback<MITTour> callback);

        @GET(Constants.Tours.TOUR_STOP_IMAGE_PATH)
        void _get_images_(Callback<String> callback);
    }
}
