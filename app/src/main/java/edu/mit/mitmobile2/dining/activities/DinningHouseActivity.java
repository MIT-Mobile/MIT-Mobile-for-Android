package edu.mit.mitmobile2.dining.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.HouseMenuPagerAdapter;
import edu.mit.mitmobile2.dining.fragments.HouseWeekFragment;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;

public class DinningHouseActivity extends MITActivity {

    @InjectView(R.id.house_image_view)
    ImageView houseImageView;
    @InjectView(R.id.house_name_text_view)
    TextView houseNameTextView;
    @InjectView(R.id.house_hours_text_view)
    TextView houseHoursTextView;
    @InjectView(R.id.dinning_house_menu_viewpager)
    ViewPager houseMenuViewpager;
    @InjectView(R.id.info_image_view)
    ImageView infoImageView;

    private HouseMenuPagerAdapter houseMenuPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinning_house);
        ButterKnife.inject(this);

        MITDiningHouseVenue venue = getIntent().getParcelableExtra(Constants.Dining.DINNING_HOUSE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle(venue.getShortName());

        buildHouseMenuPager();

        HouseWeekFragment fragment = new HouseWeekFragment();
        getFragmentManager().beginTransaction().replace(R.id.dinning_house_week_fragment, fragment).commit();

        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DinningHouseInfoActivity.class);
                startActivity(intent);
            }
        });

        houseNameTextView.setText(venue.getName());
        houseHoursTextView.setText("Opens at 5:30PM");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dinning_house, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}