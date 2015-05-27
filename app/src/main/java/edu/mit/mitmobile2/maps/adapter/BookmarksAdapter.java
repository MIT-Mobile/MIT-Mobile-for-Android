package edu.mit.mitmobile2.maps.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.model.MITMapPlace;

/**
 * Created by serg on 5/27/15.
 */
public class BookmarksAdapter extends BaseAdapter {

    private ArrayList<MITMapPlace> bookmarkedPlaces;

    public BookmarksAdapter() {
        this.bookmarkedPlaces = new ArrayList<>();
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
            convertView = View.inflate(parent.getContext(), R.layout.row_map_category_bookmark, null);

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

    private void updateBookmarkedPlaces(ArrayList<MITMapPlace> bookmarkedPlaces) {
        this.bookmarkedPlaces.clear();
        if (bookmarkedPlaces != null) {
            this.bookmarkedPlaces.addAll(bookmarkedPlaces);
        }

        notifyDataSetChanged();
    }
}
