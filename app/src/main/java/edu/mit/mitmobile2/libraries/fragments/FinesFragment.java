package edu.mit.mitmobile2.libraries.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITFineItem;

public class FinesFragment extends AccountPageFragment {

    public static FinesFragment newInstance() {
        return new FinesFragment();
    }

    public FinesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // TODO: Get fines

        List<MITLibrariesMITFineItem> fineItems = new ArrayList<>();

        MITLibrariesMITFineItem mitFineItem = new Gson().fromJson("{\n" +
                "      status: \"Not paid by/credited to patron\",\n" +
                "      description: \"Replacement Cost\",\n" +
                "      call_number: \"GV880.Z56 1992\",\n" +
                "      author: \"Zimbalist, Andrew S.\",\n" +
                "      year: \"1992\",\n" +
                "      title: \"Baseball and billions : a probing look inside the big business of our national pastime / by Andrew Z\",\n" +
                "      imprint: \"BasicBooks,\",\n" +
                "      isbn: \"0465006140\",\n" +
                "      doc_number: \"635600\",\n" +
                "      material: \"Book\",\n" +
                "      sub_library: \"Dewey Library\",\n" +
                "      barcode: \"39080008409341\",\n" +
                "      formatted_amount: \"$135.00\",\n" +
                "      amount: 13500,\n" +
                "      fined_at: \"2014-09-15T00:00:00-04:00\",\n" +
                "      cover_images: [\n" +
                "        {\n" +
                "          width: 170,\n" +
                "          height: 260,\n" +
                "          url: \"https://m.mit.edu/apis/libraries/cover_images/0465006140?width=170&height=260\"\n" +
                "        },\n" +
                "        {\n" +
                "          width: 110,\n" +
                "          height: 170,\n" +
                "          url: \"https://m.mit.edu/apis/libraries/cover_images/0465006140?width=110&height=170\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }", MITLibrariesMITFineItem.class);

        int fineTotal = 0;

        fineItems.add(mitFineItem);

        for (MITLibrariesMITFineItem fineItem : fineItems) {
            fineTotal += fineItem.getAmount();
        }

        String today = new SimpleDateFormat("M/dd/yyy", Locale.US).format(Calendar.getInstance().getTime());

        buildAndAddHeaderView(String.format(getString(R.string.fines_total), new DecimalFormat("#.##").format(fineTotal * 0.01), today), getString(R.string.library_fine_payment_info), fineTotal > 0 ? R.color.closed_red : R.color.black);

        adapter.updateFineItems(fineItems);

        return view;
    }
}
