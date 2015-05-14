package edu.mit.mitmobile2.dining.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.HouseMenuDetailAdapter;

public class HouseMenuFragment extends Fragment {

    @InjectView(R.id.menu_detail_list_view)
    ListView menuDetailListView;

    private HouseMenuDetailAdapter houseMenuDetailAdapter;

    private List<String> testNames;

    public static HouseMenuFragment newInstance() {
        HouseMenuFragment houseMenuFragment = new HouseMenuFragment();
        return houseMenuFragment;
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
        View view = inflater.inflate(R.layout.fragment_dinning_house_menu, container, false);
        ButterKnife.inject(this, view);

        //TODO connect to server
        testNames = new ArrayList<>();
        buildTestData();
        houseMenuDetailAdapter = new HouseMenuDetailAdapter(getActivity(), testNames);
        menuDetailListView.setAdapter(houseMenuDetailAdapter);

        return view;
    }

    private void buildTestData() {
        testNames.add("Chicken in peanut hoisin sauce");
        testNames.add("Chicken in peanut hoisin sauce");
        testNames.add("Chicken in peanut hoisin sauce");
        testNames.add("Chicken in peanut hoisin sauce");
        testNames.add("Chicken in peanut hoisin sauce");
    }
}