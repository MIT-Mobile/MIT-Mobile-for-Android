package edu.mit.mitmobile2.dining.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.HouseMealMenuAdapter;
import edu.mit.mitmobile2.dining.model.MITDiningHouseDay;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;
import edu.mit.mitmobile2.dining.model.MITDiningMenuItem;
import edu.mit.mitmobile2.dining.model.SelectedFilters;

public class HouseMenuFragment extends Fragment {

    @InjectView(R.id.menu_detail_list_view)
    ListView menuDetailListView;
    @InjectView(R.id.no_items_text_view)
    TextView noItemsTextView;
    @InjectView(R.id.filter_layout)
    LinearLayout filterLayout;
    @InjectView(R.id.filter_image_layout)
    LinearLayout filterImageLayout;
    @InjectView(R.id.filter_name_text_view)
    TextView filterNameTextView;

    private HouseMealMenuAdapter houseMealMenuAdapter;
    private MITDiningMeal meal;
    private MITDiningHouseDay day;
    private Set<SelectedFilters.Filters> filtersSet;
    private List<MITDiningMenuItem> filterItems;
    private List<String> filterNames;

    public static HouseMenuFragment newInstance(MITDiningMeal meal) {
        HouseMenuFragment fragment = new HouseMenuFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.Dining.HOUSE_MEAL, meal);
        fragment.setArguments(args);

        return fragment;
    }


    public static HouseMenuFragment newInstance(MITDiningHouseDay day) {
        HouseMenuFragment fragment = new HouseMenuFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.Dining.HOUSE_DAY, day);
        fragment.setArguments(args);

        return fragment;
    }

    public HouseMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_house_menu, container, false);
        ButterKnife.inject(this, view);

        if (getArguments() != null && getArguments().containsKey(Constants.Dining.HOUSE_MEAL)) {
            meal = getArguments().getParcelable(Constants.Dining.HOUSE_MEAL);
        } else if (getArguments() != null && getArguments().containsKey(Constants.Dining.HOUSE_DAY)){
            day = getArguments().getParcelable(Constants.Dining.HOUSE_DAY);
        }

        SharedPreferences sharedPreferences = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this.getActivity());
        String filterGson = sharedPreferences.getString(Constants.Dining.FILTERS_KEY, "");

        filterItems = new ArrayList<>();

        if (meal != null) {
            if (filterGson != "" && meal.getItems() != null) {
                filtersSet = getFiltersSet(filterGson);
                filterNames = new ArrayList<>();
                filterNames = convertToFilterList(filtersSet);

                if (filterNames.size() > 0) {
                    updateMealMenuItems();
                    filterLayout.setVisibility(View.VISIBLE);
                    for (int i =0; i < filterNames.size(); i++) {
                        buildAndAddFilterImages(filterNames.get(i).toString());
                    }
                } else {
                    filterLayout.setVisibility(View.GONE);
                    filterItems.addAll(meal.getItems());
                    buildAndUpdateMenuSegment(filterItems);
                }
            } else {
                filterLayout.setVisibility(View.GONE);
                if(meal.getItems() != null) {
                    filterItems.addAll(meal.getItems());
                }
                buildAndUpdateMenuSegment(filterItems);
            }
        } else if (day != null) {
            if (filterGson != "") {
                filtersSet = getFiltersSet(filterGson);
                filterNames = new ArrayList<>();
                filterNames = convertToFilterList(filtersSet);

                if (filterNames.size() > 0) {
                    filterLayout.setVisibility(View.VISIBLE);
                    for (int i =0; i < filterNames.size(); i++) {
                        buildAndAddFilterImages(filterNames.get(i).toString());
                    }
                } else {
                    filterLayout.setVisibility(View.GONE);
                }

            } else {
                filterLayout.setVisibility(View.GONE);
            }

            noItemsTextView.setText(getResources().getString(R.string.dining_day_closed));
            noItemsTextView.setVisibility(View.VISIBLE);
            menuDetailListView.setVisibility(View.GONE);
        }

        return view;
    }

    private Set<SelectedFilters.Filters> getFiltersSet(String filterGson) {
        Gson gson = new Gson();
        SelectedFilters filters = gson.fromJson(filterGson, SelectedFilters.class);
        return filters.getFiltersSet();
    }

    private void updateMealMenuItems() {
        for (int i = 0; i < meal.getItems().size(); i++) {
            if (meal.getItems().get(i).getDietaryFlags() != null) {
                for (String flag : meal.getItems().get(i).getDietaryFlags()) {
                    if (checkContainsFilter(flag) && !checkHasMenuItem(meal.getItems().get(i))) {
                        filterItems.add(meal.getItems().get(i));
                    }
                }
            }
        }
        buildAndUpdateMenuSegment(filterItems);
    }

    private boolean checkHasMenuItem(MITDiningMenuItem item) {
        boolean hasMenuItem = false;
        for (int i = 0; i < filterItems.size(); i++) {
            if (filterItems.get(i).equals(item)) {
                hasMenuItem = true;
                break;
            }
        }
        return hasMenuItem;
    }

    private boolean checkContainsFilter(String flag) {
        boolean hasFilter = false;
        for (String filterString : filterNames) {
            if (filterString.equals(flag)) {
                hasFilter = true;
                break;
            }
        }
        return hasFilter;
    }

    private void buildAndUpdateMenuSegment(List<MITDiningMenuItem> menuItems) {
        if ((meal.getItems() != null) && (meal.getItems().size() > 0)) {
            houseMealMenuAdapter = new HouseMealMenuAdapter(getActivity(), menuItems);
            menuDetailListView.setAdapter(houseMealMenuAdapter);
            noItemsTextView.setVisibility(View.GONE);
        } else {
            noItemsTextView.setVisibility(View.VISIBLE);
            menuDetailListView.setVisibility(View.GONE);
        }
    }

    private void buildAndAddFilterImages(String filterString) {
        RelativeLayout layout = (RelativeLayout) View.inflate(this.getActivity(), R.layout.dining_filter_segment, null);
        ImageView filterImageView = (ImageView) layout.findViewById(R.id.filter_image_view);

        String newFilterString;
        newFilterString = filterString.replaceAll(" ", "");
        int resId = this.getResources().getIdentifier("dining_" + newFilterString
                        .toLowerCase(), "drawable",
                getActivity().getPackageName());
        if (resId > 0) {
            filterImageView.setImageResource(resId);
        }

        if (filtersSet.size() == 1) {
            filterNameTextView.setText(filterString.replace("_", " ").toLowerCase());
            filterNameTextView.setVisibility(View.VISIBLE);
        } else {
            filterNameTextView.setVisibility(View.GONE);
        }

        filterImageLayout.addView(layout);
    }

    private List<String> convertToFilterList(Set<SelectedFilters.Filters> filtersSet) {
        List<SelectedFilters.Filters> filterList = new ArrayList<>(filtersSet);
        List<String> filterNames = new ArrayList<>();

        for (SelectedFilters.Filters filter : filterList) {
            filterNames.add(filter.name().replaceAll("_", " ").toLowerCase());
        }

        return filterNames;
    }
}