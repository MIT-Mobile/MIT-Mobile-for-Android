package edu.mit.mitmobile2.shuttles;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.AdapterView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopIntersectingAdapter;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopPredictionsAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleStopViewPagerFragment extends Fragment {

    @InjectView(R.id.stop_prediction_adapter_view)
    AdapterView predictionAdapterView;

    @InjectView(R.id.intersecting_routes_adapter_view)
    AdapterView intersectingRoutesAdapterView;

    private ShuttleStopPredictionsAdapter predictionsAdapter;
    private ShuttleStopIntersectingAdapter intersectingAdapter;

    private String stopId;
    private MITShuttleStopWrapper stop = new MITShuttleStopWrapper();

    public static ShuttleStopViewPagerFragment newInstance(String stopId) {
        ShuttleStopViewPagerFragment fragment = new ShuttleStopViewPagerFragment();
        Bundle args = new Bundle();
        args.putString(Constants.STOP_ID_KEY, stopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stop_viewpager, container, false);
        ButterKnife.inject(this, view);

        stopId = getArguments().getString(Constants.STOP_ID_KEY);

        String selectionString = Schema.Stop.TABLE_NAME + "." + Schema.Stop.STOP_ID + "=\'" + stopId + "\'";
        Cursor cursor = getActivity().getContentResolver().query(MITShuttlesProvider.SINGLE_STOP_URI, Schema.Stop.ALL_COLUMNS, selectionString, null, null);
        cursor.moveToFirst();
        stop.buildFromCursor(cursor, MitMobileApplication.dbAdapter);
        cursor.close();

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

    public void updatePredictions(List<MITShuttlePrediction> predictions) {
        predictionsAdapter = new ShuttleStopPredictionsAdapter(getActivity(), predictions);
        predictionAdapterView.setAdapter(predictionsAdapter);
    }
}
