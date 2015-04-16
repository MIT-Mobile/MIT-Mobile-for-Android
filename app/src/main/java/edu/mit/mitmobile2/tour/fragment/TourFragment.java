package edu.mit.mitmobile2.tour.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import butterknife.OnClick;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.activities.MitIntroActivity;
import edu.mit.mitmobile2.tour.activities.TourSelfGuidedActivity;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.utils.TourUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class TourFragment extends Fragment {

    int contentLayoutId = R.layout.content_tours;

    @InjectView(R.id.self_guided_tour_title)
    TextView selfGuidedTourTitleTextView;
    @InjectView(R.id.stops_info_text_view)
    TextView stopsInfoTextView;
    @InjectView(R.id.distance_info_text_view)
    TextView distanceInfoTextView;
    @InjectView(R.id.time_info_text_view)
    TextView timeInfoTextView;

    private MITTour mitTour;

    @OnClick(R.id.send_feedback_view)
    public void sendFeedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.feedback_email_address)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_subject)
                + " "
                + TourUtils.getAppVersion()
                + " ("
                + TourUtils.getBuildDescription()
                + " ("
                + TourUtils.getAppVersion()
                + ")))"
                + " on Android "
                + Build.VERSION.RELEASE);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose an email Client :"));
    }

    @OnClick(R.id.mit_admissions_view)
    public void openMitAdmissionsWebsite() {
        openOutsideWebsite(getResources().getString(R.string.mit_admissions_url));
    }

    @OnClick(R.id.mit_info_center_view)
    public void openMitInfoCenterWebsite() {
        openOutsideWebsite(getResources().getString(R.string.mit_about_guided_tour_url));
    }

    @OnClick(R.id.more_guided_tours_text_view)
    public void openMoreGuidedToursWebsite() {
        openWebsiteDialog();
    }

    @OnClick(R.id.more_about_mit_text_view)
    public void openMitIntroduction() {
        Intent intent = new Intent(getActivity(), MitIntroActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.self_guided_tour_view)
    public void openNextActivity() {
        Intent intent = new Intent(getActivity(), TourSelfGuidedActivity.class);
        startActivity(intent);
    }

    private MITAPIClient mitApiClient;

    public TourFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_tours, null);

        ButterKnife.inject(this, view);

        mitApiClient = new MITAPIClient(getActivity());

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.Tours.TOUR_KEY)) {
            mitTour = savedInstanceState.getParcelable(Constants.Tours.TOUR_KEY);
            setTourInfoView(mitTour);
        } else {
            mitApiClient.get(Constants.TOURS, Constants.Tours.TOUR_PATH, null, null, new Callback<List<MITTour>>() {
                @Override
                public void success(List<MITTour> mitTours, Response response) {
                    mitTour = mitTours.get(0);
                    setTourInfoView(mitTour);
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
        }

        return view;
    }

    public void setTourInfoView(MITTour mitTour) {
        selfGuidedTourTitleTextView.setText(mitTour.getTitle());
        stopsInfoTextView.setText(mitTour.getShortDescription());
        distanceInfoTextView.setText(TourUtils.formatDistance(mitTour.getLengthInKm()));
        timeInfoTextView.setText(TourUtils.formatEstimatedDuration(mitTour.getEstimatedDurationInMinutes()));
    }

    public void openWebsiteDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.open_in_browser))
                .setMessage(getResources().getString(R.string.mit_about_guided_tour_url))
                .setPositiveButton(getResources().getString(R.string.open_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openOutsideWebsite(getResources().getString(R.string.mit_about_guided_tour_url));
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void openOutsideWebsite(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No application can handle this request. " +
                    "Please install a map app.", Toast.LENGTH_SHORT).show();
            Timber.e(e, "No map application");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.Tours.TOUR_KEY, mitTour);
    }
}
