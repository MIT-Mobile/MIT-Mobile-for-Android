package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.ShuttleUtils;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleRouteAdapter extends ArrayAdapter<MITShuttleStopWrapper> {

    private Context mContext;

    public ShuttleRouteAdapter(Context context, int resource, List<MITShuttleStopWrapper> objects) {
        super(context, resource, objects);
        mContext = context;
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

        viewHolder.stopIcon.setImageResource(R.drawable.stop_icon_def);
        viewHolder.stopPrediction.setTextColor(mContext.getResources().getColor(R.color.contents_text));
        viewHolder.stopPrediction.setText(ShuttleUtils.formatPredictionFromStop(stopWrapper));

        if (viewHolder.stopPrediction.getText().toString().equals(ShuttleUtils.NOW)) {
            viewHolder.stopPrediction.setTextColor(mContext.getResources().getColor(R.color.mit_tintColor));
            viewHolder.stopIcon.setImageResource(R.drawable.stop_icon_red);
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
