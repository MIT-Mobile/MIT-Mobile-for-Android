package edu.mit.mitmobile2.dining.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.model.MITDiningRetailDay;
import edu.mit.mitmobile2.dining.model.MITDiningRetailVenue;
import edu.mit.mitmobile2.shared.logging.LoggingManager;

public class DiningRetailActivity extends AppCompatActivity {

    @InjectView(R.id.retail_image)
    ImageView icon;

    @InjectView(R.id.retail_name)
    TextView retailName;

    @InjectView(R.id.retail_current_status)
    TextView retailStatus;

    @InjectView(R.id.food_information)
    TextView foodInfo;

    @InjectView(R.id.retail_description)
    WebView retailDescription;

    @InjectView(R.id.menu_segment)
    RelativeLayout menuLayout;

    @InjectView(R.id.payment_segment)
    RelativeLayout paymentLayout;

    @InjectView(R.id.hours_layout)
    LinearLayout hoursLayout;

    @InjectView(R.id.location_segment)
    RelativeLayout locationLayout;

    @InjectView(R.id.homepage_segment)
    RelativeLayout homepageLayout;

    @InjectView(R.id.favorites_button)
    Button favoritesButton;

    private MITDiningRetailVenue venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_retail);

        ButterKnife.inject(this);

        venue = getIntent().getParcelableExtra(Constants.DINING_VENUE_KEY);

        if (venue == null) {
            return;
        }

        Picasso.with(this).load(venue.getIconURL()).fit().centerInside().into(icon);

        getSupportActionBar().setTitle(venue.getName());

        retailName.setText(venue.getName());

        String cuisineString = buildString(venue.getCuisine());
        foodInfo.setText(cuisineString);

        retailDescription.loadData(venue.getDescriptionHTML(), "text/html;charset=utf-8", "utf-8");

        setupInfoSegment(menuLayout, R.string.venue_menu, venue.getMenuURL(), R.drawable.open_in_browser);

        String paymentString = buildString(venue.getPayment());
        setupInfoSegment(paymentLayout, R.string.venue_payment, paymentString, -1);

        buildHoursSegment(venue);

        String location = venue.getLocation().getLocationDescription() == null ? venue.getLocation().getStreet() + "\n" + venue.getLocation().getCity() + ", " + venue.getLocation().getState() : venue.getLocation().getLocationDescription();
        setupInfoSegment(locationLayout, R.string.venue_location, location, R.drawable.ic_navigation);
        setupInfoSegment(homepageLayout, R.string.venue_homepage, venue.getHomepageURL(), R.drawable.open_in_browser);

        boolean favorite = isVenueFavorite();
        favoritesButton.setText(!favorite ? R.string.venue_add_to_favs : R.string.venue_remove_from_favs);
        venue.setFavorite(favorite);
    }

    @OnClick(R.id.homepage_segment)
    void goToHomepage() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(venue.getHomepageURL()));
        startActivity(intent);
    }

    @OnClick(R.id.menu_segment)
    void goToMenu() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(venue.getMenuURL()));
        startActivity(intent);
    }

    @OnClick(R.id.location_segment)
    void goToLocation() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + Uri.encode(venue.getLocation().getLatitude()) + "," + Uri.encode(venue.getLocation().getLongitude())));
        startActivity(intent);
    }

    @OnClick(R.id.favorites_button)
    void toggleFavorites() {
        venue.setFavorite(!venue.isFavorite());
        SharedPreferences sharedPrefs = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this);
        String id = venue.getIdentifier();
        Set<String> stringSet = new HashSet<>();
        if (isVenueFavorite()) {
            if (sharedPrefs.contains(Constants.FAVORITE_VENUES_KEY)) {
                stringSet.addAll(sharedPrefs.getStringSet(Constants.FAVORITE_VENUES_KEY, null));
                stringSet.remove(id);
                sharedPrefs.edit().remove(Constants.FAVORITE_VENUES_KEY).commit();
                sharedPrefs.edit().putStringSet(Constants.FAVORITE_VENUES_KEY, stringSet).commit();
            }
        } else {
            if (sharedPrefs.contains(Constants.FAVORITE_VENUES_KEY)) {
                stringSet.addAll(sharedPrefs.getStringSet(Constants.FAVORITE_VENUES_KEY, null));
                stringSet.add(id);
                sharedPrefs.edit().remove(Constants.FAVORITE_VENUES_KEY).commit();
                sharedPrefs.edit().putStringSet(Constants.FAVORITE_VENUES_KEY, stringSet).commit();
            } else {
                stringSet.add(id);
                sharedPrefs.edit().putStringSet(Constants.FAVORITE_VENUES_KEY, stringSet).commit();
            }
        }

        favoritesButton.setText(!venue.isFavorite() ? R.string.venue_add_to_favs : R.string.venue_remove_from_favs);
    }

    private boolean isVenueFavorite() {
        if (!PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this).contains(Constants.FAVORITE_VENUES_KEY)) {
            return false;
        } else {
            Set<String> stringSet = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this).getStringSet(Constants.FAVORITE_VENUES_KEY, null);
            return stringSet.contains(venue.getIdentifier());
        }
    }

    private String buildString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (list == null) {
            return null;
        }

        for (String s : list) {
            sb.append(s);
            if (list.indexOf(s) < list.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private void buildHoursSegment(MITDiningRetailVenue venue) {
        //Init from first item
        String startTime = venue.getHours().get(0).getStartTimeString();
        String endTime = venue.getHours().get(0).getEndTimeString();
        String message = venue.getHours().get(0).getMessage();
        Date startDate = venue.getHours().get(0).getDate();

        checkCurrentStatus(venue, endTime, startDate);

        for (int i = 1; i < venue.getHours().size(); i++) {
            MITDiningRetailDay d = venue.getHours().get(i);
            Date previousDate = venue.getHours().get(i - 1).getDate();


            if (message != null && d.getMessage() != null) {
                if (!d.getMessage().equals(message)) {
                    startDate = formatMessage(message, startDate.getTime(), previousDate.getTime(), d);
                } else if (i == venue.getHours().size() - 1) {
                    startDate = formatMessage(message, startDate.getTime(), d.getDate().getTime(), d);
                }
            } else if (d.getMessage() == null && message != null) {
                startDate = formatMessage(message, startDate.getTime(), previousDate.getTime(), d);
            } else if (d.getMessage() != null && (startTime != null && endTime != null)) {
                startDate = formatHours(startTime, endTime, startDate, d, previousDate);
            } else if ((d.getStartTimeString() != null && d.getEndTimeString() != null)) {
                if (!d.getStartTimeString().equals(startTime) || !d.getEndTimeString().equals(endTime)) {
                    startDate = formatHours(startTime, endTime, startDate, d, previousDate);
                } else if (i == venue.getHours().size() - 1) {
                    startDate = formatHours(startTime, endTime, startDate, d, d.getDate());
                }
            }

            startTime = d.getStartTimeString();
            endTime = d.getEndTimeString();
            message = d.getMessage();

            checkCurrentStatus(venue, endTime, d.getDate());
        }
    }

    private void checkCurrentStatus(MITDiningRetailVenue venue, String endTime, Date startDate) {
        Date time = Calendar.getInstance(Locale.US).getTime();

        Calendar c1 = Calendar.getInstance();
        c1.setTime(startDate);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(time);

        if (c1.get(Calendar.DAY_OF_WEEK) == c2.get(Calendar.DAY_OF_WEEK)) {
            if (venue.isOpenNow()) {
                retailStatus.setText(getString(R.string.open_until) + formatTime(endTime));
                retailStatus.setTextColor(getResources().getColor(R.color.open_green));
            } else {
                retailStatus.setText(getString(R.string.closed));
                retailStatus.setTextColor(getResources().getColor(R.color.closed_red));
            }
        }
    }

    private Date formatMessage(String message, long time1, long time2, MITDiningRetailDay d) {
        String startDay = formatDayFromDate(time1);
        String endDay = formatDayFromDate(time2);
        String dateRange = startDay.equals(endDay) ? startDay : startDay + " - " + endDay;

        buildAndAddView(dateRange, message);
        return d.getDate();
    }

    private Date formatHours(String startTime, String endTime, Date startDate, MITDiningRetailDay d, Date previousDate) {
        String startDay = formatDayFromDate(startDate.getTime());
        String endDay = formatDayFromDate(previousDate.getTime());
        String dateRange = startDay.equals(endDay) ? startDay : startDay + " - " + endDay;

        buildAndAddView(dateRange, formatTime(startTime) + " - " + formatTime(endTime));
        return d.getDate();
    }

    private String formatDayFromDate(long time) {
        DateFormat formatter = new SimpleDateFormat("EEE", Locale.US);
        return formatter.format(time);
    }

    private String formatTime(String time) {
        DateFormat formatter = new SimpleDateFormat("h:mm a", Locale.US);

        Date d = getDateFromTime(time, "hh:mm:ss");

        if (d != null) {
            return formatter.format(d);
        }

        return "";
    }

    private Date getDateFromTime(String time, String pattern) {
        Date d = null;
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
        try {
            d = format.parse(time);
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "Failed");
        }
        return d;
    }

    private void buildAndAddView(String range, String hours) {
        LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.dining_hours_segment, null);
        TextView dateRange = (TextView) layout.findViewById(R.id.dining_date_range);
        TextView diningHours = (TextView) layout.findViewById(R.id.dining_hours);

        dateRange.setText(range);
        diningHours.setText(hours);

        hoursLayout.addView(layout);
    }

    private void setupInfoSegment(RelativeLayout layout, int title, String info, int imageRes) {
        TextView sectionTitle = (TextView) layout.findViewById(R.id.section_title);
        TextView sectionInfo = (TextView) layout.findViewById(R.id.section_info);
        ImageView sectionImage = (ImageView) layout.findViewById(R.id.section_image);

        if (info == null) {
            layout.setVisibility(View.GONE);
            return;
        }

        sectionTitle.setText(getString(title));
        sectionInfo.setText(info);

        if (imageRes == -1) {
            sectionImage.setVisibility(View.GONE);
        } else {
            sectionImage.setVisibility(View.VISIBLE);
            sectionImage.setImageResource(imageRes);
        }
    }
}
