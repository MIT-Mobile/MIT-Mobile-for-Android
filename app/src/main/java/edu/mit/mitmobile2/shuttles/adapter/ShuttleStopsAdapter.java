package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleStopsAdapter extends ArrayAdapter<MITShuttleStopWrapper> {

    private Context mContext;

    public ShuttleStopsAdapter(Context context, int resource, List<MITShuttleStopWrapper> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolderItem viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.stops_list_row, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.stopIcon = (ImageView) convertView.findViewById(R.id.stop_icon);
            viewHolder.stopName = (TextView) convertView.findViewById(R.id.stop_name);
            viewHolder.stopPrediction = (TextView) convertView.findViewById(R.id.stop_prediction);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // object item based on the position
        MITShuttleStopWrapper stopWrapper = getItem(position);

        viewHolder.stopName.setText(stopWrapper.getTitle());
        if (stopWrapper.getPredictions() != null && stopWrapper.getPredictions().size() > 0) {
            viewHolder.stopPrediction.setText(String.valueOf(stopWrapper.getPredictions().get(0).getSeconds()));
        } else {
            viewHolder.stopPrediction.setText("-");
        }

        return convertView;
    }

    static class ViewHolderItem {
        ImageView stopIcon;
        TextView stopName;
        TextView stopPrediction;
    }
}
