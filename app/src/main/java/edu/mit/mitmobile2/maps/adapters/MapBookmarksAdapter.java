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
import edu.mit.mitmobile2.maps.callbacks.BookmarkCallback;
import edu.mit.mitmobile2.maps.model.MITMapPlace;

public class MapBookmarksAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView title;
        TextView subtitle;
        ImageView deleteButton;
    }

    private Context context;
    private List<MITMapPlace> bookmarks;
    private boolean editMode = false;
    private BookmarkCallback callback;

    public MapBookmarksAdapter(Context context, List<MITMapPlace> bookmarks, BookmarkCallback callback) {
        this.context = context;
        this.bookmarks = bookmarks;
        this.callback = callback;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.map_search_list_row, null);

            holder.title = (TextView) view.findViewById(R.id.map_search_result_title);
            holder.subtitle = (TextView) view.findViewById(R.id.map_search_result_subtitle);
            holder.deleteButton = (ImageView) view.findViewById(R.id.delete_bookmark_button);

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

        if (editMode) {
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.removeBookmark(position);
            }
        });

        return view;
    }

    public void toggleEditMode() {
        this.editMode = !this.editMode;
        notifyDataSetChanged();
    }

    public void updateItems(List<MITMapPlace> bookmarks) {
        this.bookmarks.clear();
        this.bookmarks.addAll(bookmarks);
        notifyDataSetChanged();
    }
}
