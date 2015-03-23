package edu.mit.mitmobile2.shuttles;

import android.os.Bundle;

import butterknife.ButterKnife;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SoloMapActivity;
import edu.mit.mitmobile2.shuttles.fragment.ShuttleStopFragment;

public class ShuttleStopActivity extends SoloMapActivity {

    private ShuttleStopFragment shuttleStopFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        shuttleStopFragment = new ShuttleStopFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.prediction_fragment, shuttleStopFragment).commit();
    }
}
