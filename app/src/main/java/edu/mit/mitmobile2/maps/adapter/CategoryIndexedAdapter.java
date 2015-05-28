package edu.mit.mitmobile2.maps.adapter;

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

/**
 * Created by serg on 5/28/15.
 */
public class CategoryIndexedAdapter extends BaseAdapter implements SectionIndexer {

    private MITMapCategory category;
    private List<MITMapPlace> places;
    private List<String> sections;
    private HashMap<String, Integer> mapIndex;


    public CategoryIndexedAdapter(MITMapCategory category) {
        this.category = category;
        this.places = new ArrayList<>();
        this.sections = new ArrayList<>();
        this.mapIndex = new HashMap<>();
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
        ViewHolder viewHolder = null;

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

    @Override
    public Object[] getSections() {
        return sections.toArray(new String[sections.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0; //mapIndex.get(getSections()[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public void updatePlaces(List<MITMapPlace> places) {
        this.places.clear();
        if (places != null) {
            this.places.addAll(places);
        }

        sections.clear();
        for (MITMapCategory subCategory : category.getCategories()) {
            sections.add(subCategory.getSectionIndexTitle());
        }

        for (MITMapPlace place : places) {
            // mapIndex.put(place.get)
        }

        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
    }
}
