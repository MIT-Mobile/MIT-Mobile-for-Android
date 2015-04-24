package edu.mit.mitmobile2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.news.NewsFragmentCallback;

public class MITSearchAdapter extends BaseAdapter implements Filterable {
    private List<String> recentSearches;
    private LayoutInflater listContainer;
    private NewsFragmentCallback callback;
    private SearchItemFilter searchItemFilter = new SearchItemFilter();
    private List<String> filteredData = null;

    public MITSearchAdapter(Context context, List<String> recentSearches, NewsFragmentCallback callback) {
        this.recentSearches = recentSearches;
        this.filteredData = recentSearches;
        this.callback = callback;
        listContainer = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = listContainer.inflate(R.layout.recent_search_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        viewHolder.recentSearchTextView.setText(filteredData.get(position));
        viewHolder.recentSearchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.itemSearch(filteredData.get(position));
            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        return searchItemFilter;
    }

    static class ViewHolder {
        @InjectView(R.id.recent_search_textview)
        TextView recentSearchTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
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
}

