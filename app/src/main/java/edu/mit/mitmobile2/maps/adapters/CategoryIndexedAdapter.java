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
public class CategoryIndexedAdapter extends BaseAdapter implements SectionIndexer, StickyListHeadersAdapter {

    private MITMapCategory category;
    private List<MITMapPlace> places;

    private ArrayList<MITMapPlace>[] sectionedPlaces;
    private String[] sections;
    private HashMap<Integer, Integer> keySet;

    public CategoryIndexedAdapter(MITMapCategory category) {
        this.category = category;
        this.places = new ArrayList<>();
        this.keySet = new HashMap<>();
        this.sections = new String[category.getCategories().size()];
        this.sectionedPlaces = new ArrayList[category.getCategories().size()];

        for (int i = 0; i < category.getCategories().size(); i++) {
            sections[i] = category.getCategories().get(i).getSectionIndexTitle();
            sectionedPlaces[i] = new ArrayList<>();
        }
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
            convertView = View.inflate(parent.getContext(), R.layout.row_map_category_indexed_detail_place, null);

            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.map_category_detail_tv_title);
            viewHolder.textViewDescription = (TextView) convertView.findViewById(R.id.map_category_detail_tv_description);

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

    /* SectionIndexer */

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return keySet.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    /* StickyListHeadersAdapter */

    @Override
    public View getHeaderView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.row_map_category_indexed_detail_header, null);

            viewHolder.headerTextView = (TextView) view.findViewById(R.id.header_title);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        MITMapCategory subCategory = category.getCategories().get((int) getHeaderId(position));

        viewHolder.headerTextView.setText(subCategory.getName());

        return view;
    }

    @Override
    public long getHeaderId(int position) {
        MITMapPlace place = getItem(position);
        for (int i = 0; i < sections.length; i++) {
            if (sections[i].equals(place.getMitCategory().getSectionIndexTitle())) {
                return i;
            }
        }

        return 0;
    }

    /* Helpers */

    public void updatePlaces(List<MITMapPlace> places, int sectionIndex) {
        this.places.clear();
        this.keySet.clear();

        this.sectionedPlaces[sectionIndex] = (ArrayList<MITMapPlace>) places;

        int currentPlacesCount = 0;
        for (int i = 0; i < sectionedPlaces.length; i++) {
            ArrayList<MITMapPlace> sectionPlaces = sectionedPlaces[i];
            this.places.addAll(sectionPlaces);

            keySet.put(i, currentPlacesCount);
            currentPlacesCount += sectionPlaces.size();
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
