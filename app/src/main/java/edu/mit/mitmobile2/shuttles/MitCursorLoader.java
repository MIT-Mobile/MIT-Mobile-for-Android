package edu.mit.mitmobile2.shuttles;

import android.content.Context;
import android.net.Uri;

import edu.mit.mitmobile2.AbstractObservableCursorLoader;

public class MitCursorLoader extends AbstractObservableCursorLoader {
    protected MitCursorLoader(Context context, Uri uri) {
        super(context, uri);
    }
}
