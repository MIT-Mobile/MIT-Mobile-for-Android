package edu.mit.mitmobile2.dining.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.HouseHoursAdapter;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;
import edu.mit.mitmobile2.shared.logging.LoggingManager;

public class DiningHouseInfoActivity extends AppCompatActivity {

    @InjectView(R.id.house_image_view)
    ImageView houseImageView;
    @InjectView(R.id.house_name_text_view)
    TextView houseNameTextView;
    @InjectView(R.id.house_status_text_view)
    TextView houseStatusTextView;
    @InjectView(R.id.payment_text_view)
    TextView paymentTextView;
    @InjectView(R.id.location_text_view)
    TextView locationTextView;
    @InjectView(R.id.house_hours_layout)
    LinearLayout hoursLayout;

    @OnClick(R.id.location_layout)
    public void openLocationInMap() {
        Intent intent = new Intent(this, MITMainActivity.class);
        intent.putExtra(Constants.LOCATION_KEY, venue.getLocation().getLocationDescription());
        startActivity(intent);
    }

    private MITDiningHouseVenue venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_house_info);
        ButterKnife.inject(this);

        venue = getIntent().getParcelableExtra(Constants.Dining.HOUSE_INFO);
        String houseStatus = getIntent().getStringExtra(Constants.Dining.HOUSE_STATUS);

        setTitle(venue.getShortName());

        houseNameTextView.setText(venue.getName());
        locationTextView.setText(venue.getLocation().getLocationDescription());

        String paymentStr = "";
        for (int i = 0; i < venue.getPayment().size(); i++) {
            if (i < venue.getPayment().size() - 1) {
                paymentStr = paymentStr + venue.getPayment().get(i) + ", ";
            } else {
                paymentStr = paymentStr + venue.getPayment().get(i);
            }
        }
        paymentTextView.setText(paymentStr);

        if (houseStatus.contains("until")) {
            houseStatusTextView.setTextColor(getResources().getColor(R.color.status_green));
        } else if (houseStatus.contains("at") || houseStatus.contains("Closed")) {
            houseStatusTextView.setTextColor(getResources().getColor(R.color.status_red));
        } else {
            houseStatusTextView.setTextColor(getResources().getColor(R.color.status_red));
        }

        houseStatusTextView.setText(houseStatus);

        buildHoursSegment();

        try {
            Picasso.with(this).load(venue.getIconURL()).placeholder(R.drawable.grey_rect).into(houseImageView);
        } catch (NullPointerException e) {
            Picasso.with(this).load(R.drawable.grey_rect).placeholder(R.drawable.grey_rect).into(houseImageView);
        }
    }

    private void buildHoursSegment() {
        if (venue.getMealsByDay().get(0).getMeals() != null) {
            List<MITDiningMeal> meals = venue.getMealsByDay().get(0).getMeals();
            String startDate = venue.getMealsByDay().get(0).getDateString();

            List<MITDiningMeal> previousMeals = meals;

            for (int i = 1; i < venue.getMealsByDay().size(); i++) {
                if (!checkMeals(venue.getMealsByDay().get(i).getMeals(), previousMeals)) {
                    String endDate = venue.getMealsByDay().get(i - 1).getDateString();
                    meals = venue.getMealsByDay().get(i - 1).getMeals();

                    String range = formatDate(startDate) + " - " + formatDate(endDate);
                    buildAndAddView(range, meals);

                    previousMeals = venue.getMealsByDay().get(i).getMeals();
                    startDate = venue.getMealsByDay().get(i).getDateString();
                } else if (i == venue.getMealsByDay().size() - 1) {
                    String endDate = venue.getMealsByDay().get(i).getDateString();
                    meals = venue.getMealsByDay().get(i).getMeals();

                    String range = formatDate(startDate) + " - " + formatDate(endDate);
                    buildAndAddView(range, meals);
                }
            }
        } else {
            buildAndAddView();
        }
    }

    private void buildAndAddView() {
        LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.dining_house_date_range_segment, null);
        TextView dateRangeTextView = (TextView) layout.findViewById(R.id.date_range_text_view);
        ListView mealsListView = (ListView) layout.findViewById(R.id.meals_list_view);
        TextView hoursTextView = (TextView) layout.findViewById(R.id.hours_text_view);

        dateRangeTextView.setText(getResources().getString(R.string.mon_sun));
        hoursTextView.setText(getResources().getString(R.string.dining_day_closed));

        mealsListView.setVisibility(View.GONE);
        hoursTextView.setVisibility(View.VISIBLE);

        hoursLayout.addView(layout);
    }

    private void buildAndAddView(String range, List<MITDiningMeal> meals) {
        LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.dining_house_date_range_segment, null);
        TextView dateRangeTextView = (TextView) layout.findViewById(R.id.date_range_text_view);
        ListView mealsListView = (ListView) layout.findViewById(R.id.meals_list_view);
        TextView hoursTextView = (TextView) layout.findViewById(R.id.hours_text_view);

        mealsListView.setVisibility(View.VISIBLE);
        hoursTextView.setVisibility(View.GONE);

        dateRangeTextView.setText(range);

        HouseHoursAdapter adapter = new HouseHoursAdapter(this, meals);
        mealsListView.setAdapter(adapter);

        hoursLayout.addView(layout);
    }

    private boolean checkMeals(List<MITDiningMeal> currentMeals, List<MITDiningMeal> previousMeals) {
        boolean isSame = true;
        if (currentMeals.size() != previousMeals.size()) {
            isSame =false;
        } else {
            for (int i = 0; i < currentMeals.size(); i++) {
                if ((!currentMeals.get(i).getName().equals(previousMeals.get(i).getName()))) {
                    isSame =false;
                }
                if ((!currentMeals.get(i).getStartTimeString().equals(previousMeals.get(i).getStartTimeString())) ||
                        (!currentMeals.get(i).getEndTimeString().equals(previousMeals.get(i).getEndTimeString()))) {
                    isSame =false;
                }
            }
        }

        return isSame;
    }

    private String formatDate(String dateString) {
        Date date = new Date();
        SimpleDateFormat formattedDate = new SimpleDateFormat("EEE");
        SimpleDateFormat originalDate = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date = originalDate.parse(dateString);
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "___________DateFormatError___________");
        }

        String formattedString = formattedDate.format(date).toUpperCase();

        return formattedString;
    }
}
