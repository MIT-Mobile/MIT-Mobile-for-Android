package edu.mit.mitmobile2.dining.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.model.MITDiningRetailVenue;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RetailAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context context;
    private List<MITDiningRetailVenue> retailVenues;

    public RetailAdapter(Context context) {
        this(context, null);
    }

    public RetailAdapter(Context context, ArrayList<MITDiningRetailVenue> retailVenues) {
        this.context = context;
        if (retailVenues != null) {
            this.retailVenues = retailVenues;
        } else {
            this.retailVenues = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return retailVenues.size();
    }

    @Override
    public MITDiningRetailVenue getItem(int position) {
        return retailVenues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.row_dining_house_venues, null);

            holder.venueImageView = (ImageView) convertView.findViewById(R.id.row_venues_iv_image);
            holder.venueTitleTextView = (TextView) convertView.findViewById(R.id.row_venues_tv_title);
            holder.venueTimeTextView = (TextView) convertView.findViewById(R.id.row_venues_tv_time);
            holder.venueStatusTextView = (TextView) convertView.findViewById(R.id.row_venues_tv_status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MITDiningRetailVenue venue = getItem(position);

        holder.venueTitleTextView.setText(venue.getName());
        // holder.venueTimeTextView.setText(venue.hoursToday(context));
        // if (venue.isOpenNow()) {
        // TODO: add logic here
        if (true) {
            holder.venueStatusTextView.setTextColor(context.getResources().getColor(R.color.status_green));
            holder.venueStatusTextView.setText(R.string.dining_venue_status_open);
        } else {
            holder.venueStatusTextView.setTextColor(context.getResources().getColor(R.color.status_red));
            holder.venueStatusTextView.setText(R.string.dining_venue_status_closed);
        }

        try {
            Picasso.with(context).load(venue.getIconURL()).placeholder(R.drawable.grey_rect).into(holder.venueImageView);
        } catch (NullPointerException e) {
            Picasso.with(context).load(R.drawable.grey_rect).placeholder(R.drawable.grey_rect).into(holder.venueImageView);
        }

        return convertView;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.row_calendar_academic_header, null);

            viewHolder.headerTextView = (TextView) view.findViewById(R.id.event_header_title);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String headerTitle = "";

        long headerId = getHeaderId(i);

        viewHolder.headerTextView.setText(headerTitle);

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return 0;
    }

    class ViewHolder {
        // header
        TextView headerTextView;

        // venues
        ImageView venueImageView;
        TextView venueTitleTextView;
        TextView venueTimeTextView;
        TextView venueStatusTextView;
    }

    public void setRetailVenues(List<MITDiningRetailVenue> retailVenues) {
        this.retailVenues = retailVenues;

        notifyDataSetChanged();
    }
}
