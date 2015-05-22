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
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITLoanItem;

public class LoansFragment extends AccountPageFragment {

    public static LoansFragment newInstance() {
        return new LoansFragment();
    }

    public LoansFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        //TODO: Get loans

        List<MITLibrariesMITLoanItem> loanItems = new ArrayList<>();

        MITLibrariesMITLoanItem mitLoanItem = new Gson().fromJson("{\n" +
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
                "      loaned_at: \"2014-08-12T17:32:00-04:00\",\n" +
                "      due_at: \"2014-08-13T18:00:00-04:00\",\n" +
                "      overdue: true,\n" +
                "      long_overdue: true,\n" +
                "      formatted_pending_fine: \"$15.00\",\n" +
                "      pending_fine: 1500,\n" +
                "      due_text: \"Long overdue — $15.00 fine accrued\",\n" +
                "      has_hold: false,\n" +
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
                "    }", MITLibrariesMITLoanItem.class);

        loanItems.add(mitLoanItem);

        int overdueCount = 0;

        for (MITLibrariesMITLoanItem loanItem : loanItems) {
            if (loanItem.isOverdue()) {
                overdueCount++;
            }
        }

        buildAndAddHeaderView(String.format(getString(R.string.loans_overdue), loanItems.size(), overdueCount), null, R.color.black);

        adapter.updateLoanItems(loanItems);

        return view;
    }

}
