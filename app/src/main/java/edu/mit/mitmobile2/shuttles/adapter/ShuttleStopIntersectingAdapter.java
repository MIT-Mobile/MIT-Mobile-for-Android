package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.activities.ShuttleRouteActivity;
import edu.mit.mitmobile2.shuttles.model.MITShuttleIntersectingRoute;

/**
 * Created by philipcorriveau on 3/24/15.
 */
public class ShuttleStopIntersectingAdapter extends BaseAdapter {

    private Context context;
    private List<MITShuttleIntersectingRoute> routes;

    private class ViewHolder {
        TextView routeTextView;
        ImageView routeImageView;
    }

    public ShuttleStopIntersectingAdapter(Context context, List<MITShuttleIntersectingRoute> routes) {
        this.context = context;
        this.routes = routes;
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routeId = routes.get(position).getId();
                Intent intent = new Intent(context, ShuttleRouteActivity.class);
                intent.putExtra(Constants.ROUTE_ID_KEY, routeId);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
