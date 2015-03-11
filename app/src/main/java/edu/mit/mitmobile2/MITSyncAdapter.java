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

//        List<MITShuttleRoute> routes = (List<MITShuttleRoute>) mitapiClient.get(Constants.SHUTTLES, Constants.Shuttles.ALL_ROUTES_PATH, null, null);
//        Timber.d("After Retrofit, size = " + routes.size());
    }
}
