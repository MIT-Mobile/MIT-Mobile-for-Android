package edu.mit.mitmobile2.about;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import edu.mit.mitmobile2.R;

public class AboutFragment extends Fragment {

    private TextView textViewVersion;
    private ListView listView;

    private String[] menuItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, null);

        textViewVersion = (TextView) view.findViewById(R.id.about_tv_version);
        listView = (ListView) view.findViewById(R.id.list);

        String versionName = "";
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        textViewVersion.setText(getString(R.string.about_version_title_template, versionName));

        menuItems = new String[3];
        menuItems[0] = getString(R.string.about_credits);
        menuItems[1] = getString(R.string.about_about);
        menuItems[2] = getString(R.string.about_send_feedback);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, menuItems);
        listView.setAdapter(adapter);

        return view;
    }
}
