package edu.mit.mitmobile2;

public class Constants {

    //shuttles
    public static final String RESOURCES = "resource";
    public static final String SHUTTLES = "shuttles";
    public static final String SHARED_PREFS_KEY = "mitPrefs";
    public static final String ROUTES_TIMESTAMP = "routesTimestamp";
    public static final String PREDICTIONS_TIMESTAMP = "predictionsTimestamp";
    public static final String ROUTE_ID_KEY = "routeId";
    public static final String STOP_ID_KEY = "stopId";
    public static final String CURRENT_ACTIVE_ALARM_IDS = "activeAlarmIds";
    public static final String ALARM_ID_KEY = "alarm";
    public static final String ALARM_DESCRIPTION = "alarmDescription";

    //tours
    public static final String TOURS = "tours";

    public static class Shuttles {
        public static final String ALL_ROUTES_PATH = "/routes";
        public static final String ROUTE_INFO_PATH = "/routes/{route}";
        public static final String STOP_INFO_PATH = "/routes/{route}/stops/{stop}";
        public static final String PREDICTIONS_PATH = "/predictions";
        public static final String VEHICLES_PATH = "/vehicles";

        public static final String MIT_TUPLES_KEY = "mitTuples";
        public static final String CR_TUPLES_KEY = "crTuples";
        public static final String MODULE_KEY = "module";
        public static final String PATH_KEY = "path";
        public static final String URI_KEY = "uri";
        public static final String PATHS_KEY = "pathParams";
        public static final String QUERIES_KEY = "queryParams";
    }

    public static class Tours {
        public static final String TOUR_PATH = "/";
        public static final String ALL_TOUR_STOPS_PATH = "/{tour}";
        public static final String TOUR_STOP_IMAGE_PATH = "/{tour}/images/{id}";

        public static final String TOUR_DETAILS_KEY = "tourDetails";
        public static final String TOUR_KEY = "tour";
        public static final String DIRECTION_KEY = "direction";
    }
}
