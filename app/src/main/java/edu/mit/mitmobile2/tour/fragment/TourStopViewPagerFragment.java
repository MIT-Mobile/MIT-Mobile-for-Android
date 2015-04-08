package edu.mit.mitmobile2.tour.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.callbacks.TourStopCallback;
import edu.mit.mitmobile2.tour.model.MITTourStop;

public class TourStopViewPagerFragment extends Fragment {

    @InjectView(R.id.stop_image_view)
    ImageView stopImageView;
    @InjectView(R.id.stop_body_web_view)
    WebView stopBodyWebView;
    @InjectView(R.id.stop_title_text_view)
    TextView stopTitleTextView;
    @InjectView(R.id.tour_stop_scrollview)
    ScrollView tourStopScrollView;

    private MITTourStop mitTourStop;
    private TourStopCallback callback;

    public static TourStopViewPagerFragment newInstance(MITTourStop mitTourStop) {
        TourStopViewPagerFragment fragment = new TourStopViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.TOUR_STOP, mitTourStop);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tour_stop_viewpager, container, false);
        ButterKnife.inject(this, view);

        callback = (TourStopCallback) getActivity();

        mitTourStop = getArguments().getParcelable(Constants.TOUR_STOP);

        tourStopScrollView.scrollTo(0, tourStopScrollView.getTop());

        stopImageView.setAdjustViewBounds(true);
        Picasso.with(getActivity().getApplicationContext()).load(mitTourStop.getImage().getUrl()).into(stopImageView);

        stopBodyWebView.loadData(mitTourStop.getBodyHtml(), "text/html", "UTF-8");

        stopTitleTextView.setText(Html.fromHtml(mitTourStop.getTitle()));

        if (mitTourStop.getType().equals(Constants.SIDE_TRIP)) {
            callback.setSideTripActionBarTitle();
        }

        return view;
    }
}
