package edu.mit.mitmobile2.dining.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.HouseMealMenuAdapter;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;

public class HouseMenuFragment extends Fragment {

    @InjectView(R.id.menu_detail_list_view)
    ListView menuDetailListView;
    @InjectView(R.id.no_items_text_view)
    TextView noItemsTextView;

    private HouseMealMenuAdapter houseMealMenuAdapter;
    private MITDiningMeal meal;

    public static HouseMenuFragment newInstance(MITDiningMeal meal) {
        HouseMenuFragment fragment = new HouseMenuFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.Dining.HOUSE_MEAL, meal);
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
        }

        if ((meal.getItems() != null) && (meal.getItems().size() > 0)) {
            houseMealMenuAdapter = new HouseMealMenuAdapter(getActivity(), meal.getItems());
            menuDetailListView.setAdapter(houseMealMenuAdapter);
            noItemsTextView.setVisibility(View.GONE);
        } else {
            noItemsTextView.setVisibility(View.VISIBLE);
            menuDetailListView.setVisibility(View.GONE);
        }

        return view;
    }
}