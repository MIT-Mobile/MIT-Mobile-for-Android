package edu.mit.mitmobile2.maps.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import edu.mit.mitmobile2.maps.model.MITMapSearch;

/**
 * Created by serg on 5/27/15.
 */
public class RecentsAdapter extends BaseAdapter {

    private ArrayList<MITMapSearch> recentSearchItems;

    public RecentsAdapter() {
        this.recentSearchItems = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.row_map_category_recent, null);

            viewHolder = new ViewHolder();

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder {
        TextView textViewTitle;
    }

    private void updateRecentPlaces(ArrayList<MITMapSearch> recentSearchItems) {
        this.recentSearchItems.clear();
        if (recentSearchItems != null) {
            this.recentSearchItems.addAll(recentSearchItems);
        }

        notifyDataSetChanged();
    }
}
