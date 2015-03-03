package edu.mit.mitmobile2;

public class Constants {
    public static final String RESOURCES = "resource";
    public static final String SHUTTLES = "shuttles";

    public static class Shuttles {
        public static final String ALL_ROUTES_PATH = "/shuttles/routes";
        public static final String ROUTE_INFO_PATH = "/shuttles/routes/{route}";
        public static final String STOP_INFO_PATH = "/shuttles/routes/{route}/stops/{stop}";
        public static final String PREDICTIONS_PATH = "/shuttles/predictions";
        public static final String VEHICLES_PATH = "/shuttles/vehicles";
    }


}
