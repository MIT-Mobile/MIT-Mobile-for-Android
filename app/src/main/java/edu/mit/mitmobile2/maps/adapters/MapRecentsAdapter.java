package edu.mit.mitmobile2.maps.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.mit.mitmobile2.R;

public class MapRecentsAdapter extends BaseAdapter {

    private class ViewHolder {
        // TODO: Add more to layout
        TextView recentName;
    }

    private Context context;
    private List<String> recents;

    public MapRecentsAdapter(Context context, Collection<String> recents) {
        this.context = context;
        this.recents = new ArrayList<>();
        this.recents.addAll(recents);
    }

    @Override
    public int getCount() {
        return recents.size();
    }

    @Override
    public String getItem(int position) {
        return recents.get(position);
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
            view = View.inflate(context, R.layout.recents_list_row, null);

            holder.recentName = (TextView) view.findViewById(R.id.recent_name);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String recent = getItem(position);
        holder.recentName.setText(recent);

        return view;
    }

    public void updateItems(List<String> recents) {
        this.recents.clear();
        this.recents.addAll(recents);
        notifyDataSetChanged();
    }
}
