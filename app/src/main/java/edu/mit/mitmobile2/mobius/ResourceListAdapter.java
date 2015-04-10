package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.row_resource_room, parent, false);
        ResourceRoom resourceRoom = (ResourceRoom)resourceData.get(position);

                // Map Index
                TextView mapIndex = (TextView) convertView.findViewById(R.id.map_index);

                // Roomset Name and room
                TextView  roomsetName = (TextView) convertView.findViewById(R.id.roomset_name);

                // Hours
                TextView hoursText = (TextView) convertView.findViewById(R.id.roomset_hours);

                // Open / Closed
                TextView roomsetOpen = (TextView) convertView.findViewById(R.id.roomset_open);
                TextView roomsetClosed = (TextView) convertView.findViewById(R.id.roomset_closed);

                LinearLayout resourceLayout = (LinearLayout)convertView.findViewById(R.id.resource_layout);


        mapIndex.setText(resourceRoom.getMapItemIndex() + ".");
        roomsetName.setText(resourceRoom.getRoomset_name() + " (" + resourceRoom.getRoom() + ")");

        String hours = "";
        if (resourceRoom.getHours() != null) {
            for (int i = 0; i < resourceRoom.getHours().size(); i++) {
                if (i > 0) {
                    hours += ",";
                }
                hours += resourceRoom.getHours().get(i).getStart_time() + " - " + resourceRoom.getHours().get(i).getEnd_time();
            }
            hoursText.setText(hours);
        }

        if (resourceRoom.isOpen()) {
            roomsetOpen.setVisibility(View.VISIBLE);
            roomsetClosed.setVisibility(View.GONE);
        }
        else {
            roomsetOpen.setVisibility(View.GONE);
            roomsetClosed.setVisibility(View.VISIBLE);
        }


        if (resourceRoom.getResources() != null) {
            for (int i = 0; i < resourceRoom.getResources().size(); i++) {
                LinearLayout resourceRow = (LinearLayout)mInflater.inflate(R.layout.row_resource, parent, false);

                resourceRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Timber.d("clicked");
                    }
                });
                TextView resourceName = (TextView)resourceRow.findViewById(R.id.resource_name);
                resourceName.setText(resourceRoom.getResources().get(i).getName());
                resourceLayout.addView(resourceRow);
            }
        }

        return convertView;
    }

    static class RoomViewHolder {
        TextView mapIndex;
        TextView roomsetName;
        TextView roomName;
        TextView resourceRoom;
        TextView resourceStatus;
        TextView hours;
        TextView roomsetOpen;
        TextView roomsetClosed;
        LinearLayout resourceLayout;
    }

    static class ResourceViewHolder {
        TextView resourceName;
    }

}
