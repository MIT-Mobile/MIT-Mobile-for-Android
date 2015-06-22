package edu.mit.mitmobile2.qrreader;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.util.Date;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.qrreader.activities.ScannerHistoryActivity;
import edu.mit.mitmobile2.qrreader.activities.ScannerInfoActivity;
import edu.mit.mitmobile2.qrreader.models.QRReaderResult;
import edu.mit.mitmobile2.shared.MITContentProvider;

public class QrReaderFragment extends Fragment implements QRCodeReaderView.OnQRCodeReadListener, View.OnClickListener {

    private TextView textViewDisclaimer;
    private TextView textViewInfo;
    private TextView textViewAdvanced;
    private QRCodeReaderView qrCodeReaderView;

    private int savedOrientation;

    public QrReaderFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // block landscape orientation (restore in onDetach)
        savedOrientation = activity.getRequestedOrientation();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_qrreader, null);

        textViewDisclaimer = (TextView) view.findViewById(R.id.scanner_tv_disclaimer);
        textViewInfo = (TextView) view.findViewById(R.id.scanner_tv_info);
        textViewAdvanced = (TextView) view.findViewById(R.id.scanner_tv_advanced);
        qrCodeReaderView = (QRCodeReaderView) view.findViewById(R.id.qrdecoderview);

        qrCodeReaderView.setOnQRCodeReadListener(this);
        textViewInfo.setOnClickListener(this);
        textViewAdvanced.setOnClickListener(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scanner, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scanner_history: {
                Intent intent = new Intent(getActivity(), ScannerHistoryActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        getActivity().setRequestedOrientation(savedOrientation);
        super.onDetach();
    }

    /* View.OnClickListener */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanner_tv_info: {
                Intent intent = new Intent(getActivity(), ScannerInfoActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.scanner_tv_advanced: {
                // TODO: navigate to "advanced" screen
            }
            break;
        }
    }

    /* QRCodeReaderView.OnQRCodeReadListener */

    @Override
    public void onQRCodeRead(String s, PointF[] pointFs) {
        // TODO: stop scanning/add continuous scanning logic here
        QRReaderResult result = new QRReaderResult();
        result.setText(s);
        result.setDate(new Date());

        ContentValues contentValues = new ContentValues();
        result.fillInContentValues(contentValues, DBAdapter.getInstance());
        getActivity().getContentResolver().insert(MITContentProvider.QRREADER_URI, contentValues);

//        qrCodeReaderView.getCameraManager().startPreview();
//        qrCodeReaderView.getCameraManager().getCamera().takePicture(null, null, null, new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                // TODO: get current screenshot here
//            }
//        });
    }

    @Override
    public void cameraNotFound() {
        textViewDisclaimer.setText(R.string.scan_no_camera);
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }
}
