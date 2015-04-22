package edu.mit.mitmobile2.shared.android;

import java.net.URI;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by grmartin on 4/21/15.
 */
public class IntentValueSet extends Intent implements ValueSet {
    public final static String INTENT_DATA_FIELD_NAME = "0144D785-7D08-4503-9DE7-E0A35D715A77";
    public final static String INTENT_TYPE_FIELD_NAME = "31029BD3-EBA8-4D25-B444-A1A625DACBEA";

    public IntentValueSet() {
        super();
    }

    public IntentValueSet(Intent o) {
        super(o);
    }

    public IntentValueSet(String action) {
        super(action);
    }

    public IntentValueSet(String action, Uri uri) {
        super(action, uri);
    }

    public IntentValueSet(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public IntentValueSet(String action, Uri uri, Context packageContext, Class<?> cls) {
        super(action, uri, packageContext, cls);
    }

    @Override
    public <T> boolean setValueForSetField(T value, @NonNull String field) {
        if (TextUtils.isEmpty(field)) {
            return false;
        }

        switch (field) {
            case INTENT_DATA_FIELD_NAME:
                this.setData((Uri)
                        (value instanceof Uri ? (Uri) value :
                                (value instanceof URI ? Uri.parse(((URI) value).toString()) :
                                        (value instanceof URL ? Uri.parse(((URL) value).toString()) :
                                                (value instanceof String ? Uri.parse((String) value) : Uri.encode(String.valueOf(value)))))));
                break;
            case INTENT_TYPE_FIELD_NAME:
                this.setType(value == null ? null : String.valueOf(value));
                break;
            default: {
                Bundle bnd = this.getExtras();

                if (bnd == null) {
                    this.putExtras(new Bundle());
                    bnd = this.getExtras();

                    assert bnd != null;
                }

                return BundleUtils.omniPut(bnd, field, value);
            }
        }

        return true;
    }
}
