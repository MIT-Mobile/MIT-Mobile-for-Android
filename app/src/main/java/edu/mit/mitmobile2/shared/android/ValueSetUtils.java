package edu.mit.mitmobile2.shared.android;

import java.util.Arrays;

import android.support.annotation.NonNull;

import edu.mit.mitmobile2.shared.logging.LoggingManager.Log;
import edu.mit.mitmobile2.shared.functional.Test;

/**
 * Created by grmartin on 4/21/15.
 */
public final class ValueSetUtils {
    public static final String LOGGER_KEY = "ValueSetUtils";

    @SuppressWarnings("unchecked")
    public static <T> boolean addFieldIfValid(@NonNull final ValueSet valueSet, @NonNull final Test<T> valueTest, @NonNull final String field, final Object... values) {
        for (Object value : values) {
            try {
                return (valueTest.test((T) value) && valueSet.setValueForSetField(value, field));
            } catch (Throwable t) {
                Log.e(LOGGER_KEY, "Exception ignored in addFieldIfValid(...) =>\n" + valueSet + "\n" + valueTest + "\n" + field + "\n" + Arrays.toString(values), t);
            }
        }

        return false;
    }

    public static <T> boolean addFieldIfValid(@NonNull final ValueSet valueSet, @NonNull final Test<T> valueTest, @NonNull final String field, final T value) {
        boolean tst = false;
        boolean set = false;

        boolean returnVal =
                (tst = valueTest.test(value)) &&
                        (set = valueSet.setValueForSetField(value, field));

        Log.v(LOGGER_KEY, "addFieldIfValid(...) {Test="+tst+", Set="+set+"} =>\n" + valueSet + "\n" + valueTest + "\n" + field + "\n" + value);

        return returnVal;
    }
}
