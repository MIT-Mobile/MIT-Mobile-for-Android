package edu.mit.mitmobile2;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.mit.mitmobile2.shuttles.MITShuttlesProvider;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import timber.log.Timber;

public class MITSyncAdapter extends AbstractThreadedSyncAdapter {

    private MITAPIClient mitapiClient;
    private HashMap<String, String> map = new HashMap<>();

    public MITSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mitapiClient = new MITAPIClient(context);
        MITAPIClient.init(context);

        map.put(MITShuttlesProvider.CONTENT_URI.toString() + "/routes", Schema.Route.ROUTE_ID);
        map.put(MITShuttlesProvider.CONTENT_URI.toString() + "/stops", Schema.Stop.STOP_ID);
    }

    public MITSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Timber.d("Sync Called");

        if (mitapiClient == null) {
            mitapiClient = new MITAPIClient(getContext());
            MITAPIClient.init(getContext());
        }

        String module = extras.getString("module");
        String path = extras.getString("path");
        String uri = extras.getString("uri");

        Gson gson = new Gson();

        HashMap<String, String> pathParams = null;
        HashMap<String, String> queryparams = null;

        Type hashMapType = new TypeToken<HashMap<String, String>>() {
        }.getType();

        if (extras.containsKey("paths")) {
            String p = extras.getString("paths");
            pathParams = gson.fromJson(p, hashMapType);
        }

        if (extras.containsKey("queries")) {
            String q = extras.getString("queries");
            queryparams = gson.fromJson(q, hashMapType);
        }

        Timber.d("Retrieved info from bundle");

        Object object = mitapiClient.get(getContext(), module, path, pathParams, queryparams);

        if (object instanceof List) {
            Timber.d("Is List");
            List<DatabaseObject> objects = (List<DatabaseObject>) object;
            for (DatabaseObject obj : objects) {
                insertObject(uri, obj);
            }
        } else {
            Timber.d("Is single object");
            insertObject(uri, (DatabaseObject) object);
        }

    }

    private void insertObject(String uri, DatabaseObject object) {
        DatabaseObject dbObject = object;
        ContentValues contentValues = new ContentValues();
        dbObject.fillInContentValues(contentValues, MitMobileApplication.dbAdapter);

        Cursor cursor = getContext().getContentResolver().query(Uri.parse(uri), null, map.get(uri) + "=\'" + contentValues.get(map.get(uri)) + "\'", null, null);
        if (cursor.getCount() > 0) {
            getContext().getContentResolver().update(Uri.parse(uri), contentValues, map.get(uri) + "=\'" + contentValues.get(map.get(uri)) + "\'", null);
        } else {
            getContext().getContentResolver().insert(Uri.parse(uri), contentValues);
        }

        cursor.close();
    }
}
