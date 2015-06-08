package edu.mit.mitmobile2.dining.fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.DiningManager;
import edu.mit.mitmobile2.dining.adapters.HouseMealMenuAdapter;
import edu.mit.mitmobile2.dining.adapters.HouseMenuRecyclerAdapter;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import edu.mit.mitmobile2.dining.model.MITDiningHouseDay;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;
import edu.mit.mitmobile2.dining.model.MITDiningMenuItem;
import edu.mit.mitmobile2.dining.model.MITDiningVenues;
import edu.mit.mitmobile2.dining.model.SelectedFilters;
import edu.mit.mitmobile2.dining.utils.DiningUtils;
import edu.mit.mitmobile2.dining.utils.DividerItemDecoration;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HouseMenuFragment extends Fragment {

    private ListView menuDetailListView;
    private TextView noItemsTextView;
    private LinearLayout filterLayout;
    private LinearLayout filterImageLayout;
    private TextView filterNameTextView;
    private TextView closedTextView;

    private HouseMealMenuAdapter houseMealMenuAdapter;
    private HouseMenuRecyclerAdapter houseMenuRecyclerAdapter;
    private MITDiningMeal meal;
    private MITDiningHouseDay day;
    private Set<SelectedFilters.Filters> filtersSet;
    private List<MITDiningMenuItem> filterItems;
    private List<String> filterNames;
    private RecyclerView menuRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<MITDiningHouseVenue> houseVenues;

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

        if (getArguments() != null && getArguments().containsKey(Constants.Dining.HOUSE_MEAL)) {
            meal = getArguments().getParcelable(Constants.Dining.HOUSE_MEAL);
        } else if (getArguments() != null && getArguments().containsKey(Constants.Dining.HOUSE_DAY)){
            day = getArguments().getParcelable(Constants.Dining.HOUSE_DAY);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            menuDetailListView = (ListView) view.findViewById(R.id.menu_detail_list_view);
            noItemsTextView = (TextView) view.findViewById(R.id.no_items_text_view);
            filterLayout = (LinearLayout) view.findViewById(R.id.filter_layout);
            filterImageLayout = (LinearLayout) view.findViewById(R.id.filter_image_layout);
            filterNameTextView = (TextView) view.findViewById(R.id.filter_name_text_view);

            buildPortraitView();

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            menuRecyclerView = (RecyclerView) view.findViewById(R.id.menu_recycler_view);
            closedTextView = (TextView) view.findViewById(R.id.closed_text_view);

            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            menuRecyclerView.setLayoutManager(layoutManager);
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST);
            menuRecyclerView.addItemDecoration(itemDecoration);
            houseVenues = new ArrayList<>();
            if (meal != null) {
                closedTextView.setVisibility(View.GONE);
                menuRecyclerView.setVisibility(View.VISIBLE);
                setDiningHouseVenues();
                houseMenuRecyclerAdapter = new HouseMenuRecyclerAdapter(getActivity(), houseVenues, meal.getName(), meal.getHouseDateString());
                menuRecyclerView.setAdapter(houseMenuRecyclerAdapter);
            } else {
                closedTextView.setText(getResources().getString(R.string.dining_day_closed));
                closedTextView.setVisibility(View.VISIBLE);
                menuRecyclerView.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private void buildPortraitView() {
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
        RelativeLayout layout = (RelativeLayout) View.inflate(this.getActivity(), R.layout.dining_house_meal_dietary_flag, null);
        ImageView filterImageView = (ImageView) layout.findViewById(R.id.flag_image_view);

        if (DiningUtils.getMenuDietaryFlagImage(getActivity(), filterString) > 0) {
            filterImageView.setImageResource(DiningUtils.getMenuDietaryFlagImage(getActivity(), filterString));
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

    private void setDiningHouseVenues() {
        DiningManager.getDiningOptions(getActivity(), new Callback<MITDiningDining>() {

            @Override
            public void success(MITDiningDining mitDiningDining, Response response) {
                MITDiningVenues venues = mitDiningDining.getVenues();
                for (MITDiningHouseVenue houseVenue : venues.getHouse()) {
                    houseVenues.add(houseVenue);
                }
                houseMenuRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
                MitMobileApplication.bus.post(new OttoBusEvent.RefreshCompletedEvent());
            }
        });
    }
}