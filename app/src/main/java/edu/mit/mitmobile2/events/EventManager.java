package edu.mit.mitmobile2.events;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.events.model.MITCalendar;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;
import retrofit.Callback;
import retrofit.http.GET;

public class EventManager extends RetrofitManager {
    private static final ApiService SERVICE_INTERFACE = MIT_REST_ADAPTER.create(ApiService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(ApiService.class, path, pathParams, queryParams, Callback.class);
        Timber.d("Method = " + m);
        m.invoke(SERVICE_INTERFACE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(ApiService.class, path, pathParams, queryParams);
        Timber.d("Method = " + m);
        return m.invoke(SERVICE_INTERFACE);
    }

    public static ServiceCall getCalendarEvents(Activity activity, MITCalendar cal, MITCalendar category, String query, Callback<List<MITCalendarEvent>> events) {
        ServiceCallWrapper<?> returnValue = new ServiceCallWrapper<>(new MITAPIClient(activity), events);

        returnValue.getClient().get(
                Constants.EVENTS,
                Constants.Events.CALENDAR_EVENTS_PATH,
                new FluentParamMap()
                        .add("calendar", cal.getIdentifier())
                        .object(),
                new FluentParamMap()
                        .add("category", category.getIdentifier())
                        .add("q", query != null ? query : "")
                        .object(),
                returnValue);

        return returnValue;
    }

    public static ServiceCall getCalendarEvents(Activity activity, MITCalendar cal, Callback<List<MITCalendarEvent>> events) {
        ServiceCallWrapper<?> returnValue = new ServiceCallWrapper<>(new MITAPIClient(activity), events);

        returnValue.getClient().get(
                Constants.EVENTS,
                Constants.Events.CALENDAR_EVENTS_PATH,
                new FluentParamMap()
                        .add("calendar", cal.getIdentifier())
                        .object(),
                null,
                returnValue);

        return returnValue;
    }

    public static ServiceCall getCalendarEventDetail(Activity activity, MITCalendar cal, MITCalendarEvent evt, Callback<MITCalendarEvent> eventHandler) {
        ServiceCallWrapper<?> returnValue = new ServiceCallWrapper<>(new MITAPIClient(activity), eventHandler);

        returnValue.getClient().get(
                Constants.EVENTS,
                Constants.Events.CALENDAR_EVENT_PATH,
                new FluentParamMap()
                        .add("calendar", cal.getIdentifier())
                        .add("event", evt.getIdentifier())
                        .object(),
                null,
                returnValue);

        return returnValue;
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
        void _get_calendar_events(Callback<List<MITCalendarEvent>> callback);

        @GET(Constants.Events.CALENDAR_EVENT_PATH)
        void _get_calendar_event(Callback<MITCalendarEvent> callback);
    }

    public static class ServiceCallWrapper<T> extends MITAPIClient.ApiCallWrapper<T> implements ServiceCall, Callback<T> {
        public ServiceCallWrapper(MITAPIClient client, Callback<T> callback) {
            super(client, callback);
        }
    }

    public interface ServiceCall extends MITAPIClient.ApiCall {
    }
}