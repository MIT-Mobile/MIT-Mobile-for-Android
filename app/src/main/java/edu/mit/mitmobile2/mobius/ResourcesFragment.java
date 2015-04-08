package edu.mit.mitmobile2.mobius;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.util.Log;
import android.widget.Button;


import edu.mit.mitmobile2.MitMapFragment;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.activities.ShuttleRouteActivity;


public class ResourcesFragment extends Fragment {

    int contentLayoutId = R.layout.content_resources;
    Button btnResourceShop;
    Button btnResourceType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_resources, null);

        btnResourceShop = (Button)view.findViewById(R.id.btnResourceShop);

        btnResourceShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ResourceShopsActivity.class);
                startActivity(i);
            }
        });

        btnResourceType = (Button)view.findViewById(R.id.btnResourceType);

        btnResourceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ResourceTypesActivity.class);
                startActivity(i);
            }
        });

        return view;

    }

}
