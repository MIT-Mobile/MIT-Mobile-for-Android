package edu.mit.mitmobile2.facilities.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.model.FacilitiesBuilding;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class RoomsAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private class ViewHolder {
        TextView roomView;
        TextView headerTextView;
    }

    private Context context;
    private List<FacilitiesBuilding.Floor> floors;
    private List<String> rooms;

    public RoomsAdapter(Context context, List<FacilitiesBuilding.Floor> floors) {
        this.context = context;
        this.floors = floors;

        rooms = new ArrayList<>();

        for (FacilitiesBuilding.Floor f : floors) {
            rooms.addAll(f.getRooms());
        }
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public String getItem(int position) {
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

        String room = rooms.get(position);

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
            if (f.getRooms().contains(rooms.get(i))) {
                text = String.valueOf(floors.indexOf(f));
            }
        }

        viewHolder.headerTextView.setText(context.getString(R.string.floor) + text);

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        for (FacilitiesBuilding.Floor f : floors) {
            if (f.getRooms().contains(rooms.get(i))) {
                return floors.indexOf(f);
            }
        }

        return 0;
    }

    public void updateItems(List<FacilitiesBuilding.Floor> floors) {
        this.floors.clear();
        this.floors.addAll(floors);

        for (FacilitiesBuilding.Floor f : this.floors) {
            rooms.addAll(f.getRooms());
        }

        notifyDataSetChanged();
    }
}
