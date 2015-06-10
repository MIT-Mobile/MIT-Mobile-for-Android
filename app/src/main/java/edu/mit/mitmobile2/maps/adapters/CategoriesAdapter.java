package edu.mit.mitmobile2.maps.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.model.MITMapCategory;

public class CategoriesAdapter extends BaseAdapter {

    private ArrayList<MITMapCategory> categories;
    private Context context;

    public CategoriesAdapter(Context context) {
        this.categories = new ArrayList<>();
        this.context = context;
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
        ViewHolder viewHolder;

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

        String name = getResourceName(category);

        viewHolder.imageViewIcon.setImageResource(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));

        return convertView;
    }

    @NonNull
    private String getResourceName(MITMapCategory category) {
        String name = category.getName().toLowerCase();
        StringBuilder sb = new StringBuilder();

        String[] split = name.split(" ");
        sb.append("ic_");

        int count = 0;
        for (String s : split) {
            sb.append(s);
            if (count < split.length - 1) {
                sb.append("_");
            }
            count++;
        }

        return sb.toString();
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
