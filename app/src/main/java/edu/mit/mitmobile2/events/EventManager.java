package edu.mit.mitmobile2.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.events.model.MITCalendar;
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

    public static ServiceCall getCalendars(Activity activity, Callback<List<MITCalendar>> calendars) {
        ServiceCallWrapper<?> returnValue = new ServiceCallWrapper<>(new MITAPIClient(activity), calendars);

        returnValue.getClient().get(Constants.EVENTS, Constants.Events.CALENDARS_PATH, null, null, returnValue);

        return returnValue;
    }

    public static ServiceCall getCalendarDetail(Activity activity, MITCalendar cal, Callback<MITCalendar> calendar) {
        ServiceCallWrapper<?> returnValue = new ServiceCallWrapper<>(new MITAPIClient(activity), calendar);

        returnValue.getClient().get(
            Constants.EVENTS,
            Constants.Events.CALENDAR_PATH,
            new FluentParamMap()
                .add("calendar", cal.getIdentifier())
                .object(),
            null,
            returnValue);

        return returnValue;
    }


    public interface ApiService {
        @GET(Constants.Events.CALENDARS_PATH)
        void _get_all(Callback<List<MITCalendar>> callback);
        @GET(Constants.Events.CALENDAR_PATH)
        void _get_a(Callback<MITCalendar> callback);
        @GET(Constants.Events.CALENDAR_EVENTS_PATH)
        void _get_calendar_events(Callback<List<UNIMPLEMENTED_GET_EVENTS>> callback);
        @GET(Constants.Events.CALENDAR_EVENT_PATH)
        void _get_calendar_event(Callback<List<UNIMPLEMENTED_GET_EVENT_DETAIL>> callback);
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
    static class UNIMPLEMENTED_GET_EVENT_DETAIL {
        static {
            new UNIMPLEMENTED_GET_EVENT_DETAIL();
        }

        public UNIMPLEMENTED_GET_EVENT_DETAIL() {
            throw new RuntimeException("The caller of this class is meant only as a stub and is not yet implemented," +
                " please implement it and once complete remove this class.", new RuntimeException("java-exception:unimplemented:" + getClass().getCanonicalName()));
        }
    }

    /**
     * This class is provided as a blank placeholder for stubbed methods, will throw upon .ctor
     */
    static class UNIMPLEMENTED_GET_EVENTS {
        static {
            new UNIMPLEMENTED_GET_EVENTS();
        }

        public UNIMPLEMENTED_GET_EVENTS() {
            throw new RuntimeException("The caller of this class is meant only as a stub and is not yet implemented," +
                " please implement it and once complete remove this class.", new RuntimeException("java-exception:unimplemented:" + getClass().getCanonicalName()));
        }
    }
}