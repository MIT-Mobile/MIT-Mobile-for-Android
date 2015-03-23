package edu.mit.mitmobile2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.util.Observable;
import java.util.Observer;

public abstract class DatabaseObject extends Observable implements Observer {
    protected static abstract class PersistenceHandler<DataObjectType, DatabaseObjectType> {
        protected DatabaseObjectType database;

        public PersistenceHandler(DatabaseObjectType database) {
            this.database = database;
        }

        public abstract long persist(DataObjectType object) throws SQLiteException;
        public abstract void delete(DataObjectType object);
    }

    public static final long INVALID_ID = Long.MIN_VALUE;

    private long databaseId = INVALID_ID;
    private boolean isDirty = false;
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