package edu.mit.mitmobile2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import edu.mit.mitmobile2.maps.model.MITMapPlace;
import edu.mit.mitmobile2.maps.model.MITMapPlaceContent;
import edu.mit.mitmobile2.qrreader.models.QrReaderResult;
import edu.mit.mitmobile2.people.model.MITPerson;
import edu.mit.mitmobile2.shared.MITContentProvider;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;

import static edu.mit.mitmobile2.DatabaseObject.createListFromCursor;
import static edu.mit.mitmobile2.DatabaseObject.getSchemaFieldForMethod;
import static edu.mit.mitmobile2.DatabaseObject.getSchemaTableForClass;
import static edu.mit.mitmobile2.Schema.Table.getTableColumns;

public class DBAdapter {
    private interface Migration {
        void apply(SQLiteDatabase db);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public static class Conditional {
        public static int COLUMN_IS_RAW = 1 << 0;
        public static int VALUE_IS_RAW = 1 << 1;

        public final String column;
        public final Object value;
        public final int isRaw;

        public Conditional(String column, Object value, int isRaw) {
            this.column = column;
            this.value = value;
            this.isRaw = isRaw;
        }

        public Conditional(String column, Object value) {
            this(column, value, 0);
        }

        public Conditional(String column) {
            this(column, true);
        }

        public boolean isColumnRaw() {
            return (isRaw & COLUMN_IS_RAW) == COLUMN_IS_RAW;
        }

        public boolean isValueRaw() {
            return (isRaw & VALUE_IS_RAW) == VALUE_IS_RAW;
        }

        public static Conditional[] many(Conditional... conditionals) {
            return conditionals;
        }
    }

    public static class ConditionalResult {
        public final String conditional;
        public final String[] boundValues;

        public ConditionalResult(String conditional, String... boundValues) {
            this.conditional = conditional;
            this.boundValues = boundValues;
        }
    }

    public static String escapeIdentifier(String identifier) {
        StringBuilder sb = new StringBuilder((identifier.length() * 2) + 2);
        sb.append('"');
        if (identifier.indexOf('"') != -1) {
            int length = identifier.length();
            for (int i = 0; i < length; i++) {
                char c = identifier.charAt(i);
                if (c != '"') {
                    sb.append(c);
                }
            }
        } else
            sb.append(identifier);
        sb.append('"');
        return sb.toString();
    }

    public static String escapeValue(String value) {
        return DatabaseUtils.sqlEscapeString(value);
    }

    public static boolean isNumeric(Object object) {
        return object instanceof Integer ||
                object instanceof Boolean ||
                object instanceof Long ||
                object instanceof Float ||
                object instanceof Double ||
                object instanceof Short ||
                object instanceof Byte ||
                object instanceof Character;
    }

    public static boolean isFloatingPoint(Object object) {
        return object instanceof Float ||
                object instanceof Double;

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

    public static synchronized DBAdapter getInstance() {
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
        flushStaleData();
        db.execSQL("DELETE FROM " + Schema.Location.TABLE_NAME);
    }

    public void flushStaleData() {
        db.execSQL("DELETE FROM " + Schema.Vehicle.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.Route.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.RouteStops.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.Stop.TABLE_NAME);
        db.execSQL("DELETE FROM " + Schema.Path.TABLE_NAME);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1; // Current DB version
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
            db.execSQL(Schema.Vehicle.CREATE_TABLE_SQL);
            db.execSQL(Schema.Route.CREATE_TABLE_SQL);
            db.execSQL(Schema.RouteStops.CREATE_TABLE_SQL);
            db.execSQL(Schema.Stop.CREATE_TABLE_SQL);
            db.execSQL(Schema.Path.CREATE_TABLE_SQL);
            db.execSQL(Schema.Location.CREATE_TABLE_SQL);
            db.execSQL(Schema.Alerts.CREATE_TABLE_SQL);
            db.execSQL(Schema.MapPlace.CREATE_TABLE_SQL);
            db.execSQL(Schema.MapPlaceContent.CREATE_TABLE_SQL);
            db.execSQL(Schema.Person.CREATE_TABLE_SQL);
            db.execSQL(Schema.QrReaderResult.CREATE_TABLE_SQL);

            db.execSQL(Schema.Person.CREATE_INDICIES_SQL);

            Timber.d("Tables created!");
        }

        private static void dropTables(SQLiteDatabase db) {
            dropIndicies(db, Schema.Person.INDICIES);

            db.execSQL("DROP TABLE IF EXISTS " + Schema.Vehicle.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Route.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.RouteStops.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Stop.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Path.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Location.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Alerts.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.MapPlace.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.MapPlaceContent.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.Person.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Schema.QrReaderResult.TABLE_NAME);
        }

        private static void dropIndicies(SQLiteDatabase db, String[] indicies) {
            for (String index : indicies) {
                db.execSQL("DROP INDEX IF EXISTS [%s]");
            }
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

    public boolean hasBeenAcquired(DatabaseObject obj) {
        return obj.hasPersistenceHandler();
    }

    public void acquireIfNeeded(DatabaseObject obj) {
        if (!hasBeenAcquired(obj))
            acquire(obj);
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

    public HashMap<String, String> getIdToDirectionMap(String tableName, String[] columns, String firstColumn, String secondColumn, String id) {
        HashMap<String, String> map = new HashMap<>();
        Cursor cursor = db.query(tableName, columns,
                Schema.RouteStops.ROUTE_ID + "=\'" + id + "\'", null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                String routeId = cursor.getString(cursor.getColumnIndex(firstColumn));
                String stopId = cursor.getString(cursor.getColumnIndex(secondColumn));
                map.put(stopId, routeId);
            }
        } finally {
            cursor.close();
        }
        return map;
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

    public int favoritesRowCount(Context context) {
        Cursor cursor = context.getContentResolver().query(MITContentProvider.PEOPLE_COUNT_URI, null, null, null, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }

        return count;
    }

    private ConditionalResult generateConditionalClause(Conditional... conditionals) {
        StringBuilder builder = new StringBuilder();
        List<String> vals = new LinkedList<>();

        boolean first = true;
        for (Conditional cnd : conditionals) {
            if (first) first = false;
            else builder.append(" AND ");

            builder.append(cnd.isColumnRaw() ? cnd.column : escapeIdentifier(cnd.column));
            builder.append(" ");

            if (cnd.isValueRaw()) {
                builder.append(cnd.value);
            } else {
                builder.append("= ");

                if (isNumeric(cnd.value)) {
                    if (isFloatingPoint(cnd.value)) {
                        builder.append(Double.toString((double) cnd.value));
                    } else {
                        if (cnd.value instanceof Boolean) {
                            builder.append(((boolean) cnd.value) ? 0x01 : 0x00);
                        } else {
                            builder.append(Long.toString((long) cnd.value));
                        }
                    }
                } else {
                    builder.append("?");
                    vals.add(String.valueOf(cnd.value));
                }
            }
        }

        return new ConditionalResult(builder.toString(), vals.toArray(new String[vals.size()]));
    }

    public static List<MITPerson> getPersistantFavoritesList(Context context) {
        List<MITPerson> returnList = null;

        Class<? extends Schema.Table> table = getSchemaTableForClass(MITPerson.class);

        if (table != null) {
            Cursor cur = simpleConditionedSelect(
                    context,
                    getTableColumns(table),
                    getSchemaFieldForMethod(MITPerson.class, "isFavorite"),
                    true
            );

            returnList = createListFromCursor(MITPerson.class, cur, DBAdapter.getInstance());

            cur.close();
        }

        return returnList;
    }

    public static Cursor simpleConditionedSelect(Context context, String[] cols, String whereCol, boolean condition) {
        return context.getContentResolver().query(MITContentProvider.PEOPLE_URI, cols, escapeIdentifier(whereCol) + " =?", new String[]{String.valueOf((condition ? 0x01 : 0x00))}, null);
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

        // ??? WTF DOES THIS DO?
        return count >= 13;
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

    public List<MITMapPlaceContent> getMapPlaceContent(String id) {
        List<MITMapPlaceContent> contents = new ArrayList<>();
        Cursor cursor = db.query(Schema.MapPlaceContent.TABLE_NAME, Schema.MapPlaceContent.ALL_COLUMNS,
                String.format("%s='%s'", Schema.MapPlaceContent.PLACE_ID, id), null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                MITMapPlaceContent content = new MITMapPlaceContent();
                content.buildFromCursor(cursor, this);
                contents.add(content);
            }
        } finally {
            cursor.close();
        }
        return contents;
    }

    public List<MITMapPlace> getBookmarks(Context context) {
        List<MITMapPlace> bookmarks = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(MITContentProvider.BOOKMARKS_URI, Schema.MapPlace.ALL_COLUMNS, null, null, null);

        try {
            while (cursor.moveToNext()) {
                MITMapPlace place = new MITMapPlace();
                place.buildFromCursor(cursor, this);
                bookmarks.add(place);
            }
        } finally {
            cursor.close();
        }
        return bookmarks;
    }

    public MITMapPlace getBookmark(Context context, String id) {
        MITMapPlace place = null;

        Cursor cursor = context.getContentResolver().query(MITContentProvider.BOOKMARKS_URI, Schema.MapPlace.ALL_COLUMNS, Schema.MapPlace.PLACE_ID + "=?", new String[]{id}, null);

        try {
            if (cursor.moveToFirst()) {
                place = new MITMapPlace();
                place.buildFromCursor(cursor, this);
            }
        } finally {
            cursor.close();
        }
        return place;
    }

    public static boolean isOnFavoritesList(Context context, String uid) {
        Cursor cursor = context.getContentResolver().query(MITContentProvider.PEOPLE_URI, Schema.Person.ALL_COLUMNS, Schema.Person.PERSON_ID + "=?", new String[]{uid}, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public List<QrReaderResult> getScanningHistory(Context context) {
        List<QrReaderResult> results = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(MITContentProvider.QRREADER_URI, Schema.QrReaderResult.ALL_COLUMNS, null, null, null);

        try {
            while (cursor.moveToNext()) {
                QrReaderResult result = new QrReaderResult();
                result.buildFromCursor(cursor, this);
                results.add(result);
            }
        } finally {
            cursor.close();
        }

        return results;
    }


    public void deleteQrHistoryFromDb(QrReaderResult result) {
        db.delete(Schema.QrReaderResult.TABLE_NAME, String.format("%s='%s'", Schema.QrReaderResult.ID_COL, result.getId()), null);
    }
}