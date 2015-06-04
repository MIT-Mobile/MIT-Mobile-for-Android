package edu.mit.mitmobile2.emergency.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.mit.mitmobile2.DrawableUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoContact;


public class MITEmergencyContactsAdapter extends BaseAdapter {

    protected List<MITEmergencyInfoContact> people;

    public MITEmergencyContactsAdapter() {
        this.people = (new ArrayList<MITEmergencyInfoContact>(0));
    }

    public MITEmergencyContactsAdapter(ArrayList<? extends MITEmergencyInfoContact> people) {
        this.people = (new ArrayList<MITEmergencyInfoContact>(people.size()));
        this.people.addAll(people);
    }

    public void updateItems(List<? extends MITEmergencyInfoContact> list) {
        this.people.clear();
        if (list != null) {
            this.people.addAll(list);
        }
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
        View view = convertView != null ? convertView : View.inflate(parent.getContext(), R.layout.row_people_directory_item, null);
        ViewHolder holder = ViewHolder.from(view);

        boolean useShortLayout = true;

        String subtitle = null;
        int iconId = 0;

        MITEmergencyInfoContact item = (MITEmergencyInfoContact) getItem(position);

        subtitle = ((subtitle = item.getDescription()) == null) ? item.getPhone() : subtitle;

        Context ctx = parent.getContext();
        holder.icon.setImageDrawable(DrawableUtils.applyTint(ctx, R.drawable.phone, ctx.getResources().getColor(R.color.mit_red)));
        holder.title.setText(item.getName());
        holder.subtitle.setText(subtitle);

        return view;
    }


    protected static final class ViewHolder {
        private static final int VIEWHOLDER_TAG_KEY = 1504221459;

        public final TextView title;
        public final TextView subtitle;
        public final ImageView icon;

        public ViewHolder(@NonNull View coOpt) {
            this.title = (TextView) coOpt.findViewById(R.id.title);
            this.subtitle = (TextView) coOpt.findViewById(R.id.subtitle);
            this.icon = (ImageView) coOpt.findViewById(R.id.icon);

            this.bind(coOpt);
        }

        public void bind(@NonNull View view) {
            view.setTag(VIEWHOLDER_TAG_KEY, this);
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


