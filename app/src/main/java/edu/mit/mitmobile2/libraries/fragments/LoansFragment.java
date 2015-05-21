package edu.mit.mitmobile2.libraries.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        int overdueCount = 0;

        for (MITLibrariesMITLoanItem loanItem : loanItems) {
            if (loanItem.isOverdue()) {
                overdueCount++;
            }
        }

        buildAndAddHeaderView(String.format(getString(R.string.loans_overdue), loanItems.size(), overdueCount), null, R.color.black);

        return view;
    }

}
