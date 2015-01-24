package edu.mit.mitmobile2.resources;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceRowAdapter extends ArrayAdapter<ResourceItem> {
    private Context mContext;
    private List<ResourceItem> resourceItems;

    public ResourceRowAdapter(Context context, int layoutResourceId, List<ResourceItem> resourceItems) {
        super(context, layoutResourceId, resourceItems);
        this.mContext = context;
        this.resourceItems = resourceItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_resource, null);
        }

        ResourceItem item = (ResourceItem) getItem(position);

        if (item != null) {

            // Name
            TextView resource_name = (TextView) v.findViewById(R.id.row_resource_name);
            resource_name.setText(item.getNumber() + ". " + item.getName());
            resource_name.setTextColor(Color.BLACK);

            // Room
            TextView resource_room = (TextView) v.findViewById(R.id.row_resource_room);
            resource_room.setText(item.getRoom());
            resource_room.setTextColor(Color.BLACK);

            // Status
            TextView resource_status = (TextView) v.findViewById(R.id.row_resource_status);
            resource_status.setText(item.getStatus());
            if (item.getStatus().equalsIgnoreCase(ResourceItem.ONLINE)) {
                resource_status.setTextColor(Color.GREEN);
            }
            else if (item.getStatus().equalsIgnoreCase(ResourceItem.OFFLINE)) {
                resource_status.setTextColor(Color.RED);
            }

        }

        return v;
    }

}
