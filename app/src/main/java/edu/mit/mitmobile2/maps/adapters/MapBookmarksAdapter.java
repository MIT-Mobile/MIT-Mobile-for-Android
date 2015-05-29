package edu.mit.mitmobile2.maps.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.model.MITMapPlace;

public class MapBookmarksAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView title;
        TextView subtitle;
    }

    private Context context;
    private List<MITMapPlace> bookmarks;

    public MapBookmarksAdapter(Context context, List<MITMapPlace> bookmarks) {
        this.context = context;
        this.bookmarks = bookmarks;
    }

    @Override
    public int getCount() {
        return bookmarks.size();
    }

    @Override
    public MITMapPlace getItem(int position) {
        return bookmarks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.map_search_list_row, null);

            holder.title = (TextView) view.findViewById(R.id.map_search_result_title);
            holder.subtitle = (TextView) view.findViewById(R.id.map_search_result_subtitle);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MITMapPlace place = getItem(position);
        String buildingNumber = place.getBuildingNumber();

        if (!TextUtils.isEmpty(buildingNumber)) {
            holder.title.setText("Building " + buildingNumber);
            holder.subtitle.setVisibility(View.VISIBLE);
            holder.subtitle.setText(place.getName());
        } else {
            holder.title.setText(place.getName());
            holder.subtitle.setVisibility(View.GONE);
        }

        return view;
    }
}
