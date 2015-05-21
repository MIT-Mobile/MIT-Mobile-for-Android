package edu.mit.mitmobile2.libraries.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITHoldItem;

public class HoldsFragment extends AccountPageFragment {

    public static HoldsFragment newInstance() {
        return new HoldsFragment();
    }

    public HoldsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // TODO: Get holds
        List<MITLibrariesMITHoldItem> holdItems = new ArrayList<>();

        MITLibrariesMITHoldItem mitHoldItem = new Gson().fromJson("{\n" +
                "      status: \"Ready. Will be held until 3/15/2015\",\n" +
                "      pickup_location: \"Hayden Library\",\n" +
                "      call_number: \"HD9259.B2 Z463 2012\",\n" +
                "      author: \"Cohen, Rich.\",\n" +
                "      year: \"2012\",\n" +
                "      title: \"The fish that ate the whale : the life and times of America's banana king / Rich Cohen.\",\n" +
                "      imprint: \"Farrar, Straus and Giroux,\",\n" +
                "      isbn: \"9780374299279\",\n" +
                "      doc_number: \"2114046\",\n" +
                "      material: \"Book\",\n" +
                "      sub_library: \"Hayden Library\",\n" +
                "      barcode: \"39080029063788\",\n" +
                "      ready_for_pickup: true,\n" +
                "      cover_images: [\n" +
                "        {\n" +
                "          width: 170,\n" +
                "          height: 260,\n" +
                "          url: \"https://m.mit.edu/apis/libraries/cover_images/9780374299279?width=170&height=260\"\n" +
                "        },\n" +
                "        {\n" +
                "          width: 110,\n" +
                "          height: 170,\n" +
                "          url: \"https://m.mit.edu/apis/libraries/cover_images/9780374299279?width=110&height=170\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }", MITLibrariesMITHoldItem.class);

        holdItems.add(mitHoldItem);

        int pickupCount = 0;

        for (MITLibrariesMITHoldItem holdItem : holdItems) {
            if (holdItem.isReadyForPickup()) {
                pickupCount++;
            }
        }

        buildAndAddHeaderView(String.format(getString(R.string.library_holds_pickup), holdItems.size(), pickupCount), null, pickupCount > 0 ? R.color.open_green : R.color.black);

        adapter.updateHoldItems(holdItems);

        return view;
    }
}
