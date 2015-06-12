package edu.mit.mitmobile2.facilities.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.model.FacilitiesRoom;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RoomsAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private class ViewHolder {
        TextView roomView;
        TextView headerTextView;
    }

    private Context context;
    private List<FacilitiesRoom> rooms;

    public RoomsAdapter(Context context, List<FacilitiesRoom> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public FacilitiesRoom getItem(int position) {
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

        FacilitiesRoom room = getItem(position);

        holder.roomView.setText(room.getNumber());

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

        viewHolder.headerTextView.setText(getItem(i).getFloor());

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return Long.parseLong(getItem(i).getFloor());
    }
}
