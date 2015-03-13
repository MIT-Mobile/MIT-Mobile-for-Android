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
import edu.mit.mitmobile2.shuttles.model.MITShuttle;


public class MITShuttleAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<MITShuttle> mitShuttles = new ArrayList<>();
    private Context context;

    public MITShuttleAdapter (Context context, List<MITShuttle> mitShuttles) {
        listContainer = LayoutInflater.from(context);
        this.mitShuttles = mitShuttles;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mitShuttles.size();
    }

    @Override
    public Object getItem(int position) {
        return mitShuttles.get(position);
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

        if (mitShuttles.get(position).isPredicable()) {
            viewHolder.shuttleRouteImageView.setImageResource(R.drawable.home_shuttles);
        } else {
            viewHolder.shuttleRouteImageView.setImageResource(R.drawable.menu_shuttles);
        }

        viewHolder.shuttleRouteTextview.setText(mitShuttles.get(position).getRouteName());
        if (mitShuttles.get(position).isPredicable()) {
            initialViewVisibility(viewHolder, View.VISIBLE);
            viewHolder.firstStopTextView.setText(mitShuttles.get(position).getFirstStopName());
            viewHolder.secondStopTextView.setText(mitShuttles.get(position).getSecondStopName());

            if (mitShuttles.get(position).getFirstMinute() != null) {
                if (mitShuttles.get(position).getFirstMinute().equals("0m")) {
                    viewHolder.firstStopMinuteTextView.setText("now");
                    viewHolder.firstStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.mit_tintColor));
                } else {
                    viewHolder.firstStopMinuteTextView.setText(mitShuttles.get(position).getFirstMinute());
                    viewHolder.firstStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
                }
            } else {
                viewHolder.firstStopMinuteTextView.setText("––");
                viewHolder.firstStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
            }

            if (mitShuttles.get(position).getSecondMinute() != null) {
                if (mitShuttles.get(position).getSecondMinute().equals("0m")) {
                    viewHolder.secondStopMinuteTextView.setText("now");
                    viewHolder.secondStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.mit_tintColor));
                } else {
                    viewHolder.secondStopMinuteTextView.setText(mitShuttles.get(position).getSecondMinute());
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
                ((ShuttleAdapterCallback)context).shuttleRouteClick(mitShuttles.get(position).getRouteID());
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
}
