package edu.mit.mitmobile2.maps.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import edu.mit.mitmobile2.maps.model.MITMapCategory;

/**
 * Created by serg on 5/27/15.
 */
public class CategoriesAdapter extends BaseAdapter {

    private ArrayList<MITMapCategory> categories;

    public CategoriesAdapter(ArrayList<MITMapCategory> categories) {
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), 1, null);

            viewHolder = new ViewHolder();

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder {

    }
}
