package edu.mit.mitmobile2.about;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import edu.mit.mitmobile2.BuildConfig;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.about.activities.AboutMitActivity;
import edu.mit.mitmobile2.about.activities.CreditsActivity;
import edu.mit.mitmobile2.shared.logging.LoggingManager;

public class AboutFragment extends Fragment implements AdapterView.OnItemClickListener {

    private TextView textViewVersion;
    private ListView listView;

    private String[] menuItems;

    private String versionName = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, null);

        textViewVersion = (TextView) view.findViewById(R.id.about_tv_version);
        listView = (ListView) view.findViewById(R.id.list);

        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LoggingManager.Timber.d("Get version name error", e);
        }

        textViewVersion.setText(getString(R.string.about_version_title_template, versionName));

        menuItems = new String[3];
        menuItems[0] = getString(R.string.about_credits);
        menuItems[1] = getString(R.string.about_about);
        menuItems[2] = getString(R.string.about_send_feedback);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, menuItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return view;
    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                Intent intent = new Intent(getActivity(), CreditsActivity.class);
                startActivity(intent);
            }
            break;
            case 1: {
                Intent intent = new Intent(getActivity(), AboutMitActivity.class);
                startActivity(intent);
            }
            break;
            case 2: {
                String mailSubject = getString(R.string.about_feedback_template,
                        versionName,
                        BuildConfig.buildDescription,
                        "Android",
                        Build.VERSION.RELEASE);

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Constants.FEEDBACK_EMAIL, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
                startActivity(Intent.createChooser(emailIntent, getString(R.string.about_send_feedback)));

            }
            break;
        }
    }
}
