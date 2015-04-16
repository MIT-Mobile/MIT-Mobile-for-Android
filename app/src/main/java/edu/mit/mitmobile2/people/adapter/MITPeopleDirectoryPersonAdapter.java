package edu.mit.mitmobile2.people.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.people.model.MITPerson;

public class MITPeopleDirectoryPersonAdapter extends BaseAdapter {
    private List<MITPerson> people;

    public MITPeopleDirectoryPersonAdapter() {
        this(new ArrayList<MITPerson>());
    }

    public MITPeopleDirectoryPersonAdapter(ArrayList<MITPerson> people) {
        this.people = people;
    }

    public void updateItems(List<MITPerson> list) {
        this.people = list;
        notifyDataSetChanged();
    }

    /* BaseAdapter */
    @Override
    public int getCount() {
        return people.size();
    }

    @Override
    public Object getItem(int position) {
        return people.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : View.inflate(parent.getContext(), R.layout.people_list_row, null);
        ViewHolder holder = ViewHolder.from(view);

        MITPerson personInfo = (MITPerson) getItem(position);

        holder.name.setText(personInfo.getName());
        holder.title.setText(personInfo.getTitle());

        return view;
    }

    private static final class ViewHolder {
        private static final int VIEWHOLDER_TAG_KEY = 1504161536;

        public TextView title;
        public TextView name;

        public ViewHolder(@NonNull View coOpt) {
            this.name = (TextView) coOpt.findViewById(R.id.people_name);
            this.title = (TextView) coOpt.findViewById(R.id.people_title);

            this.bind(coOpt);
        }

        public void bind(@NonNull View view) {
            view.setTag(VIEWHOLDER_TAG_KEY, view);
        }

        public static ViewHolder from(@NonNull View view) {
            ViewHolder holder = (ViewHolder) view.getTag(VIEWHOLDER_TAG_KEY);

            if (holder == null) {
                holder = new ViewHolder(view);
            }

            return holder;
        }
    }
}


