package edu.mit.mitmobile2.facilities.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.mit.mitmobile2.R;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_1, null);

            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.textViewTitle.setTextColor(Color.BLACK);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MITMapPlace mitMapPlace = getItem(position);
        String name = getName(mitMapPlace);
        String outputString;

        if (name.isEmpty()) {
            if (mitMapPlace.getBuildingNumber() != null) {
                outputString = mitMapPlace.getBuildingNumber() + " - " + mitMapPlace.getName();
            } else {
                outputString = mitMapPlace.getName();
            }
        } else {
            if (mitMapPlace.getBuildingNumber() != null) {
                outputString = mitMapPlace.getBuildingNumber() + " ( " + name + " ) ";
            } else {
                outputString = mitMapPlace.getName() + " - " + name;
            }
        }

        int index = outputString.toLowerCase().indexOf(query.toLowerCase());
        Spannable spannable = new SpannableString(outputString);
        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.mit_red)), index, index + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.textViewTitle.setSingleLine(true);
        viewHolder.textViewTitle.setText(spannable);

        viewHolder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String transfer = "";
                if (mitMapPlace.getBuildingNumber() != null && mitMapPlace.getName() != null) {
                    if (mitMapPlace.getBuildingNumber().equals(mitMapPlace.getName())) {
                        transfer = mitMapPlace.getName();
                    } else {
                        transfer = mitMapPlace.getBuildingNumber() + " - " + mitMapPlace.getName();
                    }
                } else if (mitMapPlace.getName() == null && (mitMapPlace.getBuildingNumber() != null)) {
                    transfer = mitMapPlace.getBuildingNumber();
                } else if (mitMapPlace.getBuildingNumber() == null && mitMapPlace.getName()!= null) {
                    transfer = mitMapPlace.getName();
                }
                callback.fetchPlace(mitMapPlace.getId(), transfer);
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

    public String getName(MITMapPlace mitMapPlace) {
        String name = "";

        if (mitMapPlace.getName().toLowerCase().contains(query)) {
            name = mitMapPlace.getName();
        } else {
            for (MITMapPlaceContent content : mitMapPlace.getContents()) {
                if (content.getName().toLowerCase().contains(query)) {
                    name = content.getName();
                    break;
                } else {
                    if (content.getAltname() != null && content.getAltname().size() > 0) {
                        for (String altName : content.getAltname()) {content.getName().toLowerCase().contains(query);
                            if (altName.toLowerCase().contains(query)) {
                                name = altName;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return name;
    }

    class ViewHolder {
        TextView textViewTitle;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
