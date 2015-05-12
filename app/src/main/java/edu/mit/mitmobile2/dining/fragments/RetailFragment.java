package edu.mit.mitmobile2.dining.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.interfaces.Updateable;
import edu.mit.mitmobile2.dining.model.MITDiningDining;

/**
 * Created by serg on 5/8/15.
 */
public class RetailFragment extends Fragment implements Updateable {

    public static RetailFragment newInstance() {
        RetailFragment fragment = new RetailFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_retail, null);

        return view;
    }

    /* Updateable */

    @Override
    public void onDining(MITDiningDining mitDiningDining) {
        // TODO: add logic here
    }
}
