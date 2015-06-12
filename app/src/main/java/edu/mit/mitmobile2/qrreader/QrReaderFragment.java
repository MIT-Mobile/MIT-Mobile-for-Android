package edu.mit.mitmobile2.qrreader;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import edu.mit.mitmobile2.R;

public class QrReaderFragment extends Fragment implements QRCodeReaderView.OnQRCodeReadListener {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_qrreader, null);

        qrCodeReaderView = (QRCodeReaderView) view.findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        return view;
    }

    @Override
    public void onQRCodeRead(String s, PointF[] pointFs) {
        // TODO: handle result
        qrCodeReaderView.getCameraManager().getCamera().takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // TODO: get current screenshot here
            }
        });
    }

    @Override
    public void onDetach() {
        getActivity().setRequestedOrientation(savedOrientation);
        super.onDetach();
    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }
}
