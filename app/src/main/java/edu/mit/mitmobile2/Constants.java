package edu.mit.mitmobile2;

public class Constants {
    public static final String RESOURCES = "resource";
    public static final String SHUTTLES = "shuttles";

    public static class Shuttles {
        public static final String ALL_ROUTES_PATH = "/routes";
        public static final String ROUTE_INFO_PATH = "/routes/{route}";
        public static final String STOP_INFO_PATH = "/routes/{route}/stops/{stop}";
        public static final String PREDICTIONS_PATH = "/predictions";
        public static final String VEHICLES_PATH = "/vehicles";

        public static final String URI = "mitmobile2://shuttles";
        public static final String ROUTE_URI = URI + "/routes";
        public static final String STOPS_URI = URI + "/stops";
    }


}
