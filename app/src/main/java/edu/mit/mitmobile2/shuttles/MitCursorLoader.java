package edu.mit.mitmobile2.shuttles;

import android.content.Context;
import android.net.Uri;

import edu.mit.mitmobile2.AbstractObservableCursorLoader;

public class MitCursorLoader extends AbstractObservableCursorLoader {
    public MitCursorLoader(Context context, Uri uri) {
        super(context, uri);
    }

    public MitCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }
}
