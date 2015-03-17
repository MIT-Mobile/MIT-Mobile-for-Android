package edu.mit.mitmobile2.shuttles;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePath;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;
import edu.mit.mitmobile2.shuttles.model.MITShuttleVehicle;
import edu.mit.mitmobile2.shuttles.model.RouteStop;

public class ShuttlesDatabaseHelper {

    public static SQLiteDatabase db = MitMobileApplication.dbAdapter.db;
    public static DBAdapter dbAdapter = MitMobileApplication.dbAdapter;

    public static void batchPersistStops(List<MITShuttleStopWrapper> dbObjects, String routeId, boolean predictable) {
        List<DatabaseObject> updatedObjects = new ArrayList<>();
        Set<String> ids = dbAdapter.getAllIds(Schema.Stop.TABLE_NAME, Schema.Stop.ALL_COLUMNS, Schema.Stop.STOP_ID);

        checkObjectAndPersist(dbObjects, updatedObjects, ids, Schema.Stop.TABLE_NAME, Schema.Stop.STOP_ID, predictable);

        updatedObjects.clear();

        HashMap<String, String> idToDirectionMap = dbAdapter.getIdToDirectionMap(Schema.RouteStops.TABLE_NAME, Schema.RouteStops.ALL_COLUMNS, Schema.RouteStops.ROUTE_ID, Schema.RouteStops.STOP_ID, routeId);

        for (MITShuttleStopWrapper s : dbObjects) {
            RouteStop routeStop = new RouteStop(routeId, s.getId());

            if (idToDirectionMap.containsKey(s.getId()) && idToDirectionMap.get(s.getId()).equals(routeId)) {
                ContentValues values = new ContentValues();
                routeStop.fillInContentValues(values, dbAdapter);

                db.update(Schema.RouteStops.TABLE_NAME, values, Schema.RouteStops.STOP_ID + " = \'" + s.getId() + "\' AND " + Schema.RouteStops.ROUTE_ID + "=\'" + routeId + "\'", null);
            } else {
                updatedObjects.add(routeStop);
            }
        }

        dbAdapter.batchPersist(updatedObjects, Schema.RouteStops.TABLE_NAME);
    }

    private static void checkObjectAndPersist(List<MITShuttleStopWrapper> dbObjects, List<DatabaseObject> updatedObjects, Set<String> ids, String tableName, String columnToIndex, boolean predictable) {
        for (MITShuttleStopWrapper s : dbObjects) {
            if (ids.contains(s.getId())) {
                ContentValues values = new ContentValues();
                s.fillInContentValues(values, dbAdapter);

                if (predictable) {
                    // Calculate location in here
                    Location stopLocation = new Location("");
                    stopLocation.setLatitude(values.getAsDouble(Schema.Stop.STOP_LAT));
                    stopLocation.setLongitude(values.getAsDouble(Schema.Stop.STOP_LON));

                    Cursor c = db.query(Schema.Location.TABLE_NAME, Schema.Location.ALL_COLUMNS, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        double lat = c.getDouble(c.getColumnIndex(Schema.Location.LATITUDE));
                        double lon = c.getDouble(c.getColumnIndex(Schema.Location.LONGITUDE));

                        Location myLocation = new Location("");
                        myLocation.setLatitude(lat);
                        myLocation.setLongitude(lon);

                        float distance = myLocation.distanceTo(stopLocation);

                        values.put(Schema.Stop.DISTANCE, distance);
                    }
                    c.close();
                }

                db.update(tableName, values,
                        columnToIndex + " = \'" + s.getId() + "\'", null);
            } else {
                updatedObjects.add(s);
            }
        }

        dbAdapter.batchPersist(updatedObjects, tableName);
    }

    public static List<MITShuttleRoute> getAllRoutes() {
        List<MITShuttleRoute> routes = new ArrayList<>();

        //TODO: Refactor the SQL statement
        String queryString = "SELECT route_stops._id AS rs_id, stops._id AS s_id, routes._id, routes.route_id, routes.route_url, routes.route_title, routes.agency, routes.scheduled, routes.predictable, routes.route_description, routes.predictions_url, routes.vehicles_url, routes.path_id, stops.stop_id, stops.stop_url, stops.stop_title, stops.stop_lat, stops.stop_lon, stops.stop_number, stops.distance, stops.predictions " +
                "FROM routes " +
                "INNER JOIN route_stops ON routes.route_id = route_stops.route_id " +
                "JOIN stops ON route_stops.stop_id = stops.stop_id";

        Cursor cursor = db.rawQuery(queryString, null);

        generateRouteObjects(routes, cursor);
        return routes;
    }

    public static void generateRouteObjects(List<MITShuttleRoute> routes, Cursor cursor) {
        try {
            while (cursor.moveToNext()) {
                MITShuttleRoute route = new MITShuttleRoute();
                route.buildFromCursor(cursor, dbAdapter);
                routes.add(route);
            }
        } finally {
            cursor.close();
        }
    }

    public static MITShuttleRoute getRoute(String routeId) {

        //TODO: Refactor the SQL statement
        String queryString = "SELECT route_stops._id AS rs_id, stops._id AS s_id, routes._id, routes.route_id, routes.route_url, routes.route_title, routes.agency, routes.scheduled, routes.predictable, routes.route_description, routes.predictions_url, routes.vehicles_url, routes.path_id, stops.stop_id, stops.stop_url, stops.stop_title, stops.stop_lat, stops.stop_lon, stops.stop_number, stops.distance, stops.predictions " +
                "FROM routes " +
                "INNER JOIN route_stops ON routes.route_id = route_stops.route_id " +
                "JOIN stops ON route_stops.stop_id = stops.stop_id " +
                "WHERE routes.route_id = \'" + routeId + "\'";

        Cursor cursor = db.rawQuery(queryString, null);

        MITShuttleRoute route;

        try {
            cursor.moveToFirst();
            route = new MITShuttleRoute();
            route.buildFromCursor(cursor, dbAdapter);
        } finally {
            cursor.close();
        }

        return route;
    }

    public static MITShuttlePath getPath(long pathId) {
        Cursor cursor = db.query(Schema.Path.TABLE_NAME, Schema.Path.ALL_COLUMNS,
                Schema.Path.ID_COL + "=" + pathId, null, null, null, null);
        MITShuttlePath path;

        try {
            cursor.moveToFirst();
            path = new MITShuttlePath();
            path.buildFromCursor(cursor, dbAdapter);
        } finally {
            cursor.close();
        }

        return path;
    }

    public static List<MITShuttleVehicle> getVehicles(String routeId) {
        List<MITShuttleVehicle> vehicles = new ArrayList<>();

        Cursor cursor = db.query(Schema.Vehicle.TABLE_NAME, Schema.Vehicle.ALL_COLUMNS, Schema.Vehicle.ROUTE_ID + "=\'" + routeId + "\'", null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                MITShuttleVehicle vehicle = new MITShuttleVehicle();
                vehicle.buildFromCursor(cursor, dbAdapter);
                vehicles.add(vehicle);
            }
        } finally {
            cursor.close();
        }
        return vehicles;
    }

    public static void batchPersistVehicles(List<MITShuttleVehicle> dbObjects, String routeId) {
        List<DatabaseObject> updatedObjects = new ArrayList<>();
        Set<String> ids = dbAdapter.getAllIds(Schema.Vehicle.TABLE_NAME, Schema.Vehicle.ALL_COLUMNS, Schema.Vehicle.VEHICLE_ID);

        for (MITShuttleVehicle v : dbObjects) {
            v.setRouteId(routeId);
            if (ids.contains(v.getId())) {
                ContentValues values = new ContentValues();
                v.fillInContentValues(values, dbAdapter);
                dbAdapter.db.update(Schema.Vehicle.TABLE_NAME, values,
                        Schema.Vehicle.VEHICLE_ID + " = \'" + v.getId() + "\'", null);
            } else {
                updatedObjects.add(v);
            }
        }

        dbAdapter.batchPersist(updatedObjects, Schema.Vehicle.TABLE_NAME);
    }
}
