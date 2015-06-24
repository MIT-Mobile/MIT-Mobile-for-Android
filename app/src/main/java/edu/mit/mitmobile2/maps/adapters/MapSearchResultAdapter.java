package edu.mit.mitmobile2.maps.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.callbacks.SearchResultListCallback;
import edu.mit.mitmobile2.maps.model.MITMapPlace;

public class MapSearchResultAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView title;
        TextView subtitle;
        ImageView info;
    }

    private Context context;
    private List<MITMapPlace> places;
    private SearchResultListCallback callback;

    public MapSearchResultAdapter(Context context, List<MITMapPlace> places, SearchResultListCallback callback) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.map_search_list_row, null);

            holder.title = (TextView) view.findViewById(R.id.map_search_result_title);
            holder.subtitle = (TextView) view.findViewById(R.id.map_search_result_subtitle);
            holder.info = (ImageView) view.findViewById(R.id.info_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        MITMapPlace place = getItem(position);
        String buildingNumber = place.getBuildingNumber();

        if (!TextUtils.isEmpty(buildingNumber)) {
            holder.title.setText((position + 1) + ". Building " + buildingNumber);
            holder.subtitle.setVisibility(View.VISIBLE);
            holder.subtitle.setText(place.getName());
        } else {
            holder.title.setText(place.getName());
            holder.subtitle.setVisibility(View.GONE);
        }

        holder.info.setVisibility(View.VISIBLE);

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.goToPlaceDetail(position);
            }
        });

        return view;
    }

}
