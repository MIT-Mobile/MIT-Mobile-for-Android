package edu.mit.mitmobile2.dining.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.HouseDayPagerAdapter;
import edu.mit.mitmobile2.dining.adapters.HouseMenuPagerAdapter;
import edu.mit.mitmobile2.dining.model.MITDiningHouseDay;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;
import edu.mit.mitmobile2.shared.logging.LoggingManager;

public class DiningHouseActivity extends AppCompatActivity  {

    @InjectView(R.id.house_image_view)
    ImageView houseImageView;
    @InjectView(R.id.house_name_text_view)
    TextView houseNameTextView;
    @InjectView(R.id.house_hours_text_view)
    TextView houseHoursTextView;
    @InjectView(R.id.dining_house_menu_viewpager)
    ViewPager houseMenuViewpager;
    @InjectView(R.id.date_text_text_view)
    TextView dateTextView;
    @InjectView(R.id.info_text_view)
    TextView infoTextView;

    @OnClick(R.id.info_image_view)
    public void gotoHouseInfo() {
        Intent intent = new Intent(this, DiningHouseInfoActivity.class);
        intent.putExtra(Constants.Dining.HOUSE_INFO, venue);
        intent.putExtra(Constants.Dining.HOUSE_STATUS, houseHoursTextView.getText().toString());
        startActivity(intent);
    }

    @OnClick(R.id.forward_image_view)
    public void goToNextHouseMeal() {
        if (houseMenuViewpager.getCurrentItem() < (diningMeals.size() - 1)) {
            houseMenuViewpager.setCurrentItem(houseMenuViewpager.getCurrentItem() + 1);
        }
    }

    @OnClick(R.id.back_image_view)
    public void goToPreviousHouseMeal() {
        if (houseMenuViewpager.getCurrentItem() > 0) {
            houseMenuViewpager.setCurrentItem(houseMenuViewpager.getCurrentItem() - 1);
        }
    }

    private HouseMenuPagerAdapter houseMenuPagerAdapter;
    private HouseDayPagerAdapter houseDayPagerAdapter;
    private List<MITDiningMeal> diningMeals;
    private MITDiningHouseVenue venue;

    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_house);
        ButterKnife.inject(this);

        venue = getIntent().getParcelableExtra(Constants.Dining.DINING_HOUSE);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        setTitle(venue.getShortName());

        buildHouseMenuPager();

        houseNameTextView.setText(venue.getName());
        dateTextView.setText("Today, " + getCurrentDate());

        try {
            Picasso.with(this).load(venue.getIconURL()).placeholder(R.drawable.grey_rect).into(houseImageView);
        } catch (NullPointerException e) {
            Picasso.with(this).load(R.drawable.grey_rect).placeholder(R.drawable.grey_rect).into(houseImageView);
        }
    }

    private void buildHouseMenuPager() {
        diningMeals = new ArrayList<>();
        for (MITDiningHouseDay diningHouseDay : venue.getMealsByDay()) {
            if (diningHouseDay.getMeals() != null) {
                for (MITDiningMeal mitDiningMeal : diningHouseDay.getMeals()) {
                    mitDiningMeal.setHouseDateString(diningHouseDay.getDateString());
                    diningMeals.add(mitDiningMeal);
                }
            }
        }

        if (diningMeals.size() > 0) {
            houseMenuPagerAdapter = new HouseMenuPagerAdapter(getFragmentManager(), diningMeals);
            houseMenuViewpager.setAdapter(houseMenuPagerAdapter);

            if (getIntent().getIntExtra(Constants.Dining.HOUSE_MENU_PAGER_INDEX, -1) != -1) {
                houseMenuViewpager.setCurrentItem(getIntent().getIntExtra(Constants.Dining.HOUSE_MENU_PAGER_INDEX, -1));
            } else {
                houseMenuViewpager.setCurrentItem(findCurrentMeal(diningMeals));
            }
        } else {
            houseDayPagerAdapter = new HouseDayPagerAdapter(getFragmentManager(), venue.getMealsByDay());
            houseMenuViewpager.setAdapter(houseDayPagerAdapter);

            if (getIntent().getIntExtra(Constants.Dining.HOUSE_MENU_PAGER_INDEX, -1) != -1) {
                houseMenuViewpager.setCurrentItem(getIntent().getIntExtra(Constants.Dining.HOUSE_MENU_PAGER_INDEX, -1));
            } else {
                houseMenuViewpager.setCurrentItem(findCurrentHouseDay(venue.getMealsByDay()));
            }
        }

        houseMenuViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (diningMeals.size() > 0) {
                    if (diningMeals.get(position).getHouseDateString().equals(getCurrentDate())) {
                        dateTextView.setText(getResources().getString(R.string.house_today, formatSimpletDate(diningMeals.get(position).getHouseDateString())));
                    } else if (diningMeals.get(position).getHouseDateString().equals(getYesterdayDate())) {
                        dateTextView.setText(getResources().getString(R.string.house_yesterday, formatSimpletDate(diningMeals.get(position).getHouseDateString())));
                    } else if (diningMeals.get(position).getHouseDateString().equals(getTomorrowDate())) {
                        dateTextView.setText(getResources().getString(R.string.house_tomorrow, formatSimpletDate(diningMeals.get(position).getHouseDateString())));
                    } else {
                        dateTextView.setText(formatDate(diningMeals.get(position).getHouseDateString()));
                    }

                    String startTime;
                    String endTime;
                    if (diningMeals.get(position).getStartTimeString().endsWith(":00:00")) {
                        startTime = DateFormat.format("h a", formatMealTime(diningMeals.get(position).getStartTimeString())).toString().toLowerCase();
                    } else {
                        startTime = DateFormat.format("h:mm a", formatMealTime(diningMeals.get(position).getStartTimeString())).toString().toLowerCase();
                    }
                    if (diningMeals.get(position).getEndTimeString().endsWith(":00:00")) {
                        endTime = DateFormat.format("h a", formatMealTime(diningMeals.get(position).getEndTimeString())).toString().toLowerCase();
                    } else {
                        endTime = DateFormat.format("h:mm a", formatMealTime(diningMeals.get(position).getEndTimeString())).toString().toLowerCase();
                    }
                    infoTextView.setText(diningMeals.get(position).getName() + " "
                            + startTime + " - "
                            + endTime);
                } else {
                    MITDiningHouseDay day = venue.getMealsByDay().get(position);
                    if (day.getDateString().equals(getCurrentDate())) {
                        dateTextView.setText(getResources().getString(R.string.house_today, formatSimpletDate(day.getDateString())));
                    } else if (day.getDateString().equals(getYesterdayDate())) {
                        dateTextView.setText(getResources().getString(R.string.house_yesterday, formatSimpletDate(day.getDateString())));
                    } else if (day.getDateString().equals(getTomorrowDate())) {
                        dateTextView.setText(getResources().getString(R.string.house_tomorrow, formatSimpletDate(day.getDateString())));
                    } else {
                        dateTextView.setText(formatDate(day.getDateString()));
                    }

                    houseHoursTextView.setText(day.getMessage());
                    houseHoursTextView.setTextColor(getResources().getColor(R.color.status_red));
                    infoTextView.setText(day.getMessage());
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private int findCurrentMeal(List<MITDiningMeal> meals) {
        int index = 0;
        String currentDate = getCurrentDate();

        for (int i = 0; i < meals.size(); i++) {
            if (currentDate.equals(meals.get(i).getHouseDateString())) {
                String startTime = meals.get(i).getStartTimeString().replace(":", "");
                String endTime = meals.get(i).getEndTimeString().replace(":", "");
                if ((Integer.parseInt(getCurrentTime()) >= Integer.parseInt(startTime)) && (Integer.parseInt(getCurrentTime()) <= Integer.parseInt(endTime))) {
                    index = i;
                    houseHoursTextView.setText("Open until " + DateFormat.format("h:mm a", formatMealTime(meals.get(index).getEndTimeString())));
                    houseHoursTextView.setTextColor(getResources().getColor(R.color.status_green));
                    break;
                } else if (Integer.parseInt(getCurrentTime()) < Integer.parseInt(startTime)){
                    index = i;
                    houseHoursTextView.setText("Opens at " + DateFormat.format("h:mm a", formatMealTime(meals.get(index).getStartTimeString())));
                    houseHoursTextView.setTextColor(getResources().getColor(R.color.status_red));
                    break;
                } else if ((Integer.parseInt(getCurrentTime()) > Integer.parseInt(endTime))) {
                    if (i == meals.size() - 1) {
                        index = i;
                        houseHoursTextView.setText(getResources().getString(R.string.close_today));
                        houseHoursTextView.setTextColor(getResources().getColor(R.color.status_red));
                        break;
                    } else if (!currentDate.equals(meals.get(i + 1).getHouseDateString())) {
                        index = i;
                        houseHoursTextView.setText(getResources().getString(R.string.close_today));
                        houseHoursTextView.setTextColor(getResources().getColor(R.color.status_red));
                        break;
                    }
                }else {
                    continue;
                }
            }
        }

        return index;
    }

    private int findCurrentHouseDay(List<MITDiningHouseDay> days) {
        int index = 0;
        String currentDate = getCurrentDate();

        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).getDateString().equals(currentDate)) {
                index = i;
                break;
            }
        }

        return index;
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

    private String getYesterdayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        String date = dateFormat.format(yesterday);
        return date;
    }

    private String getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        String date = dateFormat.format(tomorrow);
        return date;
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        String time = sdf.format(cal.getTime());

        return time;
    }

    private String formatDate(String dateString) {
        Date date = new Date();
        SimpleDateFormat formatedDate = new SimpleDateFormat("EEEE, LLL dd");
        SimpleDateFormat originalDate = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date = originalDate.parse(dateString);
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "___________DateFormatError___________");
        }

        String formattedString = formatedDate.format(date);

        return formattedString;
    }

    private String formatSimpletDate(String dateString) {
        Date date = new Date();
        SimpleDateFormat formatedDate = new SimpleDateFormat("LLL dd");
        SimpleDateFormat originalDate = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date = originalDate.parse(dateString);
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "___________DateFormatError___________");
        }

        String formattedString = formatedDate.format(date);

        return formattedString;
    }


    private Date formatMealTime(String timeString) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "___________DateFormatError___________");
        }
        return date;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dining_house, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_filter) {
            Intent intent = new Intent(this, FiltersActivity.class);
            intent.putExtra(Constants.Dining.DINING_HOUSE, venue);
            intent.putExtra(Constants.Dining.HOUSE_MENU_PAGER_INDEX, houseMenuViewpager.getCurrentItem());
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}