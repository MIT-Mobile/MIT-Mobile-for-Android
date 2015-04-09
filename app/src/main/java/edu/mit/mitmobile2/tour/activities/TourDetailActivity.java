package edu.mit.mitmobile2.tour.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;

public class TourDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        String details = getIntent().getStringExtra(Constants.Tours.TOUR_DETAILS_KEY);

        if (details != null) {
            WebView tourDetails = (WebView) findViewById(R.id.tour_details);
            tourDetails.loadData(details, "text/html", "utf-8");
        }
    }

}
