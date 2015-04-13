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
import edu.mit.mitmobile2.tour.adapters.TourStopRecyclerViewAdapter;
import edu.mit.mitmobile2.tour.callbacks.TourSelfGuidedCallback;
import edu.mit.mitmobile2.tour.callbacks.TourStopCallback;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;
import edu.mit.mitmobile2.tour.utils.TourStopScrollView;
import edu.mit.mitmobile2.tour.utils.TourStopScrollViewListener;
import edu.mit.mitmobile2.tour.utils.TourUtils;

public class TourStopViewPagerFragment extends Fragment implements TourStopScrollViewListener {

    @InjectView(R.id.stop_image_view)
    ImageView stopImageView;
    @InjectView(R.id.stop_body_web_view)
    WebView stopBodyWebView;
    @InjectView(R.id.stop_thumbnail_title_text_view)
    TextView stopTitleTextView;
    @InjectView(R.id.tour_stop_scrollview)
    TourStopScrollView tourStopScrollView;
    @InjectView(R.id.main_loop_recycler_view)
    RecyclerView mainLoopRecyclerView;
    @InjectView(R.id.near_here_recycler_view)
    RecyclerView nearHereRecyclerView;
    @InjectView(R.id.directions_button)
    FloatingActionButton directionsButton;

    private TourStopCallback tourStopCallback;
    private TourSelfGuidedCallback tourSelfGuidedCallback;

    private MITTourStop mitTourStop;
    private MITTour tour;
    private TourStopRecyclerViewAdapter mainLoopAdapter;
    private TourStopRecyclerViewAdapter nearLoopAdapter;
    private List<MITTourStop> mainLoopStops;
    private List<MITTourStop> nearHereStops;
    private int fakePosition;

    private LinearLayoutManager mainLooplayoutManager;
    private LinearLayoutManager nearHereLayoutManager;

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

        tourStopCallback = (TourStopCallback) getActivity();
        tourSelfGuidedCallback = (TourSelfGuidedCallback) getActivity();

        tour = getArguments().getParcelable(Constants.Tours.TOUR_KEY);
        mitTourStop = getArguments().getParcelable(Constants.Tours.TOUR_STOP);

        nearHereStops = TourUtils.getNearHereStops(tour.getStops(), mitTourStop);
        mainLoopStops = TourUtils.getMainLoopStops(tour.getStops());

        tourStopScrollView.scrollTo(0, tourStopScrollView.getTop());
        tourStopScrollView.setScrollViewListener(this);

        stopImageView.setAdjustViewBounds(true);
        Picasso.with(getActivity().getApplicationContext()).load(mitTourStop.getImage().getUrl()).into(stopImageView);

        stopBodyWebView.loadData(mitTourStop.getBodyHtml(), "text/html", "UTF-8");

        stopTitleTextView.setText(Html.fromHtml(mitTourStop.getTitle()));

        if (mitTourStop.getType().equals(Constants.Tours.SIDE_TRIP)) {
            tourStopCallback.setSideTripActionBarTitle();
        }

        mainLooplayoutManager = new LinearLayoutManager(this.getActivity());
        mainLooplayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        if (mitTourStop.getType().equals(Constants.Tours.SIDE_TRIP)) {
            fakePosition = mainLoopStops.size() * TourUtils.NUMBER_OF_TOUR_LOOP / 2;
        } else {
            fakePosition = mainLoopStops.size() * TourUtils.NUMBER_OF_TOUR_LOOP / 2 + mitTourStop.getIndex();
        }

        mainLooplayoutManager.scrollToPosition(fakePosition);
        mainLoopRecyclerView.setLayoutManager(mainLooplayoutManager);
        mainLoopAdapter = new TourStopRecyclerViewAdapter(getActivity().getApplicationContext(), mainLoopStops, tourSelfGuidedCallback, true);
        mainLoopRecyclerView.setAdapter(mainLoopAdapter);

        nearHereLayoutManager = new LinearLayoutManager(this.getActivity());
        nearHereLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        nearHereRecyclerView.setLayoutManager(nearHereLayoutManager);
        nearLoopAdapter = new TourStopRecyclerViewAdapter(getActivity().getApplicationContext(), nearHereStops, tourSelfGuidedCallback, false);
        nearHereRecyclerView.setAdapter(nearLoopAdapter);

        return view;
    }

    @OnClick(R.id.directions_button)
    void goToDirections() {
        //TODO: animate away

        Intent intent = new Intent(getActivity(), TourDirectionsActivity.class);
        int index = mitTourStop.getIndex();
        int prevIndex;
        if (index == 0) {
            prevIndex = mainLoopStops.size() - 1;
        } else {
            prevIndex = index - 1;
        }

        intent.putExtra(Constants.Tours.DIRECTION_KEY, tour.getStops().get(prevIndex).getDirection());

        if (mitTourStop.getType().equals(Constants.Tours.SIDE_TRIP)) {
            intent.putExtra(Constants.Tours.CURRENT_STOP_COORDS, tour.getStops().get(index).getCoordinates());
            intent.putExtra(Constants.Tours.PREV_STOP_COORDS, tour.getStops().get(0).getCoordinates());
            intent.putExtra(Constants.Tours.TITLE_KEY, mitTourStop.getTitle());
            intent.putExtra(Constants.Tours.FIRST_TITLE_KEY, tour.getStops().get(0).getTitle());
        }

        startActivity(intent);
    }

    @Override
    public void onScrollChanged(TourStopScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y >= stopTitleTextView.getBottom()) {
            tourStopCallback.setTourStopActionbarTitle(mitTourStop);
        } else {
            if (mitTourStop.getType().equals(Constants.Tours.MAIN_LOOP)) {
                tourStopCallback.setMainLoopActionBarTitle((mitTourStop.getIndex() + 1), mainLoopStops.size());
            } else {
                tourStopCallback.setSideTripActionBarTitle();
            }
        }
    }
}