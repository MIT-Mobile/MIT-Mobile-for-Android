package edu.mit.mitmobile2.libraries.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesLink;

public class LibraryLinksAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView linkTitle;
    }

    private Context context;
    private List<MITLibrariesLink> links;

    public LibraryLinksAdapter(Context context, List<MITLibrariesLink> links) {
        this.context = context;
        this.links = links;
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public MITLibrariesLink getItem(int position) {
        return links.get(position);
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
            view = View.inflate(context, R.layout.library_link_row, null);

            holder.linkTitle = (TextView) view.findViewById(R.id.link_title);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MITLibrariesLink link = getItem(position);

        holder.linkTitle.setText(link.getTitle());

        return view;
    }
}
