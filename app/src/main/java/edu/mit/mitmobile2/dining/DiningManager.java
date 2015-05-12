package edu.mit.mitmobile2.dining;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by grmartin on 5/7/15.
 */
public class DiningManager  extends RetrofitManager {
    private static final MitDiningService MIT_DINING_SERVICE = MIT_REST_ADAPTER.create(MitDiningService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback)
        throws NoSuchFieldException,  NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitDiningService.class, path, pathParams, queryParams, Callback.class);
        LoggingManager.Timber.d("Method = " + m);
        m.invoke(MIT_DINING_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams)
        throws NoSuchFieldException,  NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitDiningService.class, path, pathParams, queryParams);
        LoggingManager.Timber.d("Method = " + m);
        return m.invoke(MIT_DINING_SERVICE);
    }

    public static DiningManagerCall getDiningOptions(Activity activity, Callback<MITDiningDining> dining) {
        DiningManagerCallWrapper<?> returnValue = new DiningManagerCallWrapper<>(new MITAPIClient(activity), dining);

        returnValue.getClient().get(Constants.DINING, Constants.Dining.DINING_PATH, null, null, returnValue);

        return returnValue;
    }

    public interface MitDiningService {
        @GET(Constants.Dining.DINING_PATH)
        void _getcontacts(Callback<MITDiningDining> callback);
    }

    public static class DiningManagerCallWrapper<T>  extends MITAPIClient.ApiCallWrapper<T> implements DiningManagerCall, Callback<T> {
        public DiningManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
            super(client, callback);
        }
    }

    public interface DiningManagerCall extends MITAPIClient.ApiCall {}
}
