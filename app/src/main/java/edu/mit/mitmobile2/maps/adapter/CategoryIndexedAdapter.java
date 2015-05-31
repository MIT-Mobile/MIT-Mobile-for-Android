package edu.mit.mitmobile2.maps.adapter;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
public class CategoryIndexedAdapter extends BaseAdapter implements SectionIndexer, StickyListHeadersAdapter {

    private MITMapCategory category;
    private List<MITMapPlace> places;
    private List<String> sections;

    public CategoryIndexedAdapter(MITMapCategory category) {
        this.category = category;
        this.places = new ArrayList<>();
        this.sections = new ArrayList<>();
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
            convertView = View.inflate(parent.getContext(), R.layout.row_map_category_detail_place, null);

            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.map_category_detail_tv_title);
            viewHolder.textViewDescription = (TextView) convertView.findViewById(R.id.map_category_detail_tv_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MITMapPlace place = getItem(position);

        viewHolder.textViewTitle.setText(place.getTitle(parent.getContext()));
        viewHolder.textViewDescription.setText(place.getSubtitle(parent.getContext()));

        return convertView;
    }

    /* SectionIndexer */

    @Override
    public Object[] getSections() {
        return sections.toArray(new String[sections.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        String section = sections.get(sectionIndex);
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getCategory().getSectionIndexTitle().equalsIgnoreCase(section)) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        MITMapPlace place = getItem(position);
        return sections.indexOf(place.getCategory().getSectionIndexTitle());
    }

    /* StickyListHeadersAdapter */

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.row_map_category_detail_header, null);

            viewHolder.headerTextView = (TextView) view.findViewById(R.id.header_title);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String headerTitle = sections.get(getSectionForPosition(i));

        viewHolder.headerTextView.setText(Html.fromHtml(headerTitle));

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return getSectionForPosition(i);
    }

    /* Helpers */

    public void updatePlaces(List<MITMapPlace> places) {
        this.places.clear();
        if (places != null) {
            this.places.addAll(places);
        }

        sections.clear();
        for (MITMapCategory subCategory : category.getCategories()) {
            sections.add(subCategory.getSectionIndexTitle());
        }

        notifyDataSetChanged();
    }

    class ViewHolder {
        // header
        TextView headerTextView;

        // place
        TextView textViewTitle;
        TextView textViewDescription;
    }
}
