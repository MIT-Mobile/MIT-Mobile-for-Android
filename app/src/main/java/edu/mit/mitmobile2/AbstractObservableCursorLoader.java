package edu.mit.mitmobile2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import java.util.Arrays;

/**
 * This class is just like a regular {@link android.support.v4.content.CursorLoader} but, unlike a
 * regular CursorLoader, it will notify the caller/user when the underlying ContentProvider's data
 * has changed.
 *
 * A regular CursorLoader will only notify the caller/user when the cached data currently backed by
 * the cursor changes, not when the underlying data in the ContentProvider changes.
 *
 * Created by Anton Spaans on 1/13/15.
 */
public abstract class AbstractObservableCursorLoader extends CursorLoader {
    private final ForceLoadContentObserver observer = new ForceLoadContentObserver();

    protected AbstractObservableCursorLoader(Context context, Uri uri) {
        super(context, uri, null, null, null, null);
        getContext().getContentResolver().registerContentObserver(getUri(), true, observer);
    }

    protected AbstractObservableCursorLoader(Context context, Uri uri, String[] projection, String selection,
                                             String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        getContext().getContentResolver().registerContentObserver(getUri(), true, observer);
    }

    @Override
    public Cursor loadInBackground() {
        try {
            return super.loadInBackground();
        }
        catch (RuntimeException re) {
            StringBuilder sb = new StringBuilder();
            sb.append("  uri=").append(getUri().toString()).append("\n");
            sb.append("  projection=").append(Arrays.toString(getProjection())).append("\n");
            sb.append("  where=").append(getSelection()).append("\n");
            sb.append("  selection=").append(Arrays.toString(getSelectionArgs())).append("\n");
            sb.append("  sort=").append(getSortOrder()).append("\n");

            Log.e("CursorLoader", "Error in query:\n" + sb.toString());
            throw re;
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        getContext().getContentResolver().unregisterContentObserver(observer);
    }
}

