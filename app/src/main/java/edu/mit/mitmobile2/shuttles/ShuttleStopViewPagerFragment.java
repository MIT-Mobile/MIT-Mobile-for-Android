package edu.mit.mitmobile2.shuttles;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.AdapterView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopIntersectingAdapter;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopPredictionsAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleStopViewPagerFragment extends Fragment{

    public static final String STOP_KEY = "STOP_KEY";

    @InjectView(R.id.stop_prediction_adapter_view)
    AdapterView predictionAdapterView;

    @InjectView(R.id.intersecting_routes_adapter_view)
    AdapterView intersectingRoutesAdapterView;

    private ShuttleStopPredictionsAdapter predictionsAdapter;
    private ShuttleStopIntersectingAdapter intersectingAdapter;

    private MITShuttleStopWrapper stop;

    public static ShuttleStopViewPagerFragment newInstance(MITShuttleStopWrapper stop) {
        ShuttleStopViewPagerFragment fragment = new ShuttleStopViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(STOP_KEY, stop);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stop_viewpager, container, false);
        ButterKnife.inject(this, view);

        stop = getArguments().getParcelable(STOP_KEY);

        predictionsAdapter = new ShuttleStopPredictionsAdapter(getActivity(), stop.getPredictions());
        //TODO: Remove and replace with actual intersecting routes passed from other class
        MITShuttleRoute testRoute = new MITShuttleRoute();
        testRoute.setTitle("Testing");
        List<MITShuttleRoute> testRoutes = new ArrayList<>();
        testRoutes.add(testRoute);
        intersectingAdapter = new ShuttleStopIntersectingAdapter(getActivity(), testRoutes);

        predictionAdapterView.setAdapter(predictionsAdapter);
        intersectingRoutesAdapterView.setAdapter(intersectingAdapter);

        return view;
    }
}
