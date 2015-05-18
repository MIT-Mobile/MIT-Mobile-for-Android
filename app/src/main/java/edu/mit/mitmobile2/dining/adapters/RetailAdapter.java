package edu.mit.mitmobile2.dining.adapters;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.model.MITDiningBuilding;
import edu.mit.mitmobile2.dining.model.MITDiningRetailVenue;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RetailAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context context;
    private List<MITDiningRetailVenue> retailVenues;
    private List<MITMapPlace> mitMapPlaces;

    private ArrayList<MITDiningRetailVenue> favoriteVenues;
    private LinkedHashMap<MITDiningBuilding, ArrayList<MITDiningRetailVenue>> retailArrayMap;

    public RetailAdapter(Context context) {
        this(context, null, null);
    }

    public RetailAdapter(Context context, ArrayList<MITDiningRetailVenue> retailVenues, ArrayList<MITMapPlace> mitMapPlaces) {
        this.context = context;
        this.favoriteVenues = new ArrayList<>();
        this.retailArrayMap = new LinkedHashMap<>();

        setRetailVenues(retailVenues, mitMapPlaces);
    }

    @Override
    public int getCount() {
        int totalCount = 0;
        for (ArrayList<MITDiningRetailVenue> venuesList : retailArrayMap.values()) {
            totalCount += venuesList.size();
        }

        return totalCount;
    }

    @Override
    public MITDiningRetailVenue getItem(int position) {
        int totalCount = 0;
        for (ArrayList<MITDiningRetailVenue> venuesList : retailArrayMap.values()) {
            if (position >= totalCount && position < totalCount + venuesList.size()) {
                return venuesList.get(position - totalCount);
            }
            totalCount += venuesList.size();
        }
        return null;
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
            holder.venueDivider = convertView.findViewById(R.id.row_venues_divider);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MITDiningRetailVenue venue = getItem(position);

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

        holder.venueDivider.setVisibility(isLastInGroup(position) ? View.GONE : View.VISIBLE);

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

        int buildingKeyPosition = (int) getHeaderId(i);
        int buildingKeysCount = retailArrayMap.keySet().size();
        MITDiningBuilding building = retailArrayMap.keySet().toArray(new MITDiningBuilding[buildingKeysCount])[buildingKeyPosition];

        viewHolder.headerTextView.setText(building.getName());

        return view;
    }

    @Override
    public long getHeaderId(int position) {
        int totalCount = 0;
        long headerId = 0;
        for (ArrayList<MITDiningRetailVenue> venuesList : retailArrayMap.values()) {
            if (position >= totalCount && position < totalCount + venuesList.size()) {
                return headerId;
            }
            totalCount += venuesList.size();
            headerId++;
        }
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
        View venueDivider;
    }

    public boolean isLastInGroup(int position) {
        int totalCount = 0;
        for (ArrayList<MITDiningRetailVenue> venuesList : retailArrayMap.values()) {
            if (position >= totalCount && position < totalCount + venuesList.size()) {
                return (position - totalCount) == (venuesList.size() - 1);
            }
            totalCount += venuesList.size();
        }
        return false;
    }

    public void setRetailVenues(List<MITDiningRetailVenue> retailVenues, List<MITMapPlace> mitMapPlaces) {
        if (retailVenues == null) {
            return;
        }

        this.retailVenues = retailVenues;
        this.mitMapPlaces = mitMapPlaces;

        // TODO: remove this later
        this.retailVenues.get(0).setFavorite(true); // tor test purposes

        ArrayMap<MITDiningBuilding, ArrayList<MITDiningRetailVenue>> tempArrayMap = new ArrayMap<>();
        MITDiningBuilding buildingOther = null;

        favoriteVenues.clear();
        for (MITDiningRetailVenue venue : retailVenues) {
            if (venue.isFavorite()) {
                favoriteVenues.add(venue);
            }

            MITDiningBuilding building = buildingForVenue(venue);

            if (building.getType() == MITDiningBuilding.TYPE_OTHER) {
                buildingOther = building;
            }

            if (tempArrayMap.get(building) == null) {
                tempArrayMap.put(building, new ArrayList<MITDiningRetailVenue>());
            }
            ArrayList<MITDiningRetailVenue> buildingVenues = tempArrayMap.get(building);
            buildingVenues.add(venue);
        }

        ArrayList<MITDiningBuilding> buildingKeysList = new ArrayList<>(tempArrayMap.keySet());
        if (buildingOther != null) {
            buildingKeysList.remove(buildingOther);
        }

        Collections.sort(buildingKeysList, new Comparator<MITDiningBuilding>() {
            @Override
            public int compare(MITDiningBuilding lhs, MITDiningBuilding rhs) {
                return lhs.getSortableName().compareTo(rhs.getSortableName());
            }
        });

        retailArrayMap = new LinkedHashMap<>();

        // add favorites
        boolean hasFavorites = favoriteVenues.size() > 0;
        if (hasFavorites) {
            MITDiningBuilding favoritesBuilding = new MITDiningBuilding(context.getString(R.string.retail_category_favorites), context.getString(R.string.retail_category_favorites));
            favoritesBuilding.setType(MITDiningBuilding.TYPE_FAVORITES);
            retailArrayMap.put(favoritesBuilding, favoriteVenues);
        }

        // add sorted grouped venues
        for (MITDiningBuilding building : buildingKeysList) {
            retailArrayMap.put(building, tempArrayMap.get(building));
            building.setName(getTitleForBuilding(building));
        }

        // add other venues
        if (buildingOther != null) {
            retailArrayMap.put(buildingOther, tempArrayMap.get(buildingOther));
        }

        notifyDataSetChanged();
    }

    private String getTitleForBuilding(MITDiningBuilding building) {
        if (mitMapPlaces != null) {
            for (MITMapPlace place : mitMapPlaces) {
                if (!TextUtils.isEmpty(place.getBldgnum()) && !TextUtils.isEmpty(building.getName())
                        && place.getBldgnum().equals(building.getName())) {
                    return String.format("%s - %s", building.getName(), place.getName().toUpperCase());
                }
            }
        }

        return building.getName();
    }

    private MITDiningBuilding buildingForVenue(MITDiningRetailVenue venue) {
        MITDiningBuilding building = new MITDiningBuilding(MITDiningBuilding.TYPE_NAMED);

        if (!TextUtils.isEmpty(venue.getLocation().getMitRoomNumber())) {
            String roomNumber = venue.getLocation().getMitRoomNumber();

            if (!TextUtils.isEmpty(roomNumber)) {
                String regexPattern = "(N|NW|NE|W|WW|E)?(\\d+)";
                Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(roomNumber);

                if (matcher.find()) {
                    building.setName(matcher.group(0));

                    String letters = matcher.group(1);
                    if (TextUtils.isEmpty(letters)) {
                        letters = "0";
                    }

                    String numbers = matcher.group(2);
                    if (TextUtils.isEmpty(numbers)) {
                        numbers = "0";
                    }

                    building.setSortableName(String.format("%s%5s", letters, numbers));
                }
            }
        }

        if (TextUtils.isEmpty(building.getName())) {
            building.setName(context.getString(R.string.retail_category_other));
            building.setSortableName(String.format("%5s%5s", "ZZZZZ", "99999"));
            building.setType(MITDiningBuilding.TYPE_OTHER);
        }

        return building;
    }
}
