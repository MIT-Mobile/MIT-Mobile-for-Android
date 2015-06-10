package edu.mit.mitmobile2.facilities.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.model.FacilitiesCategory;

/**
 * Created by serg on 6/9/15.
 */
public class LocationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FacilitiesCategory> categories;

    public LocationAdapter(Context context) {
        this.context = context;
        this.categories = new ArrayList<>();
    }

    public void updateCategories(ArrayList<FacilitiesCategory> categories) {
        this.categories.clear();

        FacilitiesCategory nearbyCategory = new FacilitiesCategory();
        nearbyCategory.setName(context.getString(R.string.locations_nearby));
        this.categories.add(nearbyCategory);

        if (categories != null) {
            this.categories.addAll(categories);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public FacilitiesCategory getItem(int position) {
        return categories.get(position);
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

        FacilitiesCategory category = getItem(position);

        viewHolder.textViewTitle.setText(category.getName());

        return convertView;
    }

    class ViewHolder {
        TextView textViewTitle;
    }
}
