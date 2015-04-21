package edu.mit.mitmobile2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.annotation.Nullable;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("TryWithIdenticalCatches")
public abstract class DatabaseObject extends Observable implements Observer {

    protected static abstract class PersistenceHandler<DataObjectType, DatabaseObjectType> {
        protected DatabaseObjectType database;

        public PersistenceHandler(DatabaseObjectType database) {
            this.database = database;
        }

        public abstract long persist(DataObjectType object) throws SQLiteException;
        public abstract void delete(DataObjectType object);
    }

    /**
     * This annotation tells our {@see #NONATOMIC_ENCODER} to simply not store this field in the data blob at the end.
     * @see #NONATOMIC_ENCODER
     * @see NonAtomicExclusionStrategy
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NonAtomicExclude { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface FieldName {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface SchemaTable {
        Class value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface TableColumns {
        String[] value();
    }

    /**
     * An exclusion strategy to support the {@see NonAtomicExclude} annotation.
     * @see NonAtomicExclude
     */
    public static final class NonAtomicExclusionStrategy implements ExclusionStrategy {
        final String[] PROTECTED_PACKAGE_PREFIXES = {
            "android"
        };

        final Class[] PROTECTED_CLASSES = {
            java.util.Observable.class,
            java.util.EventListener.class
        };

        @Override public boolean shouldSkipField(final FieldAttributes fld) {

            final String pkg = fld.getDeclaringClass().getPackage().getName();

            for (String pkgPrefix : PROTECTED_PACKAGE_PREFIXES) {
                if (pkg.startsWith(pkgPrefix)) return true;
            }

            for (Class<?> klass : PROTECTED_CLASSES) {
                if (fld.getDeclaringClass().equals(klass)) return true;
            }

            return fld.getAnnotation(NonAtomicExclude.class) != null;
        }

        @Override public boolean shouldSkipClass(final Class<?> klass) { return false; }
    }

    /**
     * An encoder to allow us to store bulk data in our database whilst still maintaining that certain fields of the
     * object are to be ignored and stored as atomic data for use in query filtering and other database related tasks.
     *
     * Note: The term atomic refers to the method of data storage for a field itself and currently implies no meaning
     *       in any other context.
     */
    protected static final Gson NONATOMIC_ENCODER = new GsonBuilder().setExclusionStrategies(new NonAtomicExclusionStrategy()).create();

    public static final long INVALID_ID = Long.MIN_VALUE;

    @NonAtomicExclude
    private long databaseId = INVALID_ID;
    @NonAtomicExclude
    private boolean isDirty = false;
    @NonAtomicExclude
    private PersistenceHandler persistenceHandler;

    public DatabaseObject() {
        this.addObserver(this);
    }

    public long getDatabaseId() {
        return databaseId;
    }

    protected void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public boolean isNew() {
        return databaseId == INVALID_ID;
    }

    public boolean isDirty() {
        return isDirty;
    }

    protected void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    protected <DataObjectType, DatabaseObjectType> void setPersistenceHandler(PersistenceHandler<DataObjectType, DatabaseObjectType> handler) {
        this.persistenceHandler = handler;
    }

    protected abstract String getTableName();

    public static String getSchemaTableNameForClass(Class<? extends DatabaseObject> klass) {
        return Schema.Table.getTableName(getSchemaTableForClass(klass));
    }

    @Nullable
    public static <T extends DatabaseObject> Class<? extends Schema.Table> getSchemaTableForClass(Class<T> klass) {
        Class<?> klassFromAttr = klass.getAnnotation(SchemaTable.class).value();

        if (Schema.Table.class.isAssignableFrom(klassFromAttr)) {
            //noinspection unchecked ^^^ see previous check ^^^
            return (Class<? extends Schema.Table>) klassFromAttr;
        }

        return null;
    }

    @Nullable
    public static String getSchemaFieldForMethod(Class<? extends DatabaseObject> klass, String method, boolean onlyTry) {
        try {
            return klass.getMethod(method).getAnnotation(FieldName.class).value();
        } catch (NullPointerException npe) {
            if (!onlyTry)
                throw new IllegalArgumentException("The method name provided was null, returned no value or the required attribute could not be found.", npe);
        } catch (NoSuchMethodException nsme) {
            if (!onlyTry)
                throw new IllegalArgumentException("The method name provided could not be found.", nsme);
        } catch (Throwable t) {
            if (!onlyTry)
                throw t;
        }
        return null;
    }

    @Nullable
    public static String getSchemaFieldForMethod(Class<? extends DatabaseObject> klass, String method) {
        return getSchemaFieldForMethod(klass, method, false);
    }

    public String getSchemaTableName() {
        return getSchemaTableNameForClass(this.getClass());
    }

    public String getSchemaFieldForGetter(String getter, boolean onlyTry) {
        return getSchemaFieldForMethod(this.getClass(), getter, onlyTry);
    }

    /**
     * This is the re-hydration code.  Build your class-specific fields from the db row.
     * @param cursor a cursor pointing to the row for this object, with all columns available
     * @param dbAdapter
     */
    protected abstract void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter);

    // For JOINs
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter, String prefix) { };

    /**
     * Store this object in the database.  The framework does most of the work but subclasses
     * must fill in the provided ContentValues with appropriate values.
     * @param values a ContentValues map, ready to fill in
     * @param dbAdapter
     */
    public abstract void fillInContentValues(ContentValues values, DBAdapter dbAdapter);

    public void buildFromCursor(Cursor cursor, DBAdapter adapter) {
        setDatabaseId(cursor.getLong(cursor.getColumnIndex(Schema.Table.ID_COL)));

        buildSubclassFromCursor(cursor, adapter);

        if (!hasPersistenceHandler()) {
            adapter.acquire(this);
        }
    }

    public static <T extends DatabaseObject> T createFromCursor(Class<T> klass, Cursor cursor, DBAdapter adapter) {
        try {
            T returnVal = klass.newInstance();

            returnVal.buildFromCursor(cursor, adapter);

            return returnVal;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T extends DatabaseObject> List<T> createListFromCursor(Class<T> klass, Cursor cursor, DBAdapter adapter) {
        List<T> returnVal = new ArrayList<>();

        while (cursor.moveToNext()) {
            returnVal.add(createFromCursor(klass, cursor, adapter));
        }

        return returnVal;
    }

    protected boolean hasPersistenceHandler() {
        return persistenceHandler != null;
    }

    public void deleteFromDatabase() {
        this.persistenceHandler.delete(this);
    }

    public long persistToDatabase() {
        return this.persistenceHandler.persist(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        isDirty = true;
    }
}