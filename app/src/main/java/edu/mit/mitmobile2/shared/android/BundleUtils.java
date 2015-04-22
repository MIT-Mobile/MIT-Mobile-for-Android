package edu.mit.mitmobile2.shared.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import edu.mit.mitmobile2.shared.logging.LoggingManager.Log;

/**
 * Created by grmartin on 4/21/15.
 */
public final class BundleUtils {
    public static String LOGGER_KEY = "BundleUtils";

    public static boolean omniPut(final Bundle bundle, final String key, final Object value) {
        try {
            if      (value instanceof Boolean)        {bundle.putBoolean(key, (Boolean) value); return true;}
            else if (value instanceof Character)      {bundle.putChar(key, (Character) value); return true;}
            else if (value instanceof Byte)           {bundle.putByte(key, (Byte) value); return true;}
            else if (value instanceof Short)          {bundle.putShort(key, (Short) value); return true;}
            else if (value instanceof Float)          {bundle.putFloat(key, (Float) value); return true;}
            else if (value instanceof Bundle)         {bundle.putBundle(key, (Bundle) value); return true;}
            else if (value instanceof List)           return handleOmniPutList(bundle, key, (List<?>) value);
            else if (value instanceof Map)            return handleOmniPutMap(bundle, key, (Map<?, ?>) value);
            else if (value instanceof boolean[])      {bundle.putBooleanArray(key, (boolean[]) value); return true;}
            else if (value instanceof byte[])         {bundle.putByteArray(key, (byte[]) value); return true;}
            else if (value instanceof short[])        {bundle.putShortArray(key, (short[]) value); return true;}
            else if (value instanceof char[])         {bundle.putCharArray(key, (char[]) value); return true;}
            else if (value instanceof float[])        {bundle.putFloatArray(key, (float[]) value); return true;}
            else if (value instanceof CharSequence[]) {bundle.putCharSequenceArray(key, (CharSequence[]) value); return true;}
            else if (value instanceof CharSequence)   {bundle.putCharSequence(key, (CharSequence) value); return true;}
            else if (value instanceof Parcelable)     {bundle.putParcelable(key, (Parcelable) value); return true;}
            else if (value instanceof Parcelable[])   {bundle.putParcelableArray(key, (Parcelable[]) value); return true;}
            else if (value instanceof Serializable)   {bundle.putSerializable(key, (Serializable) value); return true;}
        } catch (Throwable t) {
            Log.e(LOGGER_KEY, "Exception ignored in omniPut(...) =>\n"+bundle+"\n"+key+"\n"+value, t);
        }
        return false;
    }

    private static boolean handleOmniPutMap(Bundle bundle, String key, Map<?, ?> value) {
        try {
            bundle.putSerializable(key, (Serializable) value);
            return true;
        } catch (Throwable t) {
            Log.e(LOGGER_KEY, "Exception ignored in handleOmniPutMap(...) =>\n"+bundle+"\n"+key+"\n"+value, t);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static boolean handleOmniPutList(Bundle bundle, String key, List<?> value) {
        try {
            if (value instanceof SparseArray) {
                bundle.putSparseParcelableArray(key, (SparseArray<? extends Parcelable>) value);
                return true;
            }else if (value instanceof ArrayList) {
                bundle.putParcelableArrayList(key, (ArrayList<? extends Parcelable>) value);
                return true;
            }else {
                bundle.putParcelableArrayList(key, new ArrayList(value));
                return true;
            }
        } catch (Throwable t) {
            Log.e(LOGGER_KEY, "Exception ignored in handleOmniPutList(...) =>\n"+bundle+"\n"+key+"\n"+value, t);
        }
        return false;
    }

}
