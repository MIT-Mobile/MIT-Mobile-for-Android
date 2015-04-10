package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.mobius.model.ResourceItem;
import edu.mit.mitmobile2.mobius.model.ResourceRoom;
import edu.mit.mitmobile2.mobius.model.Roomset;
import timber.log.Timber;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceListAdapter extends ArrayAdapter<Object> {

    private static final int TYPE_RESOURCE_ROOM = 0;
    private static final int TYPE_RESOURCE_ITEM = 1;
    private static final int TYPE_MAX_COUNT = 2;


    private Context mContext;
    private List<Object> resourceData;
    public ResourceListAdapter(Context context, int layoutResourceId, List<Object> resourceData) {
        super(context, layoutResourceId, resourceData);
        this.mContext = context;
        this.resourceData = resourceData;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RoomHolder roomHolder = null;
        ResourceHolder resourceHolder= null;

        View v = convertView;
        int type = getItemViewType(position);

        if (v == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (type == TYPE_RESOURCE_ROOM) {
                v = mInflater.inflate(R.layout.row_resource_room, parent, false);
                // well set up the ViewHolder
                roomHolder = new RoomHolder();
                ResourceRoom rr = (ResourceRoom)resourceData.get(position);

                // Map Index
                roomHolder.mapIndex = (TextView) v.findViewById(R.id.map_index);

                // Roomset Name and room
                roomHolder.roomsetName = (TextView) v.findViewById(R.id.roomset_name);

                // Hours
                roomHolder.hoursText = (TextView) v.findViewById(R.id.roomset_hours);

                // Open / Closed
                roomHolder.roomsetOpen = (TextView) v.findViewById(R.id.roomset_open);
                roomHolder.roomsetClosed = (TextView) v.findViewById(R.id.roomset_closed);

                v.setTag(roomHolder);
            }
            else {
                resourceHolder = new ResourceHolder();

                v = mInflater.inflate(R.layout.row_resource, parent, false);
                ResourceItem r = (ResourceItem)resourceData.get(position);

                resourceHolder.resourceName = (TextView)v.findViewById(R.id.resource_name);

                v.setTag(resourceHolder);
            }


        }
        else {
            if (type == TYPE_RESOURCE_ROOM) {
                roomHolder = (RoomHolder) v.getTag();
            }
            else {
                resourceHolder = (ResourceHolder)v.getTag();
            }
        }

        // Get item and populate
        if (type == TYPE_RESOURCE_ROOM) {
            ResourceRoom rr = (ResourceRoom)resourceData.get(position);
            // assign values if the object is not null
            if(rr != null) {
                // get the TextView from the ViewHolder and then set the text (item name) and tag (item ID) values
                roomHolder.mapIndex.setText(rr.getMapItemIndex() + ".");
                roomHolder.roomsetName.setText(rr.getRoom_label());

                String hours = "";
                if (rr.getHours() != null) {
                    for (int i = 0; i < rr.getHours().size(); i++) {
                        if (i > 0) {
                            hours += ",";
                        }
                        hours += rr.getHours().get(i).getStart_time() + " - " + rr.getHours().get(i).getEnd_time();
                    }
                    roomHolder.hoursText.setText(hours);
                }


                if (rr.isOpen()) {
                    roomHolder.roomsetOpen.setVisibility(View.VISIBLE);
                    roomHolder.roomsetClosed.setVisibility(View.GONE);
                }
                else {
                    roomHolder.roomsetOpen.setVisibility(View.GONE);
                    roomHolder.roomsetClosed.setVisibility(View.VISIBLE);
                }

            }

        }
        else {
            ResourceItem r  = (ResourceItem)resourceData.get(position);
            if (r != null) {
                resourceHolder.resourceName.setText(r.getName());
            }
        }

        return v;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = resourceData.get(position).getClass().getSimpleName().equalsIgnoreCase("ResourceRoom") ? 0 : 1;
        Timber.d("viewType = " + viewType);
        return viewType;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    static class RoomHolder {
        // Map Index
        TextView mapIndex;

        // Roomset Name and room
        TextView  roomsetName;

        // Hours
        TextView hoursText;

        // Open / Closed
        TextView roomsetOpen;
        TextView roomsetClosed;

    }
    static class ResourceHolder {
        TextView resourceName;
    }

}
