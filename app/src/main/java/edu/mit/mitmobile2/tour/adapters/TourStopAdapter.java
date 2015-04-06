package edu.mit.mitmobile2.tour.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.model.MITTourStop;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class TourStopAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context context;
    private List<MITTourStop> tourStops;
    private String[] headers;

    public TourStopAdapter(Context context, List<MITTourStop> tourStops) {
        this.context = context;
        this.tourStops = tourStops;
        this.headers = new String[]{context.getString(R.string.main_loop), context.getString(R.string.side_trips)};
    }

    private class ViewHolder {
        ImageView stopImage;
        TextView stopTitle;
    }

    private class HeaderViewHolder {
        ImageView dotIcon;
        TextView headerText;
    }

    @Override
    public int getCount() {
        return tourStops.size();
    }

    @Override
    public Object getItem(int position) {
        return tourStops.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            view = View.inflate(context, R.layout.tour_stop_list_row, null);
            holder = new ViewHolder();

            holder.stopImage = (ImageView) view.findViewById(R.id.stop_image);
            holder.stopTitle = (TextView) view.findViewById(R.id.stop_title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MITTourStop stop = tourStops.get(position);
        holder.stopTitle.setText((position + 1) + ". " + stop.getTitle());
        Picasso.with(context).load(stop.getBigImage().getUrl()).fit().centerCrop().into(holder.stopImage);

        return view;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        HeaderViewHolder holder;

        if (view == null) {
            view = View.inflate(context, R.layout.tour_stop_list_header, null);
            holder = new HeaderViewHolder();

            holder.dotIcon = (ImageView) view.findViewById(R.id.header_icon);
            holder.headerText = (TextView) view.findViewById(R.id.header_text);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        int headerId = (int) getHeaderId(i);
        String headerText = headers[headerId];

        holder.headerText.setText(headerText);
        holder.dotIcon.setImageResource(headerId == 0 ? R.drawable.red_circle : R.drawable.blue_circle);

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return (i < 10) ? 0 : 1;
    }

    public void updateItems(List<MITTourStop> items) {
        this.tourStops = items;
        notifyDataSetChanged();
    }
}
