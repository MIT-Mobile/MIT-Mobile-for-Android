package edu.mit.mitmobile2.mobius;

import android.content.Context;
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
public class ResourceListAdapter extends ArrayAdapter<ResourceItem> {

    private static final int TYPE_ONLINE = 0;
    private static final int TYPE_OFFLINE = 1;
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_MAX_COUNT = 3;


    private Context mContext;
    private List<ResourceItem> resourceItems;

    public ResourceListAdapter(Context context, int layoutResourceId, List<ResourceItem> resourceItems) {
        super(context, layoutResourceId, resourceItems);
        this.mContext = context;
        this.resourceItems = resourceItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolderItem viewHolder = null;

        int type = getItemViewType(position);

        if(convertView==null){
            ResourceItem item = (ResourceItem)getItem(position);
            // inflate the layout


            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            switch (type) {
                case TYPE_ONLINE:
                    convertView = mInflater.inflate(R.layout.row_resource, parent, false);
                    // Name
                    viewHolder.resourceName = (TextView)convertView.findViewById(R.id.row_resource_name);

                    // Room
                    viewHolder.resourceRoom = (TextView)convertView.findViewById(R.id.row_resource_room);

                    // Status
                    viewHolder.resourceStatus = (TextView)convertView.findViewById(R.id.row_resource_status);
                    break;
                case TYPE_OFFLINE:
                    convertView = mInflater.inflate(R.layout.row_resource_offline, parent, false);
                    // Name
                    viewHolder.resourceName = (TextView)convertView.findViewById(R.id.row_resource_name);

                    // Room
                    viewHolder.resourceRoom = (TextView)convertView.findViewById(R.id.row_resource_room);

                    // Status
                    viewHolder.resourceStatus = (TextView)convertView.findViewById(R.id.row_resource_status);
                    break;

                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.row_resource_header, parent, false);
                    // Building Header
                    viewHolder.resourceHeader = (TextView)convertView.findViewById(R.id.row_resource_header);
                    break;
            }


            // store the holder with the view.
            convertView.setTag(viewHolder);
        }
        else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // object item based on the position
        ResourceItem item = getItem(position);

        // assign values if the object is not null
        if(item != null) {

            switch (type) {
                case TYPE_ONLINE:
                    viewHolder.resourceName.setText(item.getNumber() + ". " + item.getName());

                    // Room
                    viewHolder.resourceRoom.setText(item.getRoom());

                    // Status
                    viewHolder.resourceStatus.setText(item.getStatus());

                    break;

                case TYPE_OFFLINE:
                    viewHolder.resourceName.setText(item.getNumber() + ". " + item.getName());

                    // Room
                    viewHolder.resourceRoom.setText(item.getRoom());

                    // Status
                    viewHolder.resourceStatus.setText(item.getStatus());

                    break;

                case TYPE_HEADER:
                    // Header
                    viewHolder.resourceHeader.setText("Building " + item.getBuilding());
                    break;
            }

        }

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        if (resourceItems.get(position).getBuildingHeader()) {
            return TYPE_HEADER;
        }
        else {
            return resourceItems.get(position).getStatus().equals(ResourceItem.ONLINE) ? TYPE_ONLINE : TYPE_OFFLINE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    static class ViewHolderItem {
        TextView resourceHeader;
        TextView resourceName;
        TextView resourceRoom;
        TextView resourceStatus;
    }

}
