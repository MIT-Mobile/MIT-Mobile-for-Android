package edu.mit.mitmobile2.facilities.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.mit.mitmobile2.facilities.callback.LocationCallback;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import edu.mit.mitmobile2.maps.model.MITMapPlaceContent;

public class FacilitiesSearchAdapter extends BaseAdapter implements Filterable {

    private ArrayList<MITMapPlace> places;
    private String query;
    private Context context;
    private LocationCallback callback;

    public FacilitiesSearchAdapter(Context context, ArrayList<MITMapPlace> places, String query, LocationCallback callback) {
        this.context = context;
        this.places = places;
        this.callback = callback;
        this.query = query.toLowerCase();

        sortPlaces();

        if (query.length() > 0) {
            MITMapPlace place = new MITMapPlace();
            place.setName(String.format("Use \"%s\"", query));
            places.add(0, place);
        }
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public MITMapPlace getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_1, null);

            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.textViewTitle.setTextColor(Color.BLACK);
            viewHolder.textViewTitle.setSingleLine(true);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MITMapPlace mitMapPlace = getItem(position);

        String contentName = getContentName(mitMapPlace);
        String outputString = getOutputString(contentName, mitMapPlace);

        if (position != 0) {
            viewHolder.textViewTitle.setText(outputString);
        } else {
            viewHolder.textViewTitle.setText(mitMapPlace.getName());
        }

        viewHolder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    callback.fetchPlace(null, query, true);
                } else {
                    String transfer = "";
                    if (mitMapPlace.getBuildingNumber() != null && mitMapPlace.getName() != null) {
                        if (mitMapPlace.getBuildingNumber().equals(mitMapPlace.getName())) {
                            transfer = mitMapPlace.getName();
                        } else {
                            transfer = mitMapPlace.getBuildingNumber() + " - " + mitMapPlace.getName();
                        }
                    } else if (mitMapPlace.getName() == null && (mitMapPlace.getBuildingNumber() != null)) {
                        transfer = mitMapPlace.getBuildingNumber();
                    } else if (mitMapPlace.getBuildingNumber() == null && mitMapPlace.getName() != null) {
                        transfer = mitMapPlace.getName();
                    }
                    callback.fetchPlace(null, transfer, false);
                }
            }
        });

        return convertView;
    }

    private void sortPlaces() {
        for (MITMapPlace place : places) {
            if (place.getBuildingNumber() == null) {
                place.parseNumber(place.getName());
            } else {
                place.parseNumber(place.getBuildingNumber());
            }
        }

        Collections.sort(places, new Comparator<MITMapPlace>() {
            @Override
            public int compare(MITMapPlace lhs, MITMapPlace rhs) {
                return lhs.numberCompare(rhs);
            }
        });
    }

    public String getContentName(MITMapPlace mitMapPlace) {
        String contentName = "";

        if (mitMapPlace.getName().toLowerCase().contains(query)) {
            contentName = "";
        } else if (mitMapPlace.getBuildingNumber() != null && mitMapPlace.getBuildingNumber().toLowerCase().contains(query)) {
            contentName = "";
        } else {
            for (MITMapPlaceContent content : mitMapPlace.getContents()) {
                if (content.getName().toLowerCase().contains(query)) {
                    contentName = content.getName();
                    break;
                } else {
                    if (content.getAltname() != null && content.getAltname().size() > 0) {
                        for (String altName : content.getAltname()) {content.getName().toLowerCase().contains(query);
                            if (altName.toLowerCase().contains(query)) {
                                contentName = altName;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return contentName;
    }

    private String getOutputString(String contentName, MITMapPlace mitMapPlace) {
        String outputString;
        if (contentName.isEmpty()) {
            if (mitMapPlace.getBuildingNumber() != null) {
                outputString = mitMapPlace.getBuildingNumber() + " - " + mitMapPlace.getName();
            } else {
                outputString = mitMapPlace.getName();
            }
        } else {
            if (mitMapPlace.getBuildingNumber() != null) {
                outputString = mitMapPlace.getBuildingNumber() + " ( " + contentName + " ) ";
            } else {
                outputString = mitMapPlace.getName() + " - " + contentName;
            }
        }
        return outputString;
    }

    class ViewHolder {
        TextView textViewTitle;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
