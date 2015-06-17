package edu.mit.mitmobile2.qrreader.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.qrreader.ScannerManager;
import edu.mit.mitmobile2.qrreader.models.QrReaderDetails;
import edu.mit.mitmobile2.qrreader.models.QrReaderResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScannerHistoryDetailActivity extends ActionBarActivity {

    public static final String KEY_EXTRAS_SCANNER_RESULT = "key_extras_scanner_result";
    private static final String KEY_STATE_SCANNER_DETAILS = "key_state_scanner_details";

    private QrReaderResult result;
    private QrReaderDetails details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_history_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(KEY_EXTRAS_SCANNER_RESULT)) {
            result = extras.getParcelable(KEY_EXTRAS_SCANNER_RESULT);
        }

        if (savedInstanceState == null) {
            fetchScannerDetails(result);
        } else {
            if (savedInstanceState.containsKey(KEY_STATE_SCANNER_DETAILS)) {

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scanner_history_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Network */

    private void fetchScannerDetails(QrReaderResult result) {
        ScannerManager.getScannerDetails(this, result, new Callback<QrReaderDetails>() {

            @Override
            public void success(QrReaderDetails details, Response response) {
                ScannerHistoryDetailActivity.this.details = details;
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }
 }
