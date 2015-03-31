package edu.mit.mitmobile2;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.shuttles.MITShuttlesProvider;
import timber.log.Timber;

public class MITSyncAdapter extends AbstractThreadedSyncAdapter {

    private MITAPIClient mitapiClient;
    private HashMap<Integer, String> map = new HashMap<>();

    public MITSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mitapiClient = new MITAPIClient(context);
        MITAPIClient.init(context);

        map.put(MITShuttlesProvider.ROUTES, Schema.Route.ROUTE_ID);
        map.put(MITShuttlesProvider.STOPS, Schema.Stop.STOP_ID);
        map.put(MITShuttlesProvider.PREDICTIONS, Schema.Stop.STOP_ID);
        map.put(MITShuttlesProvider.ROUTE_ID, Schema.Vehicle.ROUTE_ID);
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

        int uriType = getUriType(uri);

        if (uriType == MITShuttlesProvider.PREDICTIONS) {
            Timber.d("Predictions call received: " + System.currentTimeMillis());
            handleDualAgenciesForPredictions(module, path, uri, extras);
        } else {
            processParameters(extras, module, path, uri);

            PreferenceUtils.getDefaultSharedPreferencesMultiProcess(getContext()).edit().putLong(Constants.ROUTES_TIMESTAMP, System.currentTimeMillis()).apply();
            Timber.d("Saved Routes Timestamp");

            if (extras.containsKey("return")) {
                getContext().getContentResolver().notifyChange(Uri.parse(extras.getString("return")), null);
            } else {
                Timber.d("Notifying URI: " + uri);
                getContext().getContentResolver().notifyChange(Uri.parse(uri), null);
            }
        }
    }

    private void processParameters(Bundle extras, String module, String path, String uri) {
        Timber.d("Route call received: " + System.currentTimeMillis());
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

        PreferenceUtils.getDefaultSharedPreferencesMultiProcess(getContext()).edit().putLong(Constants.PREDICTIONS_TIMESTAMP, System.currentTimeMillis()).apply();
        Timber.d("Saved Predictions Timestamp");
        getContext().getContentResolver().notifyChange(MITShuttlesProvider.ALL_ROUTES_URI, null);
    }

    private void requestDataAndStoreInDb(String module, String path, String uri, HashMap<String, String> pathParams, HashMap<String, String> queryparams) {
        Object object = mitapiClient.get(getContext(), module, path, pathParams, queryparams);

        int uriType = getUriType(uri);

        if (object instanceof List) {
            Timber.d("Is List");
            if (uriType == MITShuttlesProvider.PREDICTIONS) {
                //noinspection unchecked
                bulkInsertPredictions(uri, (List<DatabaseObject>) object);
            } else {
                //noinspection unchecked
                bulkInsertObjects(uri, (List<DatabaseObject>) object);
            }
        } else {
            Timber.d("Is single object");
            if (uriType == MITShuttlesProvider.PREDICTIONS) {
                insertPredictionObject(uri, (DatabaseObject) object);
            } else {
                insertObject(uri, (DatabaseObject) object);
            }
        }
    }

    private void bulkInsertObjects(String uri, List<DatabaseObject> objects) {
        ContentValues[] values = generateContentValues(objects);

        /**
         * For routes/stops: If ONE of the IDs is in the DB, ALL of them are. So only check 1 to save time
         */
        String selection = getSelectionString(getUriType(uri), values[0]);

        Cursor cursor = getContext().getContentResolver().query(Uri.parse(uri + "/check"), null, selection, null, null);
        boolean containsData = cursor.moveToFirst();
        cursor.close();

        if (containsData) {
            Timber.d("Updating values");
            batchUpdateAllValues(uri, values);
        } else {
            getContext().getContentResolver().bulkInsert(Uri.parse(uri), values);
        }
    }

    private void bulkInsertPredictions(String uri, List<DatabaseObject> objects) {
        ContentValues[] values = generateContentValues(objects);

        //special case for predictions
        List<ContentProviderOperation> operations = new ArrayList<>();

        for (ContentValues v : values) {
            if (v.get(Schema.Route.PREDICTABLE) == true) {
                String selection = getSelectionString(getUriType(uri), v);

                ContentProviderOperation operation = ContentProviderOperation.newUpdate(Uri.parse(uri))
                        .withValue(Schema.Stop.PREDICTIONS, v.getAsString(Schema.Stop.PREDICTIONS))
                        .withValue(Schema.Stop.TIMESTAMP, System.currentTimeMillis())
                        .withSelection(selection, null)
                        .build();

                operations.add(operation);
            }
        }

        try {
            getContext().getContentResolver().applyBatch(MitMobileApplication.AUTHORITY, (ArrayList<ContentProviderOperation>) operations);
        } catch (RemoteException | OperationApplicationException e) {
            Timber.e(e, "Batch Update");
        }
    }

    private void insertObject(String uri, DatabaseObject object) {
        if (object == null) {
            return;
        }

        DatabaseObject dbObject = object;
        ContentValues contentValues = new ContentValues();
        dbObject.fillInContentValues(contentValues, DBAdapter.getInstance());

        int uriType = getUriType(uri);

        String selection = getSelectionString(uriType, contentValues);

        if (uriType == MITShuttlesProvider.ROUTES) {
            Cursor cursor = getContext().getContentResolver().query(Uri.parse(uri + "/check"), null, selection, null, null);
            boolean existsInDb = cursor.moveToFirst();
            cursor.close();

            if (existsInDb) {
                getContext().getContentResolver().update(Uri.parse(uri), contentValues, selection, null);
            } else {
                getContext().getContentResolver().insert(Uri.parse(uri), contentValues);
            }
        } else {
            getContext().getContentResolver().update(Uri.parse(uri), contentValues, selection, null);
        }
    }

    private void insertPredictionObject(String uri, DatabaseObject object) {
        if (object == null) {
            return;
        }

        DatabaseObject dbObject = object;
        ContentValues contentValues = new ContentValues();
        dbObject.fillInContentValues(contentValues, DBAdapter.getInstance());

        String selection = getSelectionString(getUriType(uri), contentValues);

        //special case for predictions
        contentValues.remove(Schema.Route.ROUTE_ID);
        contentValues.remove(Schema.Stop.STOP_ID);
        getContext().getContentResolver().update(Uri.parse(uri), contentValues, selection, null);
    }

    private ContentValues[] generateContentValues(List<DatabaseObject> objects) {
        ContentValues[] values = new ContentValues[objects.size()];

        for (int i = 0; i < objects.size(); i++) {
            ContentValues contentValues = new ContentValues();
            objects.get(i).fillInContentValues(contentValues, DBAdapter.getInstance());
            values[i] = contentValues;
        }
        return values;
    }

    private void batchUpdateAllValues(String uri, ContentValues[] values) {
        List<ContentProviderOperation> operations = new ArrayList<>();

        for (ContentValues v : values) {
            String selection = getSelectionString(getUriType(uri), v);

            ContentProviderOperation operation = ContentProviderOperation.newUpdate(Uri.parse(uri))
                    .withValues(v)
                    .withSelection(selection, null)
                    .build();

            operations.add(operation);
        }

        try {
            getContext().getContentResolver().applyBatch(MitMobileApplication.AUTHORITY, (ArrayList<ContentProviderOperation>) operations);
        } catch (RemoteException | OperationApplicationException e) {
            Timber.e(e, "Batch Update");
        }
    }

    private int getUriType(String uri) {
        return MITShuttlesProvider.sURIMatcher.match(Uri.parse(uri));
    }

    private String getSelectionString(int uriType, ContentValues v) {
        return map.get(uriType) + "=\'" + v.get(map.get(uriType)) + "\'";
    }
}
