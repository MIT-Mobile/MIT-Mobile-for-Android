package edu.mit.mitmobile2.people.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.mit.mitmobile2.DrawableUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.people.PeopleDirectoryManager.DirectoryDisplayProperty;
import edu.mit.mitmobile2.people.model.MITPeopleDirectoryPersonAdaptable;
import edu.mit.mitmobile2.people.model.MITPeopleDirectoryPersonAdaptableSurrogate;
import edu.mit.mitmobile2.people.model.MITPerson;


public class MITPeopleDirectoryPersonAdapter extends BaseAdapter {
    public static final String FORCE_SHORT_MODE = "FORCE_SHORT_MODE_201504201433";

    private List<MITPeopleDirectoryPersonAdaptable> people;

    private boolean forceShortMode;

    public MITPeopleDirectoryPersonAdapter() {
        this.people = (new ArrayList<MITPeopleDirectoryPersonAdaptable>(0));
    }

    public MITPeopleDirectoryPersonAdapter(ArrayList<? extends MITPeopleDirectoryPersonAdaptable> people) {
        this.people = (new ArrayList<MITPeopleDirectoryPersonAdaptable>(people.size()));
        this.people.addAll(people);
    }

    public void updateItems(List<? extends MITPeopleDirectoryPersonAdaptable> list) {
        this.people.clear();
        this.people.addAll(list);
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

        String title = null;
        String subtitle = null;
        int iconId = 0;

        Object item = getItem(position);

        if (item instanceof MITPerson) {
            MITPerson personInfo = (MITPerson) item;
            DirectoryDisplayProperty displayProp = DirectoryDisplayProperty.getPrimaryDisplayPropertyFor(personInfo);

            title = personInfo.getName();
            subtitle = personInfo.getTitle();

            if (!forceShortMode && !FORCE_SHORT_MODE.equals(subtitle) && displayProp != null) {
                useShortLayout = false;
                iconId = displayProp.getActionIcon().getIconId();
            }
        } else if (item instanceof MITPeopleDirectoryPersonAdaptableSurrogate) {
            MITPeopleDirectoryPersonAdaptableSurrogate surrog = (MITPeopleDirectoryPersonAdaptableSurrogate) item;
            title = surrog.getName();
            subtitle = surrog.getValue();

            DirectoryDisplayProperty displayProp = DirectoryDisplayProperty.byKeyAttribute(surrog.getAttributeType());

            if (!forceShortMode && !FORCE_SHORT_MODE.equals(subtitle) && displayProp != null) {
                useShortLayout = false;
                iconId = displayProp.getActionIcon().getIconId();
            }
        }

        holder.configureHeight(useShortLayout);

        holder.title.setText(title);

        if (!useShortLayout) {
            Context ctx = parent.getContext();
            holder.icon.setImageDrawable(DrawableUtils.applyTint(ctx, iconId, ctx.getResources().getColor(R.color.mit_red)));
            holder.subtitle.setText(subtitle);
        }

        return view;
    }

    public void setForceShortMode(boolean forceShortMode) {
        this.forceShortMode = forceShortMode;
    }

    public boolean isForceShortMode() {
        return forceShortMode;
    }

    protected static final class ViewHolder {
        private static final int VIEWHOLDER_TAG_KEY = 1504161536;

        public final LinearLayout rowContainer;
        public final TextView title;
        public final TextView subtitle;
        public final ImageView icon;

        public ViewHolder(@NonNull View coOpt) {
            this.rowContainer = (LinearLayout) coOpt.findViewById(R.id.row_container);
            this.title = (TextView) coOpt.findViewById(R.id.title);
            this.subtitle = (TextView) coOpt.findViewById(R.id.subtitle);
            this.icon = (ImageView) coOpt.findViewById(R.id.icon);

            this.bind(coOpt);
        }

        public void bind(@NonNull View view) {
            view.setTag(VIEWHOLDER_TAG_KEY, this);
        }

        private float getDimension(int id) {
            return this.rowContainer.getContext().getResources().getDimension(id);
        }

        public static ViewHolder from(@NonNull View view) {
            ViewHolder holder = (ViewHolder) view.getTag(VIEWHOLDER_TAG_KEY);

            if (holder == null) {
                holder = new ViewHolder(view);
            }

            return holder;
        }

        public void configureHeight(boolean useShortLayout) {
            this.rowContainer.setMinimumHeight((int) (useShortLayout ? getDimension(R.dimen.people_row_height_short) : getDimension(R.dimen.people_row_height_tall)));

            if (useShortLayout) {
                this.icon.setVisibility(View.GONE);
                this.subtitle.setVisibility(View.GONE);
            } else {
                this.icon.setVisibility(View.VISIBLE);
                this.subtitle.setVisibility(View.VISIBLE);
            }
        }
    }
}


