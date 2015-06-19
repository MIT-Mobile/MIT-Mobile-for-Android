package edu.mit.mitmobile2.qrreader.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;

public class ScannerAdvancedActivity extends MITActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.scanner_preferences);
        }
    }
}
