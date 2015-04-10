package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.mit.mitmobile2.APIJsonResponse;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MitMapFragment;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleRouteAdapter;
import edu.mit.mitmobile2.shuttles.callbacks.MapFragmentCallback;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStop;
import timber.log.Timber;

/**
 * Created by sseligma on 4/5/15.
 */
public class ResourceListFragment extends MitMapFragment implements GoogleMap.InfoWindowAdapter {

    MITAPIClient apiClient;
    Context context;
    public ArrayList<Object> mapItems;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        //View resourceListContent = inflater.inflate(R.layout.content_resource_list, null);
        //addHeaderView(resourceListContent);
        this.context = getActivity();
        return view;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout v = (LinearLayout)mInflater.inflate(R.layout.resource_room_info_window, null);
        TextView roomLabel = (TextView)v.findViewById(R.id.room_label);
        roomLabel.setText(marker.getTitle());
        TextView resourceList = (TextView)v.findViewById(R.id.room_resource);
        resourceList.setText(marker.getSnippet());
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    @Override
    protected ArrayAdapter getMapItemAdapter() {
        return new ResourceListAdapter(context, R.layout.row_test, mapItems);
    }


    public void updateAndShowMapItems(ArrayList<Object> a) {
        this.mapItems = a;
        updateMapItems(a,true);
        displayMapItems();
    }

    @Override
    protected void listItemClicked(int position) {
        // since the listview has a transparent header, the array index is position - 1
        Object o = mapItems.get(position - 1);
        if (o.getClass().getSimpleName().equalsIgnoreCase("ResourceRoom")) {
            Timber.d("detail for multiple resources");
            Intent i = new Intent(context,ResourceViewActivity.class);
            startActivity(i);
        }
        else {
            Timber.d("detail for a single resource");
        }
    }
}
