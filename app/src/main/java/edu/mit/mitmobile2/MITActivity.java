package edu.mit.mitmobile2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by sseligma on 2/24/15.
 */
public class MITActivity extends Activity {

    protected String TAG;
    protected Context mContext;
    protected MITAPIClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mContext = this;
        this.apiClient = new MITAPIClient(mContext);
    }
}
