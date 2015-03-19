package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;

public class ShuttleStopAdapter extends ArrayAdapter<String> {

    private final static int STOP_PREDICTION_VIEW = 0;
    private final static int INTERSECTING_ROUTES_VIEW_HEADER = 1;
    private final static int INTERSECTING_ROUTES_VIEW = 2;
    private final static int INTERSECTING_ROUTES_VIEW_FOOTER = 3;

    private ArrayList<String> stop = new ArrayList<>();
    private LayoutInflater listContainer;
    private int prediciontsSize;

    public ShuttleStopAdapter(Context context, ArrayList<String> stop, int prediciontsSize) {
        super(context, 0, stop);
        listContainer = LayoutInflater.from(context);
        this.prediciontsSize = prediciontsSize;
        this.stop = stop;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < prediciontsSize) {
            return STOP_PREDICTION_VIEW;
        } else if (position == prediciontsSize) {
            return INTERSECTING_ROUTES_VIEW_HEADER;
        } else if (position < stop.size() - 1 && position > prediciontsSize) {
            return INTERSECTING_ROUTES_VIEW;
        } else {
            return INTERSECTING_ROUTES_VIEW_FOOTER;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (getItemViewType(position) == STOP_PREDICTION_VIEW) {
            TopViewHolder viewHolder;
            view = listContainer.inflate(R.layout.prediction_list_item, parent, false);
            viewHolder = new TopViewHolder(view);
            view.setTag(viewHolder);

            viewHolder.predictionTextView.setText(stop.get(position));

            return view;
        } else if (getItemViewType(position) == INTERSECTING_ROUTES_VIEW_HEADER) {
            view = listContainer.inflate(R.layout.intersecting_routes_list_header, parent, false);

            return view;
        } else if (getItemViewType(position) == INTERSECTING_ROUTES_VIEW) {
            BottomViewHolder viewHolder;
            view = listContainer.inflate(R.layout.intersecting_routes_list_item, parent, false);
            viewHolder = new BottomViewHolder(view);
            view.setTag(viewHolder);

            viewHolder.routeTextView.setText(stop.get(position));

            return view;
        } else {
            view = listContainer.inflate(R.layout.intersecting_routes_list_footer, parent, false);

            return view;
        }
    }

    static class TopViewHolder {
        @InjectView(R.id.predicion_textview)
        TextView predictionTextView;

        TopViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    static class BottomViewHolder {
        @InjectView(R.id.route_textview)
        TextView routeTextView;

        @InjectView(R.id.route_imageview)
        ImageView routeImageView;

        BottomViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
