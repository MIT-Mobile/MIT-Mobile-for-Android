package edu.mit.mitmobile2.facilities.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.model.FacilitiesBuilding;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RoomsAdapter extends BaseAdapter implements StickyListHeadersAdapter, Filterable, SectionIndexer {

    private class ViewHolder {
        TextView roomView;
        TextView headerTextView;
    }

    private Context context;
    private List<FacilitiesBuilding.Floor> floors;
    private List<String> allData;
    private List<String> filteredData;
    private List<String>[] sectionedRooms;
    private String[] sections;
    private HashMap<Integer, Integer> keySet;
    private boolean searchMode = false;

    private SearchItemFilter searchItemFilter = new SearchItemFilter();

    public RoomsAdapter(Context context, List<FacilitiesBuilding.Floor> floors) {
        this.context = context;
        this.floors = floors;

        allData = new ArrayList<>();
        filteredData = new ArrayList<>();

        if (floors.size() > 0) {
            sections = new String[floors.size()];
            sectionedRooms = new ArrayList[floors.size()];
        }
        keySet = new HashMap<>();

        for (FacilitiesBuilding.Floor f : floors) {
            int i = floors.indexOf(f);
            keySet.put(i, this.allData.size());

            allData.addAll(f.getRooms());
            filteredData.addAll(f.getRooms());
            sections[i] = String.valueOf(i);
            sectionedRooms[i] = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public String getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            holder = new ViewHolder();
            v = View.inflate(context, R.layout.room_list_row, null);
            holder.roomView = (TextView) v.findViewById(R.id.room_view);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        String room = filteredData.get(position);

        holder.roomView.setText(room);

        return v;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.row_calendar_academic_header, null);

            viewHolder.headerTextView = (TextView) view.findViewById(R.id.event_header_title);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String text = "";
        for (FacilitiesBuilding.Floor f : floors) {
            if (f.getRooms().contains(allData.get(i))) {
                text = String.valueOf(floors.indexOf(f));
            }
        }

        viewHolder.headerTextView.setText(context.getString(R.string.floor) + text);

        if (searchMode) {
            view.setAlpha(0);
        } else {
            view.setAlpha(1);
        }

        return view;

    }

    @Override
    public long getHeaderId(int i) {
        for (FacilitiesBuilding.Floor f : floors) {
            if (f.getRooms().contains(allData.get(i))) {
                return floors.indexOf(f);
            }
        }

        return 0;
    }

    @Override
    public Filter getFilter() {
        return searchItemFilter;
    }

    public void updateItems(List<FacilitiesBuilding.Floor> floors) {
        this.floors.clear();
        this.floors.addAll(floors);

        allData.clear();
        filteredData.clear();
        keySet.clear();

        if (sections == null || sectionedRooms == null) {
            sections = new String[floors.size()];
            sectionedRooms = new ArrayList[floors.size()];
        }

        for (FacilitiesBuilding.Floor f : this.floors) {
            int i = floors.indexOf(f);
            keySet.put(i, this.allData.size());

            if (sectionedRooms[i] == null) {
                sectionedRooms[i] = new ArrayList<>();
            }
            sectionedRooms[i].addAll(f.getRooms());

            if (sections[i] == null) {
                sections[i] = String.valueOf(i);
            }

            allData.addAll(f.getRooms());
            filteredData.addAll(f.getRooms());
        }

        notifyDataSetChanged();
    }

    public void setSearchMode(boolean search) {
        if (searchMode != search) {
            this.searchMode = search;
            notifyDataSetChanged();
        }
    }

    public boolean isSearchMode() {
        return searchMode;
    }

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

    private class SearchItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<String> unFilteredList = allData;

            int count = unFilteredList.size();
            final ArrayList<String> filteredList = new ArrayList<>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = unFilteredList.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    filteredList.add(filterableString);
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData.clear();
            filteredData = (ArrayList<String>) results.values;
            if (constraint.length() > 0) {
                filteredData.add(0, String.format("Use \"%s\"", constraint));
            }
            notifyDataSetChanged();
        }
    }
}
