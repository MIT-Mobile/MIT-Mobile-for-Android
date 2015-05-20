package edu.mit.mitmobile2.libraries.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITItem;

public class AccountItemAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView title;
        TextView description;
        ImageView image;
        TextView status;
    }

    private Context context;
    private List<MITLibrariesMITItem> items;

    public AccountItemAdapter(Context context, List<MITLibrariesMITItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
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
            view = View.inflate(context, R.layout.mit_library_item_row, null);

            holder.title = (TextView) view.findViewById(R.id.item_title);
            holder.description = (TextView) view.findViewById(R.id.item_description);
            holder.status = (TextView) view.findViewById(R.id.item_status);
            holder.image = (ImageView) view.findViewById(R.id.item_image);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MITLibrariesMITItem item = (MITLibrariesMITItem) getItem(position);

        holder.title.setText(item.getTitle());
        holder.description.setText(item.getMaterial());
        holder.status.setText("A");
        Picasso.with(context).load(item.getCoverImages().get(0).getUrl()).fit().centerCrop().into(holder.image);

        return view;
    }
}
