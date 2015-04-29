package edu.mit.mitmobile2;

public class Constants {

    //shuttles
    public static final String RESOURCES = "resource";
    public static final String SHUTTLES = "shuttles";
    public static final String NEWS = "news";
    public static final String SHARED_PREFS_KEY = "mitPrefs";
    public static final String ROUTES_TIMESTAMP = "routesTimestamp";
    public static final String PREDICTIONS_TIMESTAMP = "predictionsTimestamp";
    public static final String ROUTE_ID_KEY = "routeId";
    public static final String STOP_ID_KEY = "stopId";
    public static final String CURRENT_ACTIVE_ALARM_IDS = "activeAlarmIds";
    public static final String ALARM_ID_KEY = "alarm";
    public static final String ALARM_DESCRIPTION = "alarmDescription";
    
    /* We use these to bind the *Managers to the MITAPIClient via api.json */
    public static final String TOURS = "tours";
    public static final String PEOPLE_DIRECTORY = "people";
    public static final String EMERGENCY = "emergency";
    public static final String EVENTS = "events";

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
        public static final String TOUR_STOP = "tourStop";
        public static final String MAIN_LOOP = "Main Loop";
        public static final String SIDE_TRIP = "Side Trip";
        public static final String TOUR_STOP_TYPE = "tourStopType";
        public static final String CURRENT_MAIN_LOOP_STOP = "currentMainLoopStop";
        public static final String CURRENT_STOP_COORDS = "currentStopCoords";
        public static final String PREV_STOP_COORDS = "prevStopCoords";
        public static final String TITLE_KEY = "title";
        public static final String FIRST_TITLE_KEY = "firstTitle";
    }

    public static class News {
        public static final String STORIES_PATH = "/stories";
        public static final String STORIES_BY_ID_PATH = "/stories/{id}";
        public static final String CATEGORIES_PATH = "/categories";
        public static final String MIT_NEWS = "mit_news";
        public static final String AROUND_CAMPUS = "around_campus";
        public static final String IN_THE_MEDIA = "in_the_media";
        public static final String STORIES_KEY = "stories";
        public static final String CATEGORIES_KEY = "categories";
        public static final String STORY = "story";
        public static final String IMAGES_KEY = "images";
        public static final String TITLE_KEY = "title";
        public static final String URL_KEY = "url";
    }

    public static final class People { /* Much of this context is found in the api.json asset. */
        public static final String PEOPLE_PATH = "/";
        public static final String PERSON_PATH = "/{person}";
    }

    public class Emergency {
        public static final String ANNOUNCEMENT_PATH = "/announcement"; /* Not yet implemented, stubbed only */
        public static final String CONTACTS_PATH = "/contacts";
    }

    public class Events {
        public static final String CALENDARS_PATH = "/";
        public static final String CALENDAR_PATH = "/{calendar}";
        public static final String CALENDAR_EVENTS_PATH = "/{calendar}/events";
        public static final String CALENDAR_EVENT_PATH = "/{calendar}/events/{event}";

        public static final String CALENDARS = "calendars";
    }

    public static class Resources {
        public static final String RESOURCE_PATH = "/resource";
        public static final String RESOURCE_ROOMSET_PATH = "/resourceroomset";
//        public static final String STOP_INFO_PATH = "/routes/{route}/stops/{stop}";
//        public static final String PREDICTIONS_PATH = "/predictions";
//        public static final String VEHICLES_PATH = "/vehicles";
//
//        public static final String MIT_TUPLES_KEY = "mitTuples";
//        public static final String CR_TUPLES_KEY = "crTuples";
//        public static final String MODULE_KEY = "module";
//        public static final String PATH_KEY = "path";
//        public static final String URI_KEY = "uri";
//        public static final String PATHS_KEY = "pathParams";
//        public static final String QUERIES_KEY = "queryParams";
    }
}
