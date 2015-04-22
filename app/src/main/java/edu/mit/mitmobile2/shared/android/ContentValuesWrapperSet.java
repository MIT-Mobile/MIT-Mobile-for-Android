package edu.mit.mitmobile2.shared.android;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by grmartin on 4/21/15.
 */
public class ContentValuesWrapperSet implements ValueSet {
    private ContentValues values;

    public ContentValuesWrapperSet(ContentValues values) {
        this.values = values;
    }

    public ContentValuesWrapperSet() {
        this(new ContentValues());
    }

    public void put(String key, String value) {
        values.put(key, value);
    }

    public void putAll(ContentValues other) {
        values.putAll(other);
    }

    public void put(String key, Byte value) {
        values.put(key, value);
    }

    public void put(String key, Short value) {
        values.put(key, value);
    }

    public void put(String key, Integer value) {
        values.put(key, value);
    }

    public void put(String key, Long value) {
        values.put(key, value);
    }

    public void put(String key, Float value) {
        values.put(key, value);
    }

    public void put(String key, Double value) {
        values.put(key, value);
    }

    public void put(String key, Boolean value) {
        values.put(key, value);
    }

    public void put(String key, byte[] value) {
        values.put(key, value);
    }

    public void putNull(String key) {
        values.putNull(key);
    }

    public int size() {
        return values.size();
    }

    public void remove(String key) {
        values.remove(key);
    }

    public void clear() {
        values.clear();
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public String getAsString(String key) {
        return values.getAsString(key);
    }

    public Long getAsLong(String key) {
        return values.getAsLong(key);
    }

    public Integer getAsInteger(String key) {
        return values.getAsInteger(key);
    }

    public Short getAsShort(String key) {
        return values.getAsShort(key);
    }

    public Byte getAsByte(String key) {
        return values.getAsByte(key);
    }

    public Double getAsDouble(String key) {
        return values.getAsDouble(key);
    }

    public Float getAsFloat(String key) {
        return values.getAsFloat(key);
    }

    public Boolean getAsBoolean(String key) {
        return values.getAsBoolean(key);
    }

    public byte[] getAsByteArray(String key) {
        return values.getAsByteArray(key);
    }

    public ContentValues copyContentValues() {
        return ParcelableUtils.copyParcelable(this.values);
    }

    public boolean shouldDestroy(boolean actuallyDoIt) {
        this.destroy();
        return  actuallyDoIt;
    }

    public boolean shouldDestroyIfFails(boolean dontDoIt) {
        if (!dontDoIt) this.destroy();
        return  dontDoIt;
    }

    public void destroy() {
        Log.d(this.getClass().getSimpleName(), "destroy(...) Destroying => "+this.toString());
        this.values = null;
    }

    public ContentValues returnDestroyContentValues() {
        ContentValues returnValue = this.values;
        this.destroy();
        return returnValue;
    }

    public boolean isDestroyed() {
        return this.values == null;
    }

    @Override
    public <T> boolean setValueForSetField(T value, @NonNull String field) {
        return ContentValuesUtils.omniPut(this.values, field, value);
    }

    @Override
    public String toString() {
        return "ContentValuesWrapperSet{" +
                "values=" + values +
                '}';
    }
}
