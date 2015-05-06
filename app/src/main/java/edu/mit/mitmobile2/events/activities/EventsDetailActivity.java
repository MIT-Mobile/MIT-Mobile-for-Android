package edu.mit.mitmobile2.events.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.fragment.CalendarWeekFragment;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;
import edu.mit.mitmobile2.events.model.MITCalendarLocation;
import edu.mit.mitmobile2.events.model.MITCalendarSponsor;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventsDetailActivity extends MITActivity {

    private MITCalendarEvent event;

    @InjectView(R.id.speaker_layout)
    LinearLayout speakerLayout;
    @InjectView(R.id.location_layout)
    RelativeLayout locationLayout;
    @InjectView(R.id.phone_layout)
    RelativeLayout phoneLayout;
    @InjectView(R.id.webview_layout)
    LinearLayout webViewLayout;
    @InjectView(R.id.website_layout)
    RelativeLayout websiteLayout;
    @InjectView(R.id.opento_layout)
    LinearLayout opentoLayout;
    @InjectView(R.id.cost_layout)
    LinearLayout costLayout;
    @InjectView(R.id.sponsor_layout)
    LinearLayout sponsorLayout;
    @InjectView(R.id.formoreinfo_layout)
    RelativeLayout forMoreInfoLayout;
    @InjectView(R.id.time_layout)
    RelativeLayout timeLayout;

    @InjectView(R.id.title_text_view)
    TextView titleTextView;
    @InjectView(R.id.seriesDescription_text_view)
    TextView seriesDescriptionTextView;
    @InjectView(R.id.speaker_text_view)
    TextView speakerTextView;
    @InjectView(R.id.time_text_view)
    TextView timeTextView;
    @InjectView(R.id.location_text_view)
    TextView locationTextView;
    @InjectView(R.id.phone_text_view)
    TextView phoneTextView;
    @InjectView(R.id.cost_text_view)
    TextView costTextView;
    @InjectView(R.id.website_text_view)
    TextView websiteTextView;
    @InjectView(R.id.opento_text_view)
    TextView opentoTextView;
    @InjectView(R.id.sponsor_text_view)
    TextView sponsorTextView;
    @InjectView(R.id.formoreinfo_text_view)
    TextView foreMoreInfoTextView;
    @InjectView(R.id.description_web_view)
    WebView descriptionWebView;

    @OnClick(R.id.website_layout)
    public void openWebisteIntent() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(websiteTextView.getText().toString())));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. " +
                    "Please install an app.", Toast.LENGTH_SHORT).show();
            LoggingManager.Timber.e(e, "No application found");
        }
    }

    @OnClick(R.id.location_layout)
    public void openLocationIntent() {
        //TODO : link to Map Module
    }

    @OnClick(R.id.phone_layout)
    public void makePhoneCallIntent() {
        String uri = "tel:" + phoneTextView.getText().toString().trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_detail);

        ButterKnife.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final MITAPIClient mitApiClient = new MITAPIClient(this);

        event = getIntent().getParcelableExtra(Constants.Events.CALENDAR_EVENT);

        HashMap<String, String> pathParams = new HashMap<>();
        pathParams.put("calendar", "events_calendar");
        pathParams.put("event", event.getIdentifier());

        mitApiClient.get(Constants.EVENTS, Constants.Events.CALENDAR_EVENT_PATH, pathParams, null, new Callback<MITCalendarEvent>() {
            @Override
            public void success(MITCalendarEvent mitEvent, Response response) {
                LoggingManager.Timber.d("Success!");
                setEventDetails(mitEvent);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void setEventDetails(final MITCalendarEvent event) {
        titleTextView.setText(event.getTitle());

        if (event.getSeriesInfo() != null) {
            seriesDescriptionTextView.setVisibility(View.VISIBLE);
        } else {
            seriesDescriptionTextView.setVisibility(View.GONE);
        }

        if (event.getLecturer() != null) {
            speakerLayout.setVisibility(View.VISIBLE);
            speakerTextView.setText(event.getLecturer());
        } else {
            speakerLayout.setVisibility(View.GONE);
        }

        if (event.getStartAt() != null) {
            timeLayout.setVisibility(View.VISIBLE);
            String dateString =  (String) DateFormat.format(CalendarWeekFragment.DATE_FORMAT, event.getStartDate());
            String timeString = (DateFormat.format("h:mm a", event.getStartDate()) + " - " + DateFormat.format("h:mm a", event.getEndDate()));
            timeTextView.setText(dateString + "\n" + timeString);
        } else {
            timeLayout.setVisibility(View.GONE);
        }

        if (event.getLocation() != null) {
            locationLayout.setVisibility(View.VISIBLE);
            MITCalendarLocation location = event.getLocation();
            String locationString = "";
            if (location.getRoomNumber() != null && location.getDescription() != null) {
                locationString = location.getRoomNumber() + "\n" + location.getDescription();
            } else if (location.getRoomNumber() != null && location.getDescription() == null) {
                locationString = location.getRoomNumber();
            } else if (location.getRoomNumber() == null && location.getDescription() != null) {
                locationString = location.getDescription();
            }
            locationTextView.setText(locationString);
        } else {
            locationLayout.setVisibility(View.GONE);
        }

        if (event.getContact() != null && event.getContact().getPhone() != null) {
            phoneLayout.setVisibility(View.VISIBLE);
            phoneTextView.setText(event.getContact().getPhone());
        } else {
            phoneLayout.setVisibility(View.GONE);
        }

        if (event.getHtmlDescription() != null) {
            webViewLayout.setVisibility(View.VISIBLE);
            descriptionWebView.getSettings().setJavaScriptEnabled(true);
            String template = readInHtmlTemplate();
            if (event.getHtmlDescription() != null) {
                template = template.replace("__BODY__", event.getHtmlDescription());
            } else {
                template = template.replace("__BODY__", "");
            }
            descriptionWebView.loadData(template, "text/html;charset=utf-8", "UTF-8");
        } else {
            webViewLayout.setVisibility(View.GONE);
        }

        if (event.getContact() != null && event.getContact().getWebsiteURL() != null) {
            websiteLayout.setVisibility(View.VISIBLE);
            websiteTextView.setText(event.getContact().getWebsiteURL());
        } else {
            websiteLayout.setVisibility(View.GONE);
        }

        if (event.getOpenTo() != null) {
            opentoLayout.setVisibility(View.VISIBLE);
            opentoTextView.setText(event.getOpenTo());
        } else {
            opentoLayout.setVisibility(View.GONE);
        }

        if (event.getCost() != null) {
            costLayout.setVisibility(View.VISIBLE);
            costTextView.setText(event.getCost());
        } else {
            costLayout.setVisibility(View.GONE);
        }

        if (event.getSponsors() != null && event.getSponsors().size() > 0) {
            sponsorLayout.setVisibility(View.VISIBLE);
            String sponsorText = "";
            List<MITCalendarSponsor> sponsors = new ArrayList<>(event.getSponsors());
            for (MITCalendarSponsor sponsor : sponsors) {
                sponsorText += sponsor.getName() + " ";
            }
            sponsorTextView.setText(sponsorText);
        } else {
            sponsorLayout.setVisibility(View.GONE);
        }

        if (event.getContact() != null && event.getContact().getEmail() != null) {
            forMoreInfoLayout.setVisibility(View.VISIBLE);
            String name = "";
            if (event.getContact().getName() != null) {
                name = event.getContact().getName();
            }
            foreMoreInfoTextView.setText(name + "(" + event.getContact().getEmail() +")");
        } else {
            forMoreInfoLayout.setVisibility(View.GONE);
        }

        forMoreInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{event.getContact().getEmail()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, event.getTitle());
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Choose an email Client :"));
            }
        });

        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, formatMilliseconds(event.getStartAt()));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, formatMilliseconds(event.getEndAt()));
                intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, locationTextView.getText().toString());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getHtmlDescription());
                intent.setData(CalendarContract.Events.CONTENT_URI);
                startActivity(intent);
            }
        });
    }

    private String readInHtmlTemplate() {
        String template = "";
        try {
            InputStream is = getAssets().open("event_template.html");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            template = new String(buffer, "UTF-8");
        } catch (IOException e) {
            LoggingManager.Timber.e(e, "_____________HTML read Failed_____________");
        }
        return template;
    }

    private long formatMilliseconds(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "_____________Unparsable Date_____________");
        }
        return 0;
    }
}
