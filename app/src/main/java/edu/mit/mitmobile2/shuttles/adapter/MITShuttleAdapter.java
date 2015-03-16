package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.ShuttleAdapterCallback;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;


public class MITShuttleAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<MITShuttleRoute> routes = new ArrayList<>();
    private Context context;

    public MITShuttleAdapter(Context context, List<MITShuttleRoute> routes) {
        listContainer = LayoutInflater.from(context);
        this.routes = routes;
        this.context = context;
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
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = listContainer.inflate(R.layout.shuttle_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        if (routes.get(position).isPredictable()) {
            viewHolder.shuttleRouteImageView.setImageResource(R.drawable.home_shuttles);
        } else {
            viewHolder.shuttleRouteImageView.setImageResource(R.drawable.menu_shuttles);
        }

        viewHolder.shuttleRouteTextview.setText(routes.get(position).getTitle());
        if (routes.get(position).isPredictable()) {
            initialViewVisibility(viewHolder, View.VISIBLE);
            MITShuttleStopWrapper stop1 = routes.get(position).getStops().get(0);
            MITShuttleStopWrapper stop2 = routes.get(position).getStops().get(1);

            viewHolder.firstStopTextView.setText(stop1.getTitle());
            viewHolder.secondStopTextView.setText(stop2.getTitle());

            if (stop1.getPredictions() != null && stop1.getPredictions().size() > 0) {
                MITShuttlePrediction prediction = stop1.getPredictions().get(0);
                int timeInMins = prediction.getSeconds() / 60;
                if (timeInMins == 0) {
                    viewHolder.firstStopMinuteTextView.setText("now");
                    viewHolder.firstStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.mit_tintColor));
                } else {
                    viewHolder.firstStopMinuteTextView.setText(timeInMins + "m");
                    viewHolder.firstStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
                }
            } else {
                viewHolder.firstStopMinuteTextView.setText("––");
                viewHolder.firstStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
            }

            if (stop2.getPredictions() != null && stop2.getPredictions().size() > 0) {
                MITShuttlePrediction prediction = stop2.getPredictions().get(0);
                int timeInMins = prediction.getSeconds() / 60;
                if (timeInMins == 0) {
                    viewHolder.secondStopMinuteTextView.setText("now");
                    viewHolder.secondStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.mit_tintColor));
                } else {
                    viewHolder.secondStopMinuteTextView.setText(timeInMins + "m");
                    viewHolder.secondStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
                }
            } else {
                viewHolder.secondStopMinuteTextView.setText("––");
                viewHolder.secondStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
            }
        } else {
            initialViewVisibility(viewHolder, View.GONE);
        }

        viewHolder.shuttleRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShuttleAdapterCallback) context).shuttleRouteClick(routes.get(position).getId());
            }
        });
        viewHolder.shuttleFirstStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShuttleAdapterCallback)context).shuttleStopClick(mitShuttles.get(position).getFirstStopID());
            }
        });
        viewHolder.shuttleSecondStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShuttleAdapterCallback)context).shuttleStopClick(mitShuttles.get(position).getSecondStopID());
            }
        });

        return view;
    }

    public void initialViewVisibility(ViewHolder viewHolder, int view) {
        viewHolder.firstStopTextView.setVisibility(view);
        viewHolder.firstStopMinuteTextView.setVisibility(view);
        viewHolder.secondStopTextView.setVisibility(view);
        viewHolder.secondStopMinuteTextView.setVisibility(view);
        viewHolder.topView.setVisibility(view);
        viewHolder.bottomView.setVisibility(view);
    }

    static class ViewHolder {
        @InjectView(R.id.shuttle_route_imageview)
        ImageView shuttleRouteImageView;

        @InjectView(R.id.shuttle_route_textview)
        TextView shuttleRouteTextview;

        @InjectView(R.id.first_stop_textview)
        TextView firstStopTextView;

        @InjectView(R.id.first_minute_textview)
        TextView firstStopMinuteTextView;

        @InjectView(R.id.second_stop_textview)
        TextView secondStopTextView;

        @InjectView(R.id.second_minute_textview)
        TextView secondStopMinuteTextView;

        @InjectView(R.id.top_view)
        View topView;

        @InjectView(R.id.bottom_view)
        View bottomView;

        @InjectView(R.id.shuttle_route)
        RelativeLayout shuttleRoute;

        @InjectView(R.id.shuttle_route_first_stop)
        RelativeLayout shuttleFirstStop;

        @InjectView(R.id.shuttle_route_second_stop)
        RelativeLayout shuttleSecondStop;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public void updateListItems(List<MITShuttleRoute> routes) {
        this.routes = routes;
        notifyDataSetChanged();
    }

    public String getRouteStopTuples(String agency) {
        StringBuilder sb = new StringBuilder();
        for (MITShuttleRoute route : routes) {
            if (route.isPredictable() && route.getAgency().equals(agency)) {
                MITShuttleStopWrapper stop1 = route.getStops().get(0);
                MITShuttleStopWrapper stop2 = route.getStops().get(1);

                appendTuples(sb, route, stop1);
                appendTuples(sb, route, stop2);
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private void appendTuples(StringBuilder sb, MITShuttleRoute route, MITShuttleStopWrapper stop1) {
        sb.append(route.getId());
        sb.append(",");
        sb.append(stop1.getId());
        sb.append(";");
    }
}
