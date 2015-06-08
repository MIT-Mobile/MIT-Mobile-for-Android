package edu.mit.mitmobile2.about.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import edu.mit.mitmobile2.R;

/**
 * Created by serg on 6/8/15.
 */
public class CreditsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_credits);
    }
}
