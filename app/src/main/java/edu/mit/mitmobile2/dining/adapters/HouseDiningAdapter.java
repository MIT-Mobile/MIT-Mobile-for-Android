package edu.mit.mitmobile2.dining.adapters;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.callback.DiningHouseCallback;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;
import edu.mit.mitmobile2.dining.model.MITDiningLinks;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class HouseDiningAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private static final int ROW_TYPE_ANNOUNCEMENT = 0;
    private static final int ROW_TYPE_VENUE = 1;
    private static final int ROW_TYPE_RESOURCE = 2;
    private static final int ROW_TYPES_COUNT = 3;

    private Context context;
    private MITDiningDining mitDiningDining;
    private List<MITDiningDining> listAnnouncements;
    private List<MITDiningHouseVenue> listVenues;
    private List<MITDiningLinks> listResources;

    private DiningHouseCallback callback;

    public HouseDiningAdapter(Context context, ArrayList<MITDiningDining> listAnnouncements, ArrayList<MITDiningHouseVenue> listVenues, ArrayList<MITDiningLinks> listResources) {
        this.context = context;
        this.listAnnouncements = listAnnouncements;
        this.listVenues = listVenues;
        this.listResources = listResources;
    }

    public HouseDiningAdapter(Context context, MITDiningDining mitDiningDining, DiningHouseCallback callback) {
        this.context = context;
        this.mitDiningDining = mitDiningDining;
        this.listAnnouncements = new ArrayList<>();
        this.listVenues = new ArrayList<>();
        this.listResources = new ArrayList<>();
        this.callback = callback;

        refreshData();
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
        switch ((int) headerId) {
            case ROW_TYPE_ANNOUNCEMENT: {
//                if (listAnnouncements != null && listAnnouncements.size() > 0 && !TextUtils.isEmpty(listAnnouncements.get(0).getAnnouncementsHTML())) {
//                    headerTitle = listAnnouncements.get(0).getAnnouncementsHTML();
//                } else {
                headerTitle = context.getString(R.string.dining_house_section_announcements);
//                }
            }
            break;
            case ROW_TYPE_VENUE: {
                headerTitle = context.getString(R.string.dining_house_section_venues);
            }
            break;
            case ROW_TYPE_RESOURCE: {
                headerTitle = context.getString(R.string.dining_house_section_resources);
            }
            break;
        }

        viewHolder.headerTextView.setText(Html.fromHtml(headerTitle));

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return getItemViewType(i);
    }

    @Override
    public int getCount() {
        return listAnnouncements.size() + listVenues.size() + listResources.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < listAnnouncements.size()) {
            return listAnnouncements.get(position);
        } else if (position >= listAnnouncements.size() && position < listAnnouncements.size() + listVenues.size()) {
            return listVenues.get(position - listAnnouncements.size());
        } else {
            return listResources.get(position - listAnnouncements.size() - listVenues.size());
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        ViewHolder holder = new ViewHolder();

        switch (viewType) {
            case ROW_TYPE_ANNOUNCEMENT: {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_dining_house_announcement, null);

                    holder.announcementMessageTextView = (TextView) convertView.findViewById(R.id.row_announcement_tv_message);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                MITDiningDining dining = (MITDiningDining) getItem(position);

                holder.announcementMessageTextView.setText(Html.fromHtml(dining.getAnnouncementsHTML()));
            }
            break;
            case ROW_TYPE_VENUE: {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_dining_house_venues, null);

                    holder.venueDivider = convertView.findViewById(R.id.row_venues_divider);
                    holder.venueImageView = (ImageView) convertView.findViewById(R.id.row_venues_iv_image);
                    holder.venueTitleTextView = (TextView) convertView.findViewById(R.id.row_venues_tv_title);
                    holder.venueTimeTextView = (TextView) convertView.findViewById(R.id.row_venues_tv_time);
                    holder.venueStatusTextView = (TextView) convertView.findViewById(R.id.row_venues_tv_status);
                    holder.diningHouseRow = (LinearLayout) convertView.findViewById(R.id.dining_house_row);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                final MITDiningHouseVenue venue = (MITDiningHouseVenue) getItem(position);

                holder.venueTitleTextView.setText(venue.getName());
                holder.venueTimeTextView.setText(venue.hoursToday(context));
                if (venue.isOpenNow()) {
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

                holder.diningHouseRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.dinningHouseVenueCallback(venue);
                    }
                });

                boolean isLast = (listVenues.indexOf(venue) == listVenues.size() - 1);
                holder.venueDivider.setVisibility(isLast ? View.GONE : View.VISIBLE);
            }
            break;
            case ROW_TYPE_RESOURCE: {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_dining_house_resources, null);

                    holder.resourceTitleTextView = (TextView) convertView.findViewById(R.id.row_resources_tv_title);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                MITDiningLinks link = (MITDiningLinks) getItem(position);

                holder.resourceTitleTextView.setText(link.getName());
            }
            break;
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < listAnnouncements.size()) {
            return ROW_TYPE_ANNOUNCEMENT;
        } else if (position >= listAnnouncements.size() && position < listAnnouncements.size() + listVenues.size()) {
            return ROW_TYPE_VENUE;
        } else {
            return ROW_TYPE_RESOURCE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return ROW_TYPES_COUNT;
    }

    class ViewHolder {
        // header
        TextView headerTextView;

        // announcement
        TextView announcementMessageTextView;

        // venues
        View venueDivider;
        ImageView venueImageView;
        TextView venueTitleTextView;
        TextView venueTimeTextView;
        TextView venueStatusTextView;

        // resources
        TextView resourceTitleTextView;
        LinearLayout diningHouseRow;
    }

    public void setMitDiningDining(MITDiningDining mitDiningDining) {
        this.mitDiningDining = mitDiningDining;

        refreshData();
    }

    private void refreshData() {
        listAnnouncements.clear();
        listVenues.clear();
        listResources.clear();

        if (mitDiningDining != null) {
            listAnnouncements.add(mitDiningDining);

            // check lists are not null to prevent adding additional
            // null verifications in other methods
            if (mitDiningDining.getVenues() != null && mitDiningDining.getVenues().getHouse() != null) {
                listVenues = mitDiningDining.getVenues().getHouse();
            }
            if (mitDiningDining.getLinks() != null) {
                listResources = mitDiningDining.getLinks();
            }
        }

        notifyDataSetChanged();
    }
}
