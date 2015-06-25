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
import java.util.List;

import edu.mit.mitmobile2.facilities.callback.LocationCallback;
import edu.mit.mitmobile2.maps.model.MITMapPlace;

public class PlaceAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<MITMapPlace> allData;
    private List<MITMapPlace> filteredPlaces;
    private LocationCallback callback;
    private boolean searchMode = false;

    private SearchItemFilter searchItemFilter = new SearchItemFilter();

    public PlaceAdapter(Context context, List<MITMapPlace> places, LocationCallback callback) {
        this.context = context;

        allData = new ArrayList<>();
        filteredPlaces = new ArrayList<>();

        this.allData = places;
        this.filteredPlaces = places;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return filteredPlaces.size();
    }

    @Override
    public MITMapPlace getItem(int position) {
        return filteredPlaces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

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

        final MITMapPlace place = getItem(position);

        String outputString = "";
        if (place.getBuildingNumber() != null && place.getName() != null) {
            if (place.getBuildingNumber().equals(place.getName())) {
                outputString = place.getName();
            } else {
                outputString = place.getBuildingNumber() + " - " + place.getName();
            }
        } else if (place.getName() == null && (place.getBuildingNumber() != null)) {
            outputString = place.getBuildingNumber();
        } else if (place.getBuildingNumber() == null && place.getName()!= null) {
            outputString = place.getName() ;
        }

        viewHolder.textViewTitle.setText(outputString);

        viewHolder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.fetchPlace(place.getId(), viewHolder.textViewTitle.getText().toString());
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView textViewTitle;
    }

    public void setSearchMode(boolean search) {
        if (searchMode != search) {
            this.searchMode = search;
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        return searchItemFilter;
    }

    private class SearchItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<MITMapPlace> unFilteredList = allData;
            int count = unFilteredList.size();
            final ArrayList<MITMapPlace> filteredList = new ArrayList<>(count);

            if (filterString.isEmpty()) {
                filteredList.addAll(unFilteredList);
            } else {
                String filterableString;

                List<String> checkedStrings = new ArrayList<>();

                for (MITMapPlace place : unFilteredList) {
                    if (place.getBuildingNumber() != null && place.getName() != null) {
                        if (place.getBuildingNumber().equals(place.getName())) {
                            checkedStrings.add(place.getName());
                        } else {
                            checkedStrings.add(place.getBuildingNumber() + " - " + place.getName());
                        }
                    } else if (place.getName() == null && (place.getBuildingNumber() != null)) {
                        checkedStrings.add(place.getBuildingNumber());
                    } else if (place.getBuildingNumber() == null && place.getName()!= null) {
                        checkedStrings.add(place.getName());
                    }
                }

                for (int i = 0; i < count; i++) {
                    filterableString = checkedStrings.get(i);
                    if (filterableString.toLowerCase().contains(filterString.toLowerCase())) {
                        filteredList.add(unFilteredList.get(i));
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredPlaces = (ArrayList<MITMapPlace>) results.values;
            if (constraint.length() > 0) {
                MITMapPlace place = new MITMapPlace();
                place.setName(String.format("Use \"%s\"", constraint));
                filteredPlaces.add(0, place);
            }

            notifyDataSetChanged();
        }
    }
}
