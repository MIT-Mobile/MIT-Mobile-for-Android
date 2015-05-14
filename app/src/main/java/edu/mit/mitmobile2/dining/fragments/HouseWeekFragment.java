package edu.mit.mitmobile2.dining.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;

public class HouseWeekFragment extends Fragment {

    @InjectView(R.id.date_text_text_view)
    TextView dateTextView;
    @InjectView(R.id.info_text_view)
    TextView infoTextView;

    public static HouseWeekFragment newInstance() {
        HouseWeekFragment houseWeekFragment = new HouseWeekFragment();
        return houseWeekFragment;
    }

    public HouseWeekFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dinning_house_week, container, false);
        ButterKnife.inject(this, view);

        //TODO: connect to server
        dateTextView.setText("Tomorrow, May 15");
        infoTextView.setText("Breakfast 8am - 10am");

        return view;
    }
}