package edu.mit.mitmobile2.shared.android;

import android.support.annotation.NonNull;

/**
 * Created by grmartin on 4/21/15.
 */
public interface ValueSet {
    <T> boolean setValueForSetField(T value, @NonNull String field);
}
