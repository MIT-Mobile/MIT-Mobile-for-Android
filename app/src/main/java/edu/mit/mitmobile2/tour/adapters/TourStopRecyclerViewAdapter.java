package edu.mit.mitmobile2.tour.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.callbacks.TourSelfGuidedCallback;
import edu.mit.mitmobile2.tour.model.MITTourStop;
import edu.mit.mitmobile2.tour.utils.TourUtils;

public class TourStopRecyclerViewAdapter extends RecyclerView.Adapter<TourStopRecyclerViewAdapter.ViewHolder> {

    private List<MITTourStop> tourStops;
    private Context context;
    private LayoutInflater listContainer;
    private TourSelfGuidedCallback callback;
    private boolean isMainLoop;

    public TourStopRecyclerViewAdapter(Context context, List<MITTourStop> tourStops, TourSelfGuidedCallback callback, boolean isMainLoop) {
        this.tourStops = tourStops;
        this.context = context;
        this.callback = callback;
        this.isMainLoop = isMainLoop;
        listContainer = LayoutInflater.from(context);
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public ImageView stopImageView;
        public TextView stopTitleTextView;
        public LinearLayout recyclerViewLayout;

        public ViewHolder(View view) {
            super(view);
            stopTitleTextView = (TextView) view.findViewById(R.id.recycler_view_stop_title_text_view);
            stopImageView = (ImageView) view.findViewById(R.id.recycler_view_stop_image_view);
            recyclerViewLayout = (LinearLayout) view.findViewById(R.id.recycler_view_layout);
        }
    }

    @Override
    public int getItemCount() {
        if (isMainLoop) {
            return tourStops.size() * TourUtils.NUMBER_OF_TOUR_LOOP;
        } else {
            return tourStops.size();
        }
    }

    @Override
    public TourStopRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = listContainer.inflate(R.layout.tour_stop_recyclerview_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final MITTourStop tourStop = tourStops.get(position % tourStops.size());
        Picasso.with(context).load(tourStop.getThumbnailImage().getUrl()).fit().centerCrop().into(viewHolder.stopImageView);
        viewHolder.stopTitleTextView.setText((tourStop.getIndex() + 1) + ". " + tourStop.getTitle());

        viewHolder.recyclerViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tourStop.getType().equals(Constants.Tours.MAIN_LOOP)) {
                    callback.showMainLoopFragment(tourStop.getIndex());
                } else {
                    callback.showSideTripFragment(tourStop);
                }
            }
        });
    }
}
