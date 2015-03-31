package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.callbacks.IntersectingAdapterCallback;
import edu.mit.mitmobile2.shuttles.model.MITShuttleIntersectingRoute;

/**
 * Created by philipcorriveau on 3/24/15.
 */
public class ShuttleStopIntersectingAdapter extends BaseAdapter {

    private Context context;
    private List<MITShuttleIntersectingRoute> routes;
    private IntersectingAdapterCallback callback;

    private class ViewHolder {
        TextView routeTextView;
        ImageView routeImageView;
    }

    public ShuttleStopIntersectingAdapter(Context context, List<MITShuttleIntersectingRoute> routes, IntersectingAdapterCallback callback) {
        this.context = context;
        this.routes = routes;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public Object getItem(int position) {
        return routes.get(position);
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
            view = inflater.inflate(R.layout.intersecting_routes_list_item, parent, false);

            holder = new ViewHolder();
            holder.routeTextView = (TextView) view.findViewById(R.id.route_text_view);
            holder.routeImageView = (ImageView) view.findViewById(R.id.route_image_view);

            view.setTag(holder);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String routeId = routes.get(position).getId();
                    callback.intersectingRouteClick(routeId);
                }
            });
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.routeTextView.setText(routes.get(position).getTitle());
        if (routes.get(position).isPredictable()) {
            holder.routeImageView.setImageResource(R.drawable.shuttle_small_active);
        } else if (routes.get(position).isScheduled()) {
            holder.routeImageView.setImageResource(R.drawable.shuttle_small_unknown);
        } else {
            holder.routeImageView.setImageResource(R.drawable.shuttle_small_inactive);
        }

        return view;
    }
}
