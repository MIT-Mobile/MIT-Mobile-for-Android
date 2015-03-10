package edu.mit.mitmobile2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;
import edu.mit.mitmobile2.shuttles.model.RouteStop;
import edu.mit.mitmobile2.shuttles.model.StopPrediction;
import timber.log.Timber;

public class DBAdapter {
    private interface Migration {
        void apply(SQLiteDatabase db);
    }

    private class DynamicPersistenceHandler extends DatabaseObject.PersistenceHandler<Object, Object> {
        public final Method persist;
        public final Method delete;

        public DynamicPersistenceHandler(DBAdapter database, DatabaseObject obj) {
            super(database);

            Class<? extends DatabaseObject> objClass = obj.getClass();
            persist = ReflectionUtils.findAnnotatedMethod(database.getClass(),
                    Persist.class,
                    objClass);
            delete = ReflectionUtils.findAnnotatedMethod(database.getClass(),
                    Delete.class,
                    objClass);
        }

        @Override
        public long persist(Object object) throws SQLiteException {
            if (persist == null) {
                DatabaseObject obj = (DatabaseObject) object;
                ContentValues values = new ContentValues();
                obj.fillInContentValues(values, DBAdapter.this);
                return insertOrUpdate(obj, values);
            } else {
                try {
                    persist.invoke(database, object);
                } catch (IllegalAccessException e) {
                    Timber.e(e, "Unable to persist for Object: " + object);
                } catch (InvocationTargetException e) {
                    Timber.e(e, "Unable to persist for Object: " + object);
                    Throwable throwable = e.getCause();
                    if (throwable instanceof SQLiteException) {
                        throw (SQLiteException) throwable;
                    }
                }
                return -1;
            }
        }

        @Override
        public void delete(Object object) {
            if (delete == null) {
                DBAdapter.this.delete((DatabaseObject) object);
            } else {
                try {
                    delete.invoke(database, object);
                } catch (IllegalAccessException e) {
                    Timber.e(e, "Unable to delete for Object: " + object);
                } catch (InvocationTargetException e) {
                    Timber.e(e, "Unable to delete for Object: " + object);
                }
            }
        }
    }


    static final String DATABASE_NAME = "mit_mobile.db";

    private static Migration[] MIGRATIONS = new Migration[]{null,
            /*new Migration() {
                @Override
                public void apply(SQLiteDatabase db) {
                    Timber.d("migrate");
                    db.execSQL("ALTER TABLE " + Schema.UserTable.TABLE_NAME + " ADD COLUMN " + Schema.UserTable.CACHE_TIME + " long default 0");

                }
            },
            new Migration() {
                @Override
                public void apply(SQLiteDatabase db) {
                    Timber.d("migrate 2");
                    db.execSQL("ALTER TABLE " + Schema.ExperienceTable.TABLE_NAME + " ADD COLUMN " + Schema.ExperienceTable.CACHE_TIME + " long default 0");
                }
            }*/
    };

    final Context context;

    public SQLiteDatabase db;
    private DatabaseHelper helper;
    private static DBAdapter instance = null;

    public static DBAdapter getInstance() {
        return instance;
    }

    public DBAdapter(Context ctx) {
        this.context = ctx;
        instance = this;
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public void clear() {
        helper.onCreate(db);
    }

    //Delete all the data from the tables
    public void deletePreviousData() {
        db.execSQL("DELETE FROM " + Schema.Vehicle.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.Prediction.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.Route.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.RouteStops.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.Stop.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.StopPredictions.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.Path.TABLE_NAME);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 3; // Current DB version
        private static final int CREATE_TABLE_VERSION = 1; // This is where createTables() gets us. Will be locked down once the DB stabilizes.

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            dropTables(db);
            createTables(db);  // This gets us to CREATE_TABLE_VERSION

            runMigrations(db,
                    CREATE_TABLE_VERSION,
                    DATABASE_VERSION); // ...and then we use migrations for the rest.
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            Timber.w("DB opened!");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Timber.w("Upgrading database from version " + oldVersion + " to " + newVersion);
            runMigrations(db, oldVersion, newVersion);
        }

        /**
         * Create initial tables.
         * <p/>
         * DO NOT CHANGE THIS METHOD AFTER PRODUCTION RELEASE.  Changes after that should be in migrations.
         *
         * @param db our database
         */
        private static void createTables(SQLiteDatabase db) {
            //TODO: add here
            db.execSQL(Schema.Vehicle.CREATE_TABLE_SQL);
            db.execSQL(Schema.Prediction.CREATE_TABLE_SQL);
            db.execSQL(Schema.Route.CREATE_TABLE_SQL);
            db.execSQL(Schema.RouteStops.CREATE_TABLE_SQL);
            db.execSQL(Schema.Stop.CREATE_TABLE_SQL);
            db.execSQL(Schema.StopPredictions.CREATE_TABLE_SQL);
            db.execSQL(Schema.Path.CREATE_TABLE_SQL);
            Timber.d("Tables created!");
        }

        private static void dropTables(SQLiteDatabase db) {
            //TODO: add here
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Vehicle.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Prediction.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Route.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.RouteStops.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Stop.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.StopPredictions.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Path.TABLE_NAME);
        }

        private static void runMigrations(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (int i = oldVersion; i < newVersion; i++) {
                if (MIGRATIONS.length > i) {
                    Timber.d("migrate less than i");
                    Migration migration = MIGRATIONS[i];
                    if (migration != null) {
                        // TODO: SLTimber.d(TAG, "Applying migration #" + i);
                        migration.apply(db);
                    }
                }
            }
        }
    }

    public void acquire(DatabaseObject obj) {
        obj.setPersistenceHandler(new DynamicPersistenceHandler(this, obj));
    }

    private long insertOrUpdate(DatabaseObject dbObject, ContentValues values) {
        String tableName = dbObject.getTableName();
        long databaseId = dbObject.getDatabaseId();
        if (databaseId == DatabaseObject.INVALID_ID || databaseId == 0) {
            // Insert
            databaseId = db.insertOrThrow(tableName, null, values);
            dbObject.setDatabaseId(databaseId);
        } else {
            db.update(tableName, values, Schema.Table.ID_COL + " = ?",
                    new String[]{String.valueOf(databaseId)});
        }
        return databaseId;
    }

    private void delete(DatabaseObject dbObject) {
        String tableName = dbObject.getTableName();
        long databaseId = dbObject.getDatabaseId();
        if (databaseId == DatabaseObject.INVALID_ID) {
            Timber.e("Can't delete database object (no id) : " + dbObject);
            return;
        }
        db.delete(tableName, Schema.Table.ID_COL + " = ?", new String[]{String.valueOf(databaseId)});
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Added Code Here

    public Set<String> getAllIds(String tableName, String[] columns, String columnToIndex) {
        Set<String> set = new HashSet<>();
        Cursor cursor = db.query(tableName, columns,
                null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                set.add(cursor.getString(cursor.getColumnIndex(columnToIndex)));
            }
        } finally {
            cursor.close();
        }
        return set;
    }

    public void batchPersist(List<DatabaseObject> dbObjects, String tableName) {
        db.beginTransaction();
        try {
            for (DatabaseObject obj : dbObjects) {
                ContentValues cv = new ContentValues();
                obj.fillInContentValues(cv, this);
                long newID = db.insertWithOnConflict(tableName, null, cv, SQLiteDatabase.CONFLICT_FAIL);
                if (newID <= 0) {
                    throw new SQLException("Error");
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public boolean exists(String tableName, String[] columns) {
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);

        int count = 0;

        try {
            while (cursor.moveToNext()) {
                count++;
            }
        } finally {
            cursor.close();
        }

        return count >= 13;
    }

    public void batchPersistStops(List<MITShuttleStopWrapper> dbObjects, String routeId) {
        List<DatabaseObject> updatedObjects = new ArrayList<>();
        Set<String> ids = getAllIds(Schema.Stop.TABLE_NAME, Schema.Stop.ALL_COLUMNS, Schema.Stop.STOP_ID);

        checkObjectAndPersist(dbObjects, updatedObjects, ids, Schema.Stop.TABLE_NAME, Schema.Stop.STOP_ID, null);

        updatedObjects.clear();
        ids.clear();

        ids = getAllIds(Schema.RouteStops.TABLE_NAME, Schema.RouteStops.ALL_COLUMNS, Schema.RouteStops.STOP_ID);
        checkObjectAndPersist(dbObjects, updatedObjects, ids, Schema.RouteStops.TABLE_NAME, Schema.RouteStops.STOP_ID, routeId);
    }

    private void checkObjectAndPersist(List<MITShuttleStopWrapper> dbObjects, List<DatabaseObject> updatedObjects, Set<String> ids, String tableName, String columnToIndex, String extra) {
        for (MITShuttleStopWrapper s : dbObjects) {
            if (ids.contains(s.getId())) {
                ContentValues values = new ContentValues();
                if (extra != null) {
                    RouteStop routeStop = new RouteStop(extra, s.getId());
                    routeStop.fillInContentValues(values, this);
                } else {
                    s.fillInContentValues(values, this);
                }
                db.update(tableName, values,
                        columnToIndex + " = \'" + s.getId() + "\'", null);
            } else {
                if (extra != null) {
                    RouteStop routeStop = new RouteStop(extra, s.getId());
                    updatedObjects.add(routeStop);
                } else {
                    updatedObjects.add(s);
                }
            }
        }

        batchPersist(updatedObjects, tableName);
    }

    public void batchPersistPredictions(List<MITShuttlePrediction> dbObjects, String stopId) {
        // Store in Stops table
        db.beginTransaction();
        try {
            for (DatabaseObject obj : dbObjects) {
                ContentValues cv = new ContentValues();
                obj.fillInContentValues(cv, this);
                long newID = db.insertWithOnConflict(Schema.Prediction.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_FAIL);
                if (newID <= 0) {
                    throw new SQLException("Error");
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        //Store IDs in RouteStops table
        db.beginTransaction();
        try {
            for (MITShuttlePrediction prediction : dbObjects) {
                StopPrediction stopPrediction = new StopPrediction(stopId, prediction.getVehicleId());
                ContentValues cv = new ContentValues();
                stopPrediction.fillInContentValues(cv, this);
                long newID = db.insertWithOnConflict(Schema.StopPredictions.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_FAIL);
                if (newID <= 0) {
                    throw new SQLException("Error");
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Long> batchPersistAndReturnIds(List<DatabaseObject> dbObjects, String tableName) {
        List<Long> ids = new ArrayList<>();

        db.beginTransaction();
        try {
            for (DatabaseObject obj : dbObjects) {
                ContentValues cv = new ContentValues();
                obj.fillInContentValues(cv, this);
                long newID = db.insertWithOnConflict(tableName, null, cv, SQLiteDatabase.CONFLICT_FAIL);
                if (newID <= 0) {
                    throw new SQLException("Error");
                }
                ids.add(newID);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return ids;
    }
}