package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.callbacks.AlertIconCallback;
import edu.mit.mitmobile2.shuttles.model.MITAlert;
import edu.mit.mitmobile2.shuttles.utils.ShuttleUtils;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;

public class ShuttleStopPredictionsAdapter extends BaseAdapter {

    private final static int ALERT_MIN_THRESHOLD = 360; //6 mins

    private Context context;
    private List<MITShuttlePrediction> predictions;
    private MITAlert alert;
    private AlertIconCallback callback;
    private int selectedPosition = -1;

    private class ViewHolder {
        TextView predictionTextView;
        ImageView alertIcon;
        TextView alarmSetTextView;
    }

    public ShuttleStopPredictionsAdapter(Context context, List<MITShuttlePrediction> predictions, MITAlert alert, AlertIconCallback callback) {
        this.context = context;
        this.predictions = predictions;
        this.alert = alert;
        this.callback = callback;

        getClosestPrediction();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.prediction_list_item, parent, false);

            holder = new ViewHolder();
            holder.predictionTextView = (TextView) view.findViewById(R.id.prediction_text_view);
            holder.alertIcon = (ImageView) view.findViewById(R.id.alert_icon);
            holder.alarmSetTextView = (TextView) view.findViewById(R.id.alarm_set);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MITShuttlePrediction prediction = predictions.get(position);

        holder.predictionTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
        holder.predictionTextView.setText(ShuttleUtils.formatPrediction(prediction));
        if (holder.predictionTextView.getText().toString().equals(ShuttleUtils.NOW)) {
            holder.predictionTextView.setTextColor(context.getResources().getColor(R.color.mit_tintColor));
        }

        Integer seconds = prediction.getSeconds();
        holder.alertIcon.setVisibility(seconds >= ALERT_MIN_THRESHOLD ? View.VISIBLE : View.INVISIBLE);
        holder.alertIcon.setImageResource(R.drawable.alert_icon_outline);

        holder.alarmSetTextView.setVisibility(View.INVISIBLE);

        if (position == selectedPosition) {
            setAlarmIconVisibility(holder.alertIcon.isSelected(), holder);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.alertIcon.getVisibility() == View.VISIBLE) {
                    boolean selected = holder.alertIcon.isSelected();

                    setAlarmIconVisibility(selected, holder);
                    callback.alertIconClicked(position, !selected);

                    if (!selected) {
                        selectedPosition = position;
                        notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }

    private void setAlarmIconVisibility(boolean selected, ViewHolder holder) {
        holder.alertIcon.setImageResource(selected ? R.drawable.alert_icon_outline : R.drawable.alert_icon);
        holder.alarmSetTextView.setVisibility(selected ? View.INVISIBLE : View.VISIBLE);
        holder.alertIcon.setSelected(!selected);
        if (selected) {
            selectedPosition = -1;
        }
    }

    public void updateItems(List<MITShuttlePrediction> predictions) {
        this.predictions = predictions;
        notifyDataSetChanged();
    }

    public void getClosestPrediction() {
        if (alert != null) {
            int[] diffs = new int[predictions.size()];
            Arrays.fill(diffs, Integer.MAX_VALUE);

            for (int i = 0; i < predictions.size(); i++) {
                if (alert.getVehicleId().equals(predictions.get(i).getVehicleId()) && predictions.get(i).getSeconds() >= 360) {
                    diffs[i] = Math.abs(alert.getTimestamp() - predictions.get(i).getTimestamp());
                }
            }

            int min = Integer.MAX_VALUE;
            int index = -1;

            for (int i = 0; i < diffs.length; i++) {
                if (diffs[i] < min) {
                    min = diffs[i];
                    index = i;
                }
            }

            selectedPosition = index;
        }
    }
}
