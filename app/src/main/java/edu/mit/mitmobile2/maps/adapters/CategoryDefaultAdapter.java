package edu.mit.mitmobile2.maps.adapters;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.model.MITMapCategory;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by serg on 5/28/15.
 */
public class CategoryDefaultAdapter extends BaseAdapter {

    private MITMapCategory category;
    private List<MITMapPlace> places;

    public CategoryDefaultAdapter(MITMapCategory category) {
        this.category = category;
        this.places = new ArrayList<>();
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
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.row_map_category_default_detail_place, null);

            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.map_category_default_detail_tv_title);
            viewHolder.textViewDescription = (TextView) convertView.findViewById(R.id.map_category_default_detail_tv_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MITMapPlace place = getItem(position);
        String title = place.getTitle(parent.getContext());
        String description = place.getSubtitle(parent.getContext());

        viewHolder.textViewTitle.setText(title);

        if (TextUtils.isEmpty(description)) {
            viewHolder.textViewDescription.setVisibility(View.GONE);
        } else {
            viewHolder.textViewDescription.setVisibility(View.VISIBLE);
            viewHolder.textViewDescription.setText(description);
        }

        return convertView;
    }

    /* Helpers */

    public void updatePlaces(List<MITMapPlace> places) {
        this.places.clear();
        if (places != null) {
            this.places.addAll(places);
        }

        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
    }
}
