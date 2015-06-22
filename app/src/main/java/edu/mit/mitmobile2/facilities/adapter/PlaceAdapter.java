package edu.mit.mitmobile2.facilities.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.facilities.callback.LocationCallback;
import edu.mit.mitmobile2.facilities.model.FacilitiesLocation;
import edu.mit.mitmobile2.maps.model.MITMapPlace;

public class PlaceAdapter extends BaseAdapter{
    private Context context;
    private List<MITMapPlace> places;
    private LocationCallback callback;

    public PlaceAdapter(Context context, List<MITMapPlace> places, LocationCallback callback) {
        this.context = context;
        this.places = places;
        this.callback = callback;
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

        if (place.getBuildingNumber() != null && place.getName() != null) {
            viewHolder.textViewTitle.setText(place.getBuildingNumber() + " - " + place.getName());
        } else if (place.getName() == null && (place.getBuildingNumber() != null)) {
            viewHolder.textViewTitle.setText(place.getBuildingNumber());
        } else if (place.getBuildingNumber() == null && place.getName()!= null) {
            viewHolder.textViewTitle.setText(place.getName());
        }

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
}
