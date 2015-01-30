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

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;


    private Context mContext;
    private List<ResourceItem> resourceItems;

    public ResourceRowAdapter(Context context, int layoutResourceId, List<ResourceItem> resourceItems) {
        super(context, layoutResourceId, resourceItems);
        this.mContext = context;
        this.resourceItems = resourceItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolderItem viewHolder;

        if(convertView==null){
            ResourceItem item = (ResourceItem)getItem(position);
            // inflate the layout

            convertView = mInflater.inflate(R.layout.row_resource, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();

            // Name
            viewHolder.resourceName = (TextView)convertView.findViewById(R.id.row_resource_name);

            // Room
            viewHolder.resourceRoom = (TextView)convertView.findViewById(R.id.row_resource_room);

            // Status
            viewHolder.resourceStatus = (TextView)convertView.findViewById(R.id.row_resource_status);

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
            //Name
            viewHolder.resourceName.setText(item.getNumber() + ". " + item.getName());

            // Room
            viewHolder.resourceRoom.setText(item.getRoom());

            // Status
            viewHolder.resourceStatus.setText(item.getStatus());
            if (viewHolder.resourceStatus.getText().equals(ResourceItem.OFFLINE)) {
                viewHolder.resourceStatus.setTextColor(Color.RED);
            }
        }

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        return resourceItems.get(position).getShowBuilding() ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    static class ViewHolderItem {
        TextView resourceName;
        TextView resourceRoom;
        TextView resourceStatus;
    }

}
