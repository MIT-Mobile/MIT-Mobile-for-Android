package edu.mit.mitmobile2.people;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.people.model.MITPerson;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import timber.log.Timber;

/**
 * Created by grmartin on 4/16/15.
 */
public class PeopleDirectoryManager extends RetrofitManager {
    private static final MitPersonDirectoryService MIT_PEOPLE_DIR_SERVICE = MIT_REST_ADAPTER.create(MitPersonDirectoryService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_PEOPLE_DIR_SERVICE.getClass().getDeclaredMethod(methodName, Callback.class);
        m.invoke(MIT_PEOPLE_DIR_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_PEOPLE_DIR_SERVICE.getClass().getDeclaredMethod(methodName);
        return m.invoke(MIT_PEOPLE_DIR_SERVICE);
    }

    public static PeopleDirectoryManagerCall searchPeople(Activity activity, String query, Callback<List<MITPerson>> people) {
        PeopleDirectoryManagerCallWrapper<?> returnValue = new PeopleDirectoryManagerCallWrapper<>(new MITAPIClient(activity), people);

        final HashMap<String, String> params = new HashMap<>(1);
        params.put("q", query);
        returnValue.getClient().get(Constants.PEOPLE_DIRECTORY, Constants.People.PEOPLE_PATH, null, params, returnValue);

        return returnValue;
    }

    public interface MitPersonDirectoryService {
        @GET(Constants.People.PEOPLE_PATH)
        void _get(Callback<List<MITPerson>> callback);
        @GET(Constants.People.PERSON_PATH)
        void _getapisperson(Callback<List<MITPerson>> callback);
    }

    public static class PeopleDirectoryManagerCallWrapper<T> implements PeopleDirectoryManagerCall, Callback<T> {
        private static int CALL_IDENTIFIER = 0;

        private final int callId;
        private final AtomicBoolean completed;
        private final AtomicBoolean errored;
        private final Callback<T> callback;
        private final MITAPIClient client;

        public PeopleDirectoryManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
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
    }

    public interface PeopleDirectoryManagerCall {
        int getCallId();
        boolean isComplete();
        boolean hadError();
    }
}