package edu.mit.mitmobile2.shuttles.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.ShuttleAdapterCallback;
import edu.mit.mitmobile2.shuttles.model.MITShuttle;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;


public class MITShuttleAdapter extends CursorAdapter {

    private LayoutInflater listContainer;
    private Context context;

    public MITShuttleAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        listContainer = LayoutInflater.from(context);
        this.context = context;
    }

    public MITShuttleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        listContainer = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = listContainer.inflate(R.layout.shuttle_list_item, parent, false);
        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder viewHolder;
        viewHolder = (ViewHolder) view.getTag();

        MITShuttleRoute shuttleRoute = new MITShuttleRoute();
        shuttleRoute.buildFromCursor(cursor, MitMobileApplication.dbAdapter);

        if (shuttleRoute.isPredictable()) {
            viewHolder.shuttleRouteImageView.setImageResource(R.drawable.home_shuttles);
        } else {
            viewHolder.shuttleRouteImageView.setImageResource(R.drawable.menu_shuttles);
        }

        //TODO: Continue changing to user CursorAdapter
        /*viewHolder.shuttleRouteTextview.setText(shuttleRoute.getTitle());
        if (shuttleRoute.isPredictable()) {
            initialViewVisibility(viewHolder, View.VISIBLE);
            viewHolder.firstStopTextView.setText(shuttleRoute.getStops().get(0).getTitle());
            viewHolder.secondStopTextView.setText(shuttleRoute.getStops().get(1).getTitle());

            //Not going to be able to see the predictions until we make a second call

            if (mitShuttle.getFirstMinute() != null) {
                if (mitShuttle.getFirstMinute().equals("0m")) {
                    viewHolder.firstStopMinuteTextView.setText("now");
                    viewHolder.firstStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.mit_tintColor));
                } else {
                    viewHolder.firstStopMinuteTextView.setText(mitShuttle.getFirstMinute());
                    viewHolder.firstStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
                }
            } else {
                viewHolder.firstStopMinuteTextView.setText("––");
                viewHolder.firstStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.contents_text));
            }

            if (mitShuttle.getSecondMinute() != null) {
                if (mitShuttle.getSecondMinute().equals("0m")) {
                    viewHolder.secondStopMinuteTextView.setText("now");
                    viewHolder.secondStopMinuteTextView.setTextColor(context.getResources().getColor(R.color.mit_tintColor));
                } else {
                    viewHolder.secondStopMinuteTextView.setText(mitShuttle.getSecondMinute());
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
                ((ShuttleAdapterCallback) context).shuttleRouteClick(mitShuttle.getRouteID());
            }
        });*/

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

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
