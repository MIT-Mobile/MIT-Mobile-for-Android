package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.utils.ShuttleUtils;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;

/**
 * Created by philipcorriveau on 3/24/15.
 */
public class ShuttleStopPredictionsAdapter extends BaseAdapter {

    private Context context;
    private List<MITShuttlePrediction> predictions;

    private class ViewHolder {
        TextView predictionTextView;
    }

    public ShuttleStopPredictionsAdapter(Context context, List<MITShuttlePrediction> predictions) {
        this.context = context;
        this.predictions = predictions;
    }

    @Override
    public int getCount() {
        return predictions.size();
    }

    @Override
    public Object getItem(int position) {
        return predictions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.prediction_list_item, parent, false);

            holder = new ViewHolder();
            holder.predictionTextView = (TextView) view.findViewById(R.id.predicion_text_view);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.predictionTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
        holder.predictionTextView.setText(ShuttleUtils.formatPrediction(predictions.get(position)));
        if (holder.predictionTextView.getText().toString().equals(ShuttleUtils.NOW)) {
            holder.predictionTextView.setTextColor(context.getResources().getColor(R.color.mit_tintColor));
        }
        return view;
    }
}
