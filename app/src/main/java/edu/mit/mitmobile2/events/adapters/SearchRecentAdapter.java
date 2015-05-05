package edu.mit.mitmobile2.events.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.model.MITCalendar;

/**
 * Created by serg on 5/5/15.
 */
public class SearchRecentAdapter extends BaseAdapter implements Filterable {

    private static final int ROW_TYPE_HEADER = 0;
    private static final int ROW_TYPE_FILTER = 1;
    private static final int ROW_TYPE_RECENT = 2;

    private static final int ROW_TYPES_COUNT = 3;

    public interface SearchEventsInteractionListener {
        void onRecentSearchSelected(String query);
        void onClearFilter(MITCalendar filterCategory);
    }

    private Context context;
    private List<String> recentSearches;
    private List<String> filteredData;

    private SearchItemFilter searchItemFilter;

    private MITCalendar filterCalendar;

    private SearchEventsInteractionListener interactionListener;

    public SearchRecentAdapter(Context context, List<String> recentSearches, MITCalendar filterCalendar, SearchEventsInteractionListener interactionListener) {
        this.context = context;
        this.recentSearches = recentSearches;
        this.filteredData = recentSearches;
        this.filterCalendar = filterCalendar;
        this.interactionListener = interactionListener;
    }

    @Override
    public int getViewTypeCount() {
        return ROW_TYPES_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (filterCalendar != null) {
            if (position == 0 || position == 2) return ROW_TYPE_HEADER;
            if (position == 1) return ROW_TYPE_FILTER;
            return ROW_TYPE_RECENT;
        } else {
            if (position == 0) return ROW_TYPE_HEADER;
            return ROW_TYPE_RECENT;
        }
    }

    @Override
    public int getCount() {
        if (filterCalendar != null) {
            return filteredData.size() + 3; // 2 headers + filter
        } else {
            return filteredData.size() + 1; // 1 header
        }
    }

    @Override
    public Object getItem(int position) {
        if (filterCalendar != null) {
            if (position == 0) return context.getString(R.string.event_filter_header);
            if (position == 1) return filterCalendar;
            if (position == 2) return context.getString(R.string.event_recent_header);
            return filteredData.get(position - 3);
        } else {
            if (position == 0) return context.getString(R.string.event_recent_header);
            return filteredData.get(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int rowType = getItemViewType(position);
        ViewHolder viewHolder = new ViewHolder();

        switch (rowType) {
            case ROW_TYPE_HEADER: {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_search_events_header, null);

                    viewHolder.headerTextView = (TextView) convertView;

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                String headerTitle = (String) getItem(position);

                viewHolder.headerTextView.setText(headerTitle);
            }
            break;
            case ROW_TYPE_FILTER: {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.recent_search_event_filter_item, null);

                    viewHolder.filterTextView = (TextView) convertView.findViewById(R.id.recent_search_event_filter_textview);
                    viewHolder.clearTextView = (TextView) convertView.findViewById(R.id.recent_search_event_clear_textview);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                final MITCalendar filterCalendar = (MITCalendar) getItem(position);

                viewHolder.filterTextView.setText(filterCalendar.getName());
                viewHolder.clearTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (interactionListener != null) {
                            interactionListener.onClearFilter(filterCalendar);
                        }
                    }
                });
            }
            break;
            case ROW_TYPE_RECENT: {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.recent_search_event_list_item, null);

                    viewHolder.recentSearchTextView = (TextView) convertView.findViewById(R.id.recent_search_event_textview);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                final String data = (String) getItem(position);

                viewHolder.recentSearchTextView.setText(data);
                viewHolder.recentSearchTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (interactionListener != null) {
                            interactionListener.onRecentSearchSelected(data);
                        }
                    }
                });
            }
            break;
        }



        return convertView;
    }

    private class ViewHolder {
        // header
        private TextView headerTextView;

        // filter
        private TextView filterTextView;
        private TextView clearTextView;

        // recent
        private TextView recentSearchTextView;
    }

    @Override
    public Filter getFilter() {
        if (searchItemFilter == null) {
            searchItemFilter = new SearchItemFilter();
        }
        return searchItemFilter;
    }

    private class SearchItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<String> unFilteredList = recentSearches;

            int count = unFilteredList.size();
            final ArrayList<String> filteredList = new ArrayList<>(count);

            String filterableString ;

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
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }

    public MITCalendar getFilterCalendar() {
        return filterCalendar;
    }

    public void setFilterCalendar(MITCalendar filterCalendar) {
        this.filterCalendar = filterCalendar;

        notifyDataSetChanged();
    }
}
