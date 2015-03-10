package edu.mit.mitmobile2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by sseligma on 2/24/15.
 */
public class MITActivity extends ActionBarActivity {

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
