package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleRouteAdapter extends ArrayAdapter<MITShuttleStopWrapper> {

    private Context mContext;
    private String routeId;

    public ShuttleRouteAdapter(Context context, int resource, List<MITShuttleStopWrapper> objects, String routeId) {
        super(context, resource, objects);
        mContext = context;
        this.routeId = routeId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolderItem viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.stop_list_item, parent, false);
            viewHolder = new ViewHolderItem(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // object item based on the position
        MITShuttleStopWrapper stopWrapper = getItem(position);

        if (position == 0) {
            viewHolder.stopView.setVisibility(View.GONE);
        } else {
            viewHolder.stopView.setVisibility(View.VISIBLE);
        }

        viewHolder.stopName.setText(stopWrapper.getTitle());
        viewHolder.stopName.setText(stopWrapper.getTitle());
        if (stopWrapper.getPredictions() != null && stopWrapper.getPredictions().size() > 0) {
            MITShuttlePrediction prediction = stopWrapper.getPredictions().get(0);
            int timeInMins = prediction.getSeconds() / 60;
            if (timeInMins == 0) {
                viewHolder.stopPrediction.setText("now");
                viewHolder.stopPrediction.setTextColor(mContext.getResources().getColor(R.color.mit_tintColor));
            } else {
                viewHolder.stopPrediction.setText(timeInMins + "m");
                viewHolder.stopPrediction.setTextColor(mContext.getResources().getColor(R.color.contents_text));
            }
        } else {
            viewHolder.stopPrediction.setText("-");
        }

        return convertView;
    }

    static class ViewHolderItem {
        @InjectView(R.id.stop_icon)
        ImageView stopIcon;

        @InjectView(R.id.stop_name)
        TextView stopName;

        @InjectView(R.id.stop_prediction)
        TextView stopPrediction;

        @InjectView(R.id.stop_view)
        View stopView;

        ViewHolderItem(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
