package edu.mit.mitmobile2.emergency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoContact;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import timber.log.Timber;

/**
 * Created by grmartin on 4/17/15.
 */
public class EmergencyManager extends RetrofitManager {
    private static final MitEmergencyService MIT_EMERGENCY_SERVICE = MIT_REST_ADAPTER.create(MitEmergencyService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_EMERGENCY_SERVICE.getClass().getDeclaredMethod(methodName, Callback.class);
        m.invoke(MIT_EMERGENCY_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_EMERGENCY_SERVICE.getClass().getDeclaredMethod(methodName);
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

    public static class EmergencyManagerCallWrapper<T> implements EmergencyManagerCall, Callback<T> {
        private static int CALL_IDENTIFIER = 0;

        private final int callId;
        private final AtomicBoolean completed;
        private final AtomicBoolean errored;
        private final Callback<T> callback;
        private final MITAPIClient client;

        public EmergencyManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
            this.callId = ++CALL_IDENTIFIER;
            this.completed = new AtomicBoolean(false);
            this.errored = new AtomicBoolean(false);
            this.client = client;
            this.callback = callback != null ? callback : new Callback<T>() {
                @Override public void success(T t, Response response) { }
                @Override public void failure(RetrofitError error) { }
            };
        }

        @Override
        public int getCallId() {
            return this.callId;
        }

        @Override
        public boolean isComplete() {
            return completed.get();
        }

        @Override
        public boolean hadError() {
            return errored.get();
        }

        @Override
        public void success(T t, Response response) {
            completed.set(true);
            errored.set(false);

            this.callback.success(t, response);
        }

        @Override
        public void failure(RetrofitError error) {
            completed.set(true);
            errored.set(true);

            this.callback.failure(error);
        }

        public MITAPIClient getClient() {
            return client;
        }

        @Override
        public String toString() {
            return "EmergencyManagerCallWrapper{" +
                    "callId=" + callId +
                    ", completed=" + completed +
                    ", errored=" + errored +
                    ", callback=" + callback +
                    ", client=" + client +
                    '}';
        }
    }

    public interface EmergencyManagerCall {
        int getCallId();
        boolean isComplete();
        boolean hadError();
    }

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