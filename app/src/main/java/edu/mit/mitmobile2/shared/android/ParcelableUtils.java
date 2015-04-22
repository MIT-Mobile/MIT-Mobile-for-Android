package edu.mit.mitmobile2.shared.android;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by grmartin on 4/21/15.
 */
public final class ParcelableUtils {
    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> T copyParcelable(T parcelable) {
        Parcel p = Parcel.obtain();
        p.writeValue(parcelable);
        p.setDataPosition(0);
        T r = (T)p.readValue(parcelable.getClass().getClassLoader());
        p.recycle();
        return r;
    }
}
