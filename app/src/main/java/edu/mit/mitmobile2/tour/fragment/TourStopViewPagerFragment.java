package edu.mit.mitmobile2.tour.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.activities.TourDirectionsActivity;
import edu.mit.mitmobile2.tour.adapters.MainLoopAdapter;
import edu.mit.mitmobile2.tour.callbacks.TourStopCallback;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;
import edu.mit.mitmobile2.tour.utils.TourUtils;

public class TourStopViewPagerFragment extends Fragment {

    @InjectView(R.id.stop_image_view)
    ImageView stopImageView;
    @InjectView(R.id.stop_body_web_view)
    WebView stopBodyWebView;
    @InjectView(R.id.stop_thumbnail_title_text_view)
    TextView stopTitleTextView;
    @InjectView(R.id.tour_stop_scrollview)
    ScrollView tourStopScrollView;
    @InjectView(R.id.main_loop_recycler_view)
    RecyclerView mainLoopRecyclerView;
    @InjectView(R.id.directions_button)
    FloatingActionButton directionsButton;

    private MITTourStop mitTourStop;
    private TourStopCallback callback;
    private MITTour tour;
    private MainLoopAdapter mainLoopAdapter;
    private List<MITTourStop> mainLoopStops;
    private LinearLayoutManager layoutManager;

    public static TourStopViewPagerFragment newInstance(MITTourStop mitTourStop, MITTour tour) {
        TourStopViewPagerFragment fragment = new TourStopViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Tours.TOUR_KEY, tour);
        args.putParcelable(Constants.Tours.TOUR_STOP, mitTourStop);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tour_stop_viewpager, container, false);
        ButterKnife.inject(this, view);

        directionsButton.setSize(FloatingActionButton.SIZE_NORMAL);
        directionsButton.setColorNormalResId(R.color.mit_red);
        directionsButton.setColorPressedResId(R.color.mit_red_dark);

        callback = (TourStopCallback) getActivity();

        tour = getArguments().getParcelable(Constants.Tours.TOUR_KEY);
        mitTourStop = getArguments().getParcelable(Constants.Tours.TOUR_STOP);

        mainLoopStops = TourUtils.getMainLoopStops(tour.getStops());

        tourStopScrollView.scrollTo(0, tourStopScrollView.getTop());

        stopImageView.setAdjustViewBounds(true);
        Picasso.with(getActivity().getApplicationContext()).load(mitTourStop.getImage().getUrl()).into(stopImageView);

        stopBodyWebView.loadData(mitTourStop.getBodyHtml(), "text/html", "UTF-8");

        stopTitleTextView.setText(Html.fromHtml(mitTourStop.getTitle()));

        if (mitTourStop.getType().equals(Constants.Tours.SIDE_TRIP)) {
            callback.setSideTripActionBarTitle();
        }

        layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mainLoopRecyclerView.setLayoutManager(layoutManager);
        mainLoopAdapter = new MainLoopAdapter(getActivity().getApplicationContext(), mainLoopStops);
        mainLoopRecyclerView.setAdapter(mainLoopAdapter);

        return view;
    }

    @OnClick(R.id.directions_button)
    void goToDirections() {
        //TODO: animate away

        Intent intent = new Intent(getActivity(), TourDirectionsActivity.class);
        intent.putExtra(Constants.Tours.DIRECTION_KEY, mitTourStop.getDirection());

        if (mitTourStop.getType().equals(Constants.Tours.SIDE_TRIP)) {
            intent.putExtra(Constants.Tours.CURRENT_STOP_COORDS, tour.getStops().get(mitTourStop.getIndex()).getCoordinates());
            intent.putExtra(Constants.Tours.PREV_STOP_COORDS, tour.getStops().get(0).getCoordinates());
            intent.putExtra(Constants.Tours.TITLE_KEY, mitTourStop.getTitle());
            intent.putExtra(Constants.Tours.FIRST_TITLE_KEY, tour.getStops().get(0).getTitle());
        }

        startActivity(intent);
    }
}
