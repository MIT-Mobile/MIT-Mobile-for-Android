package edu.mit.mitmobile2.tour.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.model.MITTourStop;

public class NearHereAdapter extends RecyclerView.Adapter<NearHereAdapter.ViewHolder> {
    private List<MITTourStop> tourStops;
    private LayoutInflater listContainer;
    private Context context;

    public NearHereAdapter(Context context, List<MITTourStop> tourStops) {
        this.tourStops = tourStops;
        this.context = context;
        listContainer = LayoutInflater.from(context);
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public ImageView stopImageView;
        public TextView stopTitleTextView;

        public ViewHolder(View view) {
            super(view);
            stopTitleTextView = (TextView) view.findViewById(R.id.recycler_view_stop_title_text_view);
            stopImageView = (ImageView) view.findViewById(R.id.recycler_view_stop_image_view);
        }
    }

    @Override
    public int getItemCount() {
        return tourStops.size();
    }

    @Override
    public NearHereAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = listContainer.inflate(R.layout.main_loop_list_row, parent, false);
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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MITTourStop tourStop = tourStops.get(position);
        Picasso.with(context).load(tourStop.getThumbnailImage().getUrl()).fit().centerCrop().into(viewHolder.stopImageView);
        viewHolder.stopTitleTextView.setText((tourStop.getIndex() + 1) + ". " + tourStop.getTitle());
    }
}
