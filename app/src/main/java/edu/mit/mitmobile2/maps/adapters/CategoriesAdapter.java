package edu.mit.mitmobile2.maps.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.model.MITMapCategory;

/**
 * Created by serg on 5/27/15.
 */
public class CategoriesAdapter extends BaseAdapter {

    private ArrayList<MITMapCategory> categories;

    public CategoriesAdapter() {
        this.categories = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public MITMapCategory getItem(int position) {
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
            convertView = View.inflate(parent.getContext(), R.layout.row_map_category_category, null);

            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.map_category_tv_title);
            viewHolder.imageViewIcon = (ImageView) convertView.findViewById(R.id.map_category_iv_icon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MITMapCategory category = getItem(position);

        viewHolder.textViewTitle.setText(category.getName());

        return convertView;
    }

    class ViewHolder {
        TextView textViewTitle;
        ImageView imageViewIcon;
    }

    public void refreshCategories(ArrayList<MITMapCategory> categories) {
        this.categories.clear();
        if (categories != null) {
            this.categories.addAll(categories);
        }

        notifyDataSetChanged();
    }
}
