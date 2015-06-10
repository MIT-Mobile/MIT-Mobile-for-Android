package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RoomsAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private class ViewHolder {
        TextView roomView;
    }

    private Context context;
    private List<Object> rooms;

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
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

        holder.roomView.setText("");

        return v;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public long getHeaderId(int i) {
        return 0;
    }
}
