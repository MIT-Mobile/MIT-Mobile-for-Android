package edu.mit.mitmobile2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MITAuthenticatorService extends Service {

    private MITAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new MITAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
