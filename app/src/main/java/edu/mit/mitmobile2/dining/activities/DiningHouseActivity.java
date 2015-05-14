package edu.mit.mitmobile2.dining.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.HouseMenuPagerAdapter;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;

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
    @InjectView(R.id.forward_image_view)
    ImageView forwardImageView;
    @InjectView(R.id.back_image_view)
    ImageView backImageView;

    @OnClick(R.id.info_image_view)
    public void gotoHouseInfo() {
        Intent intent = new Intent(this, DiningHouseInfoActivity.class);
        startActivity(intent);
    }

    private HouseMenuPagerAdapter houseMenuPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_house);
        ButterKnife.inject(this);

        MITDiningHouseVenue venue = getIntent().getParcelableExtra(Constants.Dining.DINING_HOUSE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle(venue.getShortName());

        buildHouseMenuPager();

        houseNameTextView.setText(venue.getName());
        dateTextView.setText("Today, " + getCurrentDate());

        houseHoursTextView.setText("Opens at 5:30PM");
        infoTextView.setText("Breakfast 8am - 10am");

        try {
            Picasso.with(this).load(venue.getIconURL()).placeholder(R.drawable.grey_rect).into(houseImageView);
        } catch (NullPointerException e) {
            Picasso.with(this).load(R.drawable.grey_rect).placeholder(R.drawable.grey_rect).into(houseImageView);
        }
    }


    private void buildHouseMenuPager() {
        houseMenuPagerAdapter = new HouseMenuPagerAdapter(getFragmentManager());
        houseMenuViewpager.setAdapter(houseMenuPagerAdapter);

    }

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("LLL dd");
        String date = sdf.format(cal.getTime());
        return date;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dining_house, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}