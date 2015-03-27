package edu.mit.mitmobile2.qrreader;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.R;

public class QrReaderFragment extends Fragment {

    public QrReaderFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_qrreader, null);
        return view;
    }
}
