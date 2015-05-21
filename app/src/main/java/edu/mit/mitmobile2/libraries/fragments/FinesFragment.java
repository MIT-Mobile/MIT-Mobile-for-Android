package edu.mit.mitmobile2.libraries.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITFineItem;

public class FinesFragment extends AccountPageFragment {

    public static FinesFragment newInstance() {
        return new FinesFragment();
    }

    public FinesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        //TODO: Get fines

        List<MITLibrariesMITFineItem> fineItems = new ArrayList<>();

        int fineTotal = 0;

        for (MITLibrariesMITFineItem fineItem : fineItems) {
            fineTotal += fineItem.getAmount();
        }

        String today = new SimpleDateFormat("M/dd/yyy", Locale.US).format(Calendar.getInstance().getTime());

        buildAndAddHeaderView(String.format(getString(R.string.fines_total), fineTotal, today), getString(R.string.library_fine_payment_info), fineTotal > 0 ? R.color.closed_red : R.color.black);

        return view;
    }
}
