package edu.mit.mitmobile2.libraries.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import edu.mit.mitmobile2.R;

public class LibraryLinksAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView linkTitle;
    }

    private Context context;
    private String[] links;

    public LibraryLinksAdapter(Context context, String[] links) {
        this.context = context;
        this.links = links;
    }

    @Override
    public int getCount() {
        return links.length;
    }

    @Override
    public Object getItem(int position) {
        return links[position];
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

        holder.linkTitle.setText(context.getResources().getStringArray(R.array.link_titles)[position]);

        return view;
    }
}
