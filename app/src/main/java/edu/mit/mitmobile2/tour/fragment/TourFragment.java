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

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.model.MITTourWrapper;
import edu.mit.mitmobile2.tour.utils.TourUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TourFragment extends Fragment {

    int contentLayoutId = R.layout.content_tours;

    @InjectView(R.id.self_guided_tour_view)
    RelativeLayout selfGuidedTourView;
    @InjectView(R.id.send_feedback_view)
    RelativeLayout sendFeedbackView;
    @InjectView(R.id.mit_info_center_view)
    RelativeLayout mitInfoCenterView;
    @InjectView(R.id.mit_admissions_view)
    RelativeLayout mitAdmissionsView;
    @InjectView(R.id.more_guided_tours_text_view)
    TextView moreGuidedToursTextView;
    @InjectView(R.id.more_about_mit_text_view)
    TextView moreAboutMitTextView;
    @InjectView(R.id.self_guided_tour_title)
    TextView selfGuidedTourTitleTextView;
    @InjectView(R.id.stops_info_text_view)
    TextView stopsInfoTextView;
    @InjectView(R.id.distance_info_text_view)
    TextView distanceInfoTextView;
    @InjectView(R.id.time_info_text_view)
    TextView timeInfoTextView;

    private MITAPIClient mitapiClient;

    public TourFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_tours, null);

        ButterKnife.inject(this, view);

        mitapiClient = new MITAPIClient(getActivity().getApplicationContext());

        mitapiClient.get(Constants.TOURS, Constants.Tours.TOUR_PATH, null, null, new Callback<List<MITTourWrapper>>() {
            @Override
            public void success(List<MITTourWrapper> mitTourWrappers, Response response) {
                MITTourWrapper mitTourWrapper = mitTourWrappers.get(0);
                setTourInfoView(mitTourWrapper);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        selfGuidedTourView.setBackground(getResources().getDrawable(R.drawable.tours_cover_image));

        moreGuidedToursTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsiteDialog();
            }
        });

        sendFeedbackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });

        mitInfoCenterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOutsideWebsite(getResources().getString(R.string.mit_about_guided_tour_url));
            }
        });

        mitAdmissionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOutsideWebsite(getResources().getString(R.string.mit_admissions_url));
            }
        });

        return view;
    }

    public void setTourInfoView(MITTourWrapper mitTourWrapper) {
        selfGuidedTourTitleTextView.setText(mitTourWrapper.getTitle());
        stopsInfoTextView.setText(mitTourWrapper.getShortDescription());
        distanceInfoTextView.setText(TourUtils.formatDistance(mitTourWrapper.getLengthInKm()));
        timeInfoTextView.setText(TourUtils.formatEstimatedDuration(mitTourWrapper.getEstimatedDurationInMinutes()));
    }

    public void openWebsiteDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.open_in_browser))
                .setMessage(getResources().getString(R.string.mit_about_guided_tour_url))
                .setPositiveButton(getResources().getString(R.string.open_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openOutsideWebsite(getResources().getString(R.string.open_in_browser));
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
            e.printStackTrace();
        }
    }

    public void sendFeedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {getResources().getString(R.string.feedback_email_address)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_subject) + " on Android " + Build.VERSION.RELEASE);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose an email Client :"));
    }
}
