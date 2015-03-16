package edu.mit.mitmobile2;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.shuttles.MITShuttlesProvider;
import timber.log.Timber;

public class MITSyncAdapter extends AbstractThreadedSyncAdapter {

    private MITAPIClient mitapiClient;
    private HashMap<String, String> map = new HashMap<>();

    public MITSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mitapiClient = new MITAPIClient(context);
        MITAPIClient.init(context);

        map.put(MITShuttlesProvider.ALL_ROUTES_URI.toString(), Schema.Route.ROUTE_ID);
        map.put(MITShuttlesProvider.STOPS_URI.toString(), Schema.Stop.STOP_ID);
        map.put(MITShuttlesProvider.PREDICTIONS_URI.toString(), Schema.Stop.STOP_ID);
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

        String module = extras.getString(Constants.Shuttles.MODULE_KEY);
        String path = extras.getString(Constants.Shuttles.PATH_KEY);
        String uri = extras.getString(Constants.Shuttles.URI_KEY);

        if (module == null || path == null || uri == null) {
            // Default: Get all routes data for periodic sync
            module = Constants.SHUTTLES;
            path = Constants.Shuttles.ALL_ROUTES_PATH;
            uri = MITShuttlesProvider.ALL_ROUTES_URI.toString();
        }

        if (uri.contains("predictions")) {
            handleDualAgenciesForPredictions(module, path, uri, extras);
        } else {
            Gson gson = new Gson();

            HashMap<String, String> pathParams = null;
            HashMap<String, String> queryparams = null;

            Type hashMapType = new TypeToken<HashMap<String, String>>() {
            }.getType();

            if (extras.containsKey(Constants.Shuttles.PATHS_KEY)) {
                String p = extras.getString(Constants.Shuttles.PATHS_KEY);
                pathParams = gson.fromJson(p, hashMapType);
            }

            if (extras.containsKey(Constants.Shuttles.QUERIES_KEY)) {
                String q = extras.getString(Constants.Shuttles.QUERIES_KEY);
                queryparams = gson.fromJson(q, hashMapType);
            }

            Timber.d("Retrieved info from bundle:" + module + ", " + path + ", " + uri);

            requestDataAndStoreInDb(module, path, uri, pathParams, queryparams);
            Timber.d("Notifying URI: " + uri);
            getContext().getContentResolver().notifyChange(Uri.parse(uri), null);
        }
    }

    private void requestDataAndStoreInDb(String module, String path, String uri, HashMap<String, String> pathParams, HashMap<String, String> queryparams) {
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
        if (object == null) {
            return;
        }

        DatabaseObject dbObject = object;
        ContentValues contentValues = new ContentValues();
        dbObject.fillInContentValues(contentValues, MitMobileApplication.dbAdapter);

        String selection = map.get(uri) + "=\'" + contentValues.get(map.get(uri)) + "\'";

        if (uri.contains("predictions")) {
            //special case for predictions
            contentValues.remove(Schema.Route.ROUTE_ID);
            contentValues.remove(Schema.Stop.STOP_ID);
            getContext().getContentResolver().update(Uri.parse(uri), contentValues, selection, null);
        } else {

            Cursor cursor = getContext().getContentResolver().query(Uri.parse(uri + "/check"), null, selection, null, null);
            if (cursor.getCount() > 0) {
                getContext().getContentResolver().update(Uri.parse(uri), contentValues, selection, null);
            } else {
                getContext().getContentResolver().insert(Uri.parse(uri), contentValues);
            }

            cursor.close();
        }
    }

    private void handleDualAgenciesForPredictions(String module, String path, String uri, Bundle extras) {

        String mitTuples = extras.getString(Constants.Shuttles.MIT_TUPLES_KEY);
        String crTuples = extras.getString(Constants.Shuttles.CR_TUPLES_KEY);

        if (!TextUtils.isEmpty(mitTuples)) {
            HashMap<String, String> queryparams = new HashMap<>();
            queryparams.put("agency", "mit");
            queryparams.put("stops", mitTuples);
            requestDataAndStoreInDb(module, path, uri, null, queryparams);
        }

        if (!TextUtils.isEmpty(crTuples)) {
            HashMap<String, String> queryparams = new HashMap<>();
            queryparams.put("agency", "charles-river");
            queryparams.put("stops", crTuples);
            requestDataAndStoreInDb(module, path, uri, null, queryparams);
        }

        Timber.d("Notifying URI: " + uri);
        getContext().getContentResolver().notifyChange(MITShuttlesProvider.ALL_ROUTES_URI, null);
    }
}
