package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.R;

/**
 * Created by sseligma on 2/3/15.
 */
public class ResourceItemInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context mContext;

    ResourceItemInfoWindow(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layoutId;
        LinearLayout view = null;

        String snippet = marker.getSnippet();
        try {
            JSONObject data = new JSONObject(snippet);
            layoutId = R.layout.resource_info_window;
            view = (LinearLayout)mInflater.inflate(layoutId,null);

            // Name
            TextView resourceName = (TextView)view.findViewById(R.id.row_resource_name);
            resourceName.setText(data.getString("name"));

            // Room
            TextView resourceRoom = (TextView)view.findViewById(R.id.row_resource_room);
            resourceRoom.setText(data.getString("room"));

            // Status
            TextView resourceStatus = (TextView)view.findViewById(R.id.row_resource_status);
            String status = data.getString("status");
            resourceStatus.setText(status);
            if (status.equals(ResourceItem.OFFLINE)) {
                resourceStatus.setTextColor(Color.RED);
            }

            //MapItemIndex
            TextView mapItemIndex = (TextView)view.findViewById(R.id.mapItemIndex);
            String index = data.getString("mapItemIndex");
            mapItemIndex.setText(index);

        }
            catch (JSONException e) {
               Log.d("ZZZ", e.getMessage());
        }


        return view;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
