package edu.mit.mitmobile2.shared.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shared.model.MITSimpleTaggedActionItem;

/**
 * Created by grmartin on 4/20/15.
 */
public class MITSimpleTaggedActionAdapter extends BaseAdapter {
    private final ArrayList<MITSimpleTaggedActionItem> items;

    public MITSimpleTaggedActionAdapter(ArrayList<MITSimpleTaggedActionItem> items) {
        this.items = items;
    }

    public MITSimpleTaggedActionAdapter() {
        this.items = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.items.get(position).getTag();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : View.inflate(parent.getContext(), R.layout.row_shared_simple_tagged_action_item, null);
        ViewHolder holder = ViewHolder.from(view);

        MITSimpleTaggedActionItem item = (MITSimpleTaggedActionItem)this.getItem(position);

        holder.title.setText(item.getName());

        return view;
    }

    public void updateItems(List<MITSimpleTaggedActionItem> actionItems) {
        this.items.clear();
        this.notifyDataSetInvalidated();
        this.items.addAll(actionItems);
        this.notifyDataSetChanged();
    }

    protected static final class ViewHolder {
        private static final int VIEWHOLDER_TAG_KEY = 1504201607;

        public final LinearLayout rowContainer;
        public final TextView title;

        public ViewHolder(@NonNull View coOpt) {
            this.rowContainer = (LinearLayout) coOpt.findViewById(R.id.row_container);
            this.title = (TextView) coOpt.findViewById(R.id.title);

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
