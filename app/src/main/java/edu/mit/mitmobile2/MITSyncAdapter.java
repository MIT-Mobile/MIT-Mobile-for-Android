package edu.mit.mitmobile2;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import timber.log.Timber;

public class MITSyncAdapter extends AbstractThreadedSyncAdapter {

    private MITAPIClient mitapiClient;

    public MITSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mitapiClient = new MITAPIClient(context);
        MITAPIClient.init(context);
    }

    public MITSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Timber.d("Sync Called");

        //TODO: Make retrofit calls & load into database

        /*String module = extras.getString("module");
        String path = extras.getString("path");

        List<MITShuttleRoute> routes = (List<MITShuttleRoute>) mitapiClient.get(module, path, null, null);

        Timber.d("After Retrofit, size = " + routes.size());

        List<DatabaseObject> updatedRoutes = new ArrayList<>();

        DBAdapter dbAdapter = MitMobileApplication.dbAdapter;

        // Get previous IDs
        Set<String> ids = dbAdapter.getAllIds(Schema.Route.TABLE_NAME, Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID);

        for (MITShuttleRoute r : routes) {
            if (ids.contains(r.getId())) {
                ContentValues values = new ContentValues();
                r.fillInContentValues(values, dbAdapter);
                dbAdapter.db.update(Schema.Route.TABLE_NAME, values,
                        Schema.Route.ROUTE_ID + " = \'" + r.getId() + "\'", null);
            } else {
                updatedRoutes.add(r);
            }
        }

        dbAdapter.batchPersist(updatedRoutes, Schema.Route.TABLE_NAME);*/

    }
}
