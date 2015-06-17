package edu.mit.mitmobile2;

import android.support.annotation.NonNull;
import android.text.TextUtils;

/*
    Static class representation of the local Database Schema.

    All tables are defined as subclasses of Table.
    Tables are created in DBAdapter.
 */
@SuppressWarnings("TryWithIdenticalCatches")
public class Schema {

    public static final class Index {
        public final String name;
        public final String[] statements;

        public Index(String name, String... statements) {
            this.name = name;
            this.statements = statements;
        }

        public static Index of(String name, String... statements) {
            return new Index(name, statements);
        }

        public String processStatement() {
            final int count = statements.length;
            String[] outputSet = new String[count];

            for (int i = 0; count > i; i++) {
                final String stmt = this.statements[i];
                if (stmt.contains(" ")) {
                    outputSet[i] = stmt;
                } else {
                    outputSet[i] = "[" + stmt + "]";
                }
            }

            return TextUtils.join(",", outputSet);
        }
    }

    public static class Table {
        public static final String ID_COL = "_id";
        protected static final String CREATE_TERMINATOR = ");";

        protected static String buildCreateSQL(String tableName, String createText) {
            return "create table " + tableName + " (" +
                    ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    createText +
                    CREATE_TERMINATOR;
        }

        protected static String createIndicies(String tableName, Index... indicies) {
            StringBuilder builder = new StringBuilder();
            for (Index idx : indicies) {
                builder.append(String.format("CREATE INDEX IF NOT EXISTS [%s] ON [%s] (%s);", idx.name, tableName, idx.processStatement()));
            }
            return builder.toString();
        }

        public static <T extends Table> String getTableName(@NonNull Class<T> table) {
            try {
                return (String) table.getField("TABLE_NAME").get(table);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        public static <T extends Table> String[] getTableColumns(Class<T> table) {
            try {
                return (String[]) table.getField("ALL_COLUMNS").get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
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

    public static final class Person extends Table {
        public static final String TABLE_NAME = "person";
        public static final String PERSON_ID = "person_id";
        public static final String IS_FAVORITE = "is_favorite";
        public static final String EXTENDED_DATA = "nonatomicdata";

        public static final String IDX_FAVORITES = "IDX_" + TABLE_NAME + "_IS_FAVORITE";

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, PERSON_ID, IS_FAVORITE, EXTENDED_DATA
        };

        public static final String[] INDICIES = new String[]{
                IDX_FAVORITES
        };

        public static final String CREATE_INDICIES_SQL = createIndicies(
                TABLE_NAME,
                Index.of(IDX_FAVORITES, IS_FAVORITE)
        );

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        PERSON_ID + " text not null, " +
                                IS_FAVORITE + " integer not null default 0, " +
                                EXTENDED_DATA + " text not null "
                );
    }

    public static final class MapPlace extends Table {
        public static final String TABLE_NAME = "map_place";
        public static final String PLACE_ID = "place_id";
        public static final String PLACE_NAME = "place_name";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String BUILDING_NUM = "building_num";
        public static final String BUILDING_IMAGE_URL = "building_image_url";
        public static final String STREET = "street";
        public static final String ARCHITECT = "architect";
        public static final String MAILING = "mailing";
        public static final String VIEW_ANGLE = "view_angle";
        public static final String CATEGORIES = "categories";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        PLACE_ID + " text not null, " +
                                PLACE_NAME + " text not null, " +
                                LATITUDE + " double not null, " +
                                LONGITUDE + " double not null, " +
                                BUILDING_NUM + " text, " +
                                BUILDING_IMAGE_URL + " text, " +
                                STREET + " text, " +
                                ARCHITECT + " text, " +
                                MAILING + " text, " +
                                VIEW_ANGLE + " text, " +
                                CATEGORIES + " text"
                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, PLACE_ID, PLACE_NAME, LATITUDE, LONGITUDE, BUILDING_NUM, BUILDING_IMAGE_URL,
                STREET, ARCHITECT, MAILING, VIEW_ANGLE, CATEGORIES
        };
    }

    public static final class MapPlaceContent extends Table {
        public static final String TABLE_NAME = "map_place_content";
        public static final String CONTENT_NAME = "content_name";
        public static final String CATEGORIES = "categories";
        public static final String URL = "url";
        public static final String ALT_NAMES = "alt_names";
        public static final String PLACE_ID = "place_id";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                        CONTENT_NAME + " text not null, " +
                                CATEGORIES + " text, " +
                                URL + " text, " +
                                ALT_NAMES + " text, " +
                                PLACE_ID + " text not null"
                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, CONTENT_NAME, CATEGORIES, URL, ALT_NAMES, PLACE_ID
        };
    }

    public static final class QrReaderResult extends Table {
        public static final String TABLE_NAME = "qr_reader_result";
        public static final String DATE = "content_name";
        public static final String TEXT = "categories";

        public static final String CREATE_TABLE_SQL =
                buildCreateSQL(TABLE_NAME,
                                DATE + " INTEGER, " +
                                TEXT + " TEXT"
                );

        public static final String[] ALL_COLUMNS = new String[]{
                ID_COL, DATE, TEXT
        };
    }
}
