package edu.mit.mitmobile2.shuttles;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopAdapter;

public class ShuttleStopViewPagerFragment extends Fragment{

    @InjectView(R.id.stop_listview)
    ListView stopListView;

    private ShuttleStopAdapter shuttleStopAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stop_viewpager, container, false);
        ButterKnife.inject(this, view);

        ArrayList<String> stops = new ArrayList<>();
        stops.add("14m");
        stops.add("22m");
        stops.add("14m");
        stops.add("Saferide Boston All");
        stops.add("Saferide Boston All");
        stops.add("Saferide Boston All");
        stops.add("end");

        shuttleStopAdapter = new ShuttleStopAdapter(this.getActivity(), stops, 3);
        stopListView.setAdapter(shuttleStopAdapter);

        return view;
    }
}
