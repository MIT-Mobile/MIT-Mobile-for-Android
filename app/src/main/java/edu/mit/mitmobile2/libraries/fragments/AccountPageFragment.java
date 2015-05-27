package edu.mit.mitmobile2.libraries.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.activities.LibraryAccountItemDetailActivity;
import edu.mit.mitmobile2.libraries.adapter.AccountItemAdapter;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITItem;

public class AccountPageFragment extends Fragment {

    @InjectView(R.id.account_tab_listview)
    protected ListView listView;

    protected AccountItemAdapter adapter;

    public AccountPageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_page_fragment, null);

        ButterKnife.inject(this, view);

        adapter = new AccountItemAdapter(getActivity(), new ArrayList<MITLibrariesMITItem>());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                Intent intent = new Intent(getActivity(), LibraryAccountItemDetailActivity.class);
                intent.putExtra(Constants.ACCOUNT_ITEM_KEY, (MITLibrariesMITItem) adapter.getItem(position - 1));
                startActivity(intent);
            }
        });

        //TODO: Query API for account info

        return view;
    }

    public void buildAndAddHeaderView(String s1, String s2, int resColorS1) {
        View view = View.inflate(getActivity(), R.layout.library_item_header, null);
        TextView item1 = (TextView) view.findViewById(R.id.header_item_1);
        TextView item2 = (TextView) view.findViewById(R.id.header_item_2);

        if (!TextUtils.isEmpty(s1)) {
            item1.setText(s1);
            item1.setTextColor(getResources().getColor(resColorS1));
        } else {
            item1.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(s2)) {
            item2.setText(s2);
        } else {
            item2.setVisibility(View.GONE);
        }

        listView.addHeaderView(view);
    }
}
