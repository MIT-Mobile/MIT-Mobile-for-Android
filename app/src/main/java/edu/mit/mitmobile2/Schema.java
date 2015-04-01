package edu.mit.mitmobile2;

public class Schema {

    static class Table {
        public static final String ID_COL = "_id";
        protected static final String CREATE_TERMINATOR = ");";

        static String buildCreateSQL(String tableName, String createText) {
            return "create table " + tableName + " (" +
                    ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    createText +
                    CREATE_TERMINATOR;
        }
    }

    public static class Vehicle extends Table {
        public static final String TABLE_NAME = "vehicles";
        public static final String VEHICLE_ID = "id";
        public static final String VEHICLE_LAT = "vehicle_lat";
        public static final String VEHICLE_LON = "vehicle_lon";
        public static final String HEADING = "vehicle_heading";
        public static final String SPEED = "vehicle_speed";
        public static final String SECS_SINCE_REPORT = "secs_since_report";
        public static final String ROUTE_ID = "route_id";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        VEHICLE_ID + " text not null, " +
                                VEHICLE_LAT + " double not null, " +
                                VEHICLE_LON + " double not null, " +
                                HEADING + " integer not null, " +
                                SPEED + " integer not null, " +
                                SECS_SINCE_REPORT + " integer not null, " +
                                ROUTE_ID + " test not null"
                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, VEHICLE_ID, VEHICLE_LAT, VEHICLE_LON, HEADING, SPEED, SECS_SINCE_REPORT, ROUTE_ID
        };
    }

    public static class Route extends Table {
        public static final String TABLE_NAME = "routes";
        public static final String ROUTE_ID = "route_id";
        public static final String ROUTE_URL = "route_url";
        public static final String ROUTE_TITLE = "route_title";
        public static final String AGENCY = "agency";
        public static final String SCHEDULED = "scheduled";
        public static final String PREDICTABLE = "predictable";
        public static final String ROUTE_DESCRIPTION = "route_description";
        public static final String PREDICTIONS_URL = "predictions_url";
        public static final String VEHICLES_URL = "vehicles_url";
        public static final String MIT_PATH_ID = "path_id";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        ROUTE_ID + " text not null, " +
                                ROUTE_URL + " text not null, " +
                                ROUTE_TITLE + " text not null, " +
                                AGENCY + " text not null, " +
                                SCHEDULED + " integer not null, " +
                                PREDICTABLE + " integer not null, " +
                                ROUTE_DESCRIPTION + " text not null, " +
                                PREDICTIONS_URL + " text not null, " +
                                VEHICLES_URL + " text not null, " +
                                MIT_PATH_ID + " long"
                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, ROUTE_URL, ROUTE_ID, ROUTE_TITLE, AGENCY, SCHEDULED, PREDICTABLE, ROUTE_DESCRIPTION, PREDICTIONS_URL, VEHICLES_URL, MIT_PATH_ID
        };
    }

    public static class RouteStops extends Table {
        public static final String TABLE_NAME = "route_stops";
        public static final String ROUTE_ID = "route_id";
        public static final String STOP_ID = "stop_id";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        ROUTE_ID + " text not null, " +
                                STOP_ID + " text not null"
                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, ROUTE_ID, STOP_ID
        };
    }

    public static class Stop extends Table {
        public static final String TABLE_NAME = "stops";
        public static final String STOP_ID = "stop_id";
        public static final String STOP_URL = "stop_url";
        public static final String ROUTE_ID = "route_id";
        public static final String ROUTE_URL = "route_url";
        public static final String STOP_TITLE = "stop_title";
        public static final String STOP_NUMBER = "stop_number";
        public static final String STOP_LAT = "stop_lat";
        public static final String STOP_LON = "stop_lon";
        public static final String PREDICTIONS_URL = "predictions_url";
        public static final String PREDICTIONS = "predictions";
        public static final String DISTANCE = "distance";
        public static final String TIMESTAMP = "timestamp";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        STOP_ID + " text not null, " +
                                STOP_URL + " text not null, " +
                                STOP_TITLE + " text not null, " +
                                ROUTE_ID + " text, " +
                                ROUTE_URL + " text, " +
                                STOP_LAT + " double not null, " +
                                STOP_LON + " double not null, " +
                                STOP_NUMBER + " text, " +
                                DISTANCE + " float, " +
                                PREDICTIONS + " text, " +
                                TIMESTAMP + " long not null, " +
                                PREDICTIONS_URL + " text not null"
                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, STOP_ID, STOP_URL, ROUTE_ID, ROUTE_URL, STOP_TITLE, STOP_NUMBER, STOP_LAT, STOP_LON, DISTANCE, PREDICTIONS, TIMESTAMP, PREDICTIONS_URL
        };
    }

    public static class Path extends Table {
        public static final String TABLE_NAME = "paths";
        public static final String SEGMENTS = "segment";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        SEGMENTS + " text not null"
                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, SEGMENTS
        };
    }

    public static class Location extends Table {
        public static final String TABLE_NAME = "location";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        LATITUDE + " double not null, " +
                                LONGITUDE + " double not null"

                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, LATITUDE, LONGITUDE
        };
    }

    public static class Alerts extends Table {
        public static final String TABLE_NAME = "alerts";
        public static final String ROUTE_ID = "route_id";
        public static final String STOP_ID = "stop_id";
        public static final String VEHICLE_ID = "vehicle_id";
        public static final String TIMESTAMP = "timestamp";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        ROUTE_ID + " text not null, " +
                                STOP_ID + " text not null, " +
                                VEHICLE_ID + " text not null, " +
                                TIMESTAMP + " integer not null"
                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, ROUTE_ID, STOP_ID, VEHICLE_ID, TIMESTAMP
        };
    }

}
