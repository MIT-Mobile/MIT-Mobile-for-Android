package edu.mit.mitmobile2.emergency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoContact;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by grmartin on 4/17/15.
 */
public class EmergencyManager extends RetrofitManager {
    private static final MitEmergencyService MIT_EMERGENCY_SERVICE = MIT_REST_ADAPTER.create(MitEmergencyService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback)
        throws NoSuchFieldException,  NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitEmergencyService.class, path, pathParams, queryParams, Callback.class);
        Timber.d("Method = " + m);
        m.invoke(MIT_EMERGENCY_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams)
        throws NoSuchFieldException,  NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitEmergencyService.class, path, pathParams, queryParams);
        Timber.d("Method = " + m);
        return m.invoke(MIT_EMERGENCY_SERVICE);
    }

    public static EmergencyManagerCall getContacts(Activity activity, Callback<List<MITEmergencyInfoContact>> people) {
        EmergencyManagerCallWrapper<?> returnValue = new EmergencyManagerCallWrapper<>(new MITAPIClient(activity), people);

        returnValue.getClient().get(Constants.EMERGENCY, Constants.Emergency.CONTACTS_PATH, null, null, returnValue);

        return returnValue;
    }

    public interface MitEmergencyService {
        @GET(Constants.Emergency.ANNOUNCEMENT_PATH)
        void _getannouncement(Callback<List<UNIMPLEMENTED>> callback);
        @GET(Constants.Emergency.CONTACTS_PATH)
        void _getcontacts(Callback<List<MITEmergencyInfoContact>> callback);
    }

    public static class EmergencyManagerCallWrapper<T>  extends MITAPIClient.ApiCallWrapper<T> implements EmergencyManagerCall, Callback<T> {
        public EmergencyManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
            super(client, callback);
        }
    }

    public interface EmergencyManagerCall extends MITAPIClient.ApiCall {}

    /**
     * This class is provided as a blank placeholder for stubbed methods, will throw upon .ctor
     */
    static class UNIMPLEMENTED {
        static {
            new UNIMPLEMENTED();
        }

        public UNIMPLEMENTED() {
            throw new RuntimeException("The caller of this class is meant only as a stub and is not yet implemented," +
                    " please implement it and once complete remove this class.", new RuntimeException("xyzzy-java-ex:unimplemented:" + getClass().getCanonicalName()));
        }
    }
}