package edu.mit.mitmobile2.dining.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;

public class FiltersActivity extends AppCompatActivity {

    public enum Filters {
        FARM_TO_FORK,
        FOR_YOUR_WELL_BEING,
        HALAL,
        HUMANE,
        IN_BALANCE,
        KOSHER,
        MADE_WITHOUT_GLUTEN,
        ORGANIC,
        SEAFOOD_WATCH,
        VEGAN,
        VEGETARIAN
    }

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
            selectedFilters.setFiltersSet(new HashSet<Filters>());
        }
    }

    @Override
    protected void onStop() {
        saveSelectedFilters();
        super.onStop();
    }

    private void updateSelectedFilters() {
        for (Filters filter : selectedFilters.getFiltersSet()) {
            int filterIndex = Arrays.asList(Filters.values()).indexOf(filter);
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
        selectedFilters.toggleFilter(Filters.values()[filterPosition]);
    }

    private void saveSelectedFilters() {
        Gson gson = new Gson();
        String selectedFiltersJson = gson.toJson(selectedFilters);
        SharedPreferences sharedPreferences = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this);
        sharedPreferences.edit().putString(Constants.Dining.FILTERS_KEY, selectedFiltersJson).apply();
    }

    public class SelectedFilters {
        private Set<Filters> filtersSet;

        public SelectedFilters() {

        }

        public Set<Filters> getFiltersSet() {
            return filtersSet;
        }

        public void setFiltersSet(Set<Filters> filtersSet) {
            this.filtersSet = filtersSet;
        }

        public void toggleFilter(Filters filter) {
            if (filtersSet.contains(filter)) {
                filtersSet.remove(filter);
            } else {
                filtersSet.add(filter);
            }
        }
    }
}
