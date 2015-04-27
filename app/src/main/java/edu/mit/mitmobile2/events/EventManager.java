package edu.mit.mitmobile2.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by grmartin on 4/27/15.
 */
public class EventManager extends RetrofitManager {
    private static final ApiService SERVICE_INTERFACE = MIT_REST_ADAPTER.create(ApiService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback)
        throws NoSuchFieldException,  NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(ApiService.class, path, pathParams, queryParams, Callback.class);
        Timber.d("Method = " + m);
        m.invoke(SERVICE_INTERFACE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams)
        throws NoSuchFieldException,  NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(ApiService.class, path, pathParams, queryParams);
        Timber.d("Method = " + m);
        return m.invoke(SERVICE_INTERFACE);
    }

    public interface ApiService {
        @GET(Constants.Events.CALENDARS_PATH)
        void _get_all(List<List<UNIMPLEMENTED>> callback);
        @GET(Constants.Events.CALENDAR_PATH)
        void _get_a(List<List<UNIMPLEMENTED>> callback);
        @GET(Constants.Events.CALENDAR_EVENTS_PATH)
        void _get_calendar_events(List<List<UNIMPLEMENTED>> callback);
        @GET(Constants.Events.CALENDAR_EVENT_PATH)
        void _get_calendar_event(List<List<UNIMPLEMENTED>> callback);
    }

    public static class ServiceCallWrapper<T>  extends MITAPIClient.ApiCallWrapper<T> implements ServiceCall, Callback<T> {
        public ServiceCallWrapper(MITAPIClient client, Callback<T> callback) {
            super(client, callback);
        }
    }

    public interface ServiceCall extends MITAPIClient.ApiCall {}
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