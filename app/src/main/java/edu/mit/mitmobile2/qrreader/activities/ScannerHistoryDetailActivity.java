package edu.mit.mitmobile2.qrreader.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.zxing.common.StringUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.qrreader.ScannerManager;
import edu.mit.mitmobile2.qrreader.adapters.ScannerHistoryAdapter;
import edu.mit.mitmobile2.qrreader.adapters.ScannerHistoryDetailAdapter;
import edu.mit.mitmobile2.qrreader.models.QrReaderDetails;
import edu.mit.mitmobile2.qrreader.models.QrReaderDetailsAction;
import edu.mit.mitmobile2.qrreader.models.QrReaderResult;
import edu.mit.mitmobile2.qrreader.utils.ScannerImageUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScannerHistoryDetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String KEY_EXTRAS_SCANNER_RESULT = "key_extras_scanner_result";
    private static final String KEY_STATE_SCANNER_DETAILS = "key_state_scanner_details";

    private ImageView imageViewCode;
    private ListView listView;

    private ScannerHistoryDetailAdapter adapter;

    private QrReaderResult result;
    private QrReaderDetails details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_history_detail);

        imageViewCode = (ImageView) findViewById(R.id.scanner_detail_iv_image);
        listView = (ListView) findViewById(R.id.list);

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

        adapter = new ScannerHistoryDetailAdapter(result);
        listView.setAdapter(adapter);

        File file = new File(ScannerImageUtils.getCachePath(), result.getImageName());
        Picasso.with(this).load(file).into(imageViewCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scanner_history_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            // TODO: add share logic here
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = adapter.getItem(position);
        if (item != null && item instanceof QrReaderDetailsAction) {
            QrReaderDetailsAction action = (QrReaderDetailsAction) item;
            try {
                new URL(action.getUrl());

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(action.getUrl()));
                startActivity(i);
            } catch (MalformedURLException e) {
                // not an url, just skip
            }
        }
    }

    private void displayScannerResultDetails(QrReaderDetails details) {
        boolean isServerScannerCode = !TextUtils.isEmpty(details.getType()) && details.getActions() != null && details.getActions().size() > 0;

        if (!isServerScannerCode) {
            details.setDisplayName(result.getText());

            try {
                new URL(result.getText());

                details.setType(QrReaderDetails.TYPE_URL);

                QrReaderDetailsAction action = new QrReaderDetailsAction();
                action.setTitle(getString(R.string.scan_open_in_browser));
                action.setUrl(result.getText());

                List<QrReaderDetailsAction> actions = new ArrayList<>();
                actions.add(action);

                details.setActions(actions);
            } catch (MalformedURLException e) {
                // string is not an URL

                details.setType(QrReaderDetails.TYPE_OTHER);
            }
        }

        adapter.updateDetails(details);
    }

    /* Network */

    private void fetchScannerDetails(QrReaderResult result) {
        ScannerManager.getScannerDetails(this, result, new Callback<QrReaderDetails>() {

            @Override
            public void success(QrReaderDetails details, Response response) {
                ScannerHistoryDetailActivity.this.details = details;

                displayScannerResultDetails(ScannerHistoryDetailActivity.this.details);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }
}
