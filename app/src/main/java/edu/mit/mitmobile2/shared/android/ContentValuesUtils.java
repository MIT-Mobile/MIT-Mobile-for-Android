package edu.mit.mitmobile2.shared.android;

import android.content.ContentValues;

import edu.mit.mitmobile2.shared.logging.LoggingManager.Log;

/**
 * Created by grmartin on 4/21/15.
 */
public final class ContentValuesUtils {
    public static String LOGGER_KEY = "ContentValuesUtils";

    public static boolean omniPut(final ContentValues set, final String key, final Object value) {
        try {
            if (value == null) { set.putNull(key); return true; }
            if (value instanceof String) { set.put(key, (String)value); return true; }
            if (value instanceof Byte) { set.put(key, (Byte)value); return true; }
            if (value instanceof Short) { set.put(key, (Short)value); return true; }
            if (value instanceof Integer) { set.put(key, (Integer)value); return true; }
            if (value instanceof Long) { set.put(key, (Long)value); return true; }
            if (value instanceof Float) { set.put(key, (Float)value); return true; }
            if (value instanceof Double) { set.put(key, (Double)value); return true; }
            if (value instanceof Boolean) { set.put(key, (Boolean) value); return true; }
            if (value.getClass().isArray() &&
                    byte.class.isAssignableFrom(value.getClass().getComponentType())) { set.put(key, (byte[]) value); return true; }
        } catch (Throwable t) {
            Log.e(LOGGER_KEY, "Exception ignored in omniPut(...) =>\n" + set + "\n" + key + "\n" + value, t);
        }
        return false;
    }
}
