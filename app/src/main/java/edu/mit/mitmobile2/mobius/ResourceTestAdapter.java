package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.mobius.model.ResourceItem;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceTestAdapter extends ArrayAdapter<ResourceItem> {

    private Context mContext;
    private List<ResourceItem> resourceItems;

    public ResourceTestAdapter(Context context, int layoutResourceId, List<ResourceItem> resourceItems) {
        super(context, layoutResourceId, resourceItems);
        this.mContext = context;
        this.resourceItems = resourceItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.row_test, parent, false);
        TextView textView = (TextView)convertView.findViewById(R.id.txt_test);
        textView.setText("TEST");
        return convertView;
    }
}
