package edu.mit.mitmobile2.dining.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashSet;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.model.SelectedFilters;

public class FiltersActivity extends AppCompatActivity {

    private LinearLayout filtersLinearLayout;
    private SelectedFilters selectedFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        filtersLinearLayout = (LinearLayout) findViewById(R.id.filters_linear_layout);

        Gson gson = new Gson();
        String selectedFiltersJson = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this).getString(Constants.Dining.FILTERS_KEY, "");
        if (!TextUtils.isEmpty(selectedFiltersJson)) {
            selectedFilters = gson.fromJson(selectedFiltersJson, SelectedFilters.class);
            updateSelectedFilters();
        } else {
            selectedFilters = new SelectedFilters();
            selectedFilters.setFiltersSet(new HashSet<SelectedFilters.Filters>());
        }
    }

    @Override
    protected void onStop() {
        saveSelectedFilters();
        super.onStop();
    }

    private void updateSelectedFilters() {
        for (SelectedFilters.Filters filter : selectedFilters.getFiltersSet()) {
            int filterIndex = Arrays.asList(SelectedFilters.Filters.values()).indexOf(filter);
            //Spacer views also counted
            int filterViewIndex = filterIndex * 2;
            ImageView checkImage = (ImageView) filtersLinearLayout.getChildAt(filterViewIndex).findViewById(R.id.checkbox);
            checkImage.setVisibility(View.VISIBLE);
        }
    }

    public void onFilterRowClick(View view) {
        ImageView checkImage = (ImageView) view.findViewById(R.id.checkbox);
        checkImage.setVisibility(checkImage.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

        int viewIndex = filtersLinearLayout.indexOfChild(view);
        //Spacer views also counted
        int filterPosition = viewIndex / 2;
        selectedFilters.toggleFilter(SelectedFilters.Filters.values()[filterPosition]);
    }

    private void saveSelectedFilters() {
        Gson gson = new Gson();
        String selectedFiltersJson = gson.toJson(selectedFilters);
        SharedPreferences sharedPreferences = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this);
        sharedPreferences.edit().putString(Constants.Dining.FILTERS_KEY, selectedFiltersJson).apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dining_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_done) {
            saveSelectedFilters();
            Intent intent = new Intent(this, DiningHouseActivity.class);
            intent.putExtra(Constants.Dining.DINING_HOUSE, getIntent().getParcelableExtra(Constants.Dining.DINING_HOUSE));
            intent.putExtra(Constants.Dining.HOUSE_MENU_PAGER_INDEX, getIntent().getIntExtra(Constants.Dining.HOUSE_MENU_PAGER_INDEX, -1));
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
