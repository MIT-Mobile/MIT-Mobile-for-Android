
package edu.mit.mitmobile2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MITSyncService extends Service {

    private MITSyncAdapter syncAdapter = null;
    private final Object syncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new MITSyncAdapter(getApplicationContext(), true);
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }

}
