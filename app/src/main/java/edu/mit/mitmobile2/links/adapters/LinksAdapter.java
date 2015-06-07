package edu.mit.mitmobile2.links.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.links.models.MITLink;
import edu.mit.mitmobile2.links.models.MITLinksCategory;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by serg on 6/4/15.
 */
public class LinksAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private ArrayList<MITLinksCategory> linkCategories;

    public LinksAdapter() {
        this.linkCategories = new ArrayList<>();
    }

    public void updateLinkCategories(ArrayList<MITLinksCategory> linkCategories) {
        this.linkCategories.clear();
        if (linkCategories != null) {
            this.linkCategories.addAll(linkCategories);
        }

        notifyDataSetChanged();
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.row_links_header, null);

            viewHolder.textViewHeader = (TextView) view.findViewById(R.id.link_header_title);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        MITLinksCategory category = linkCategories.get((int) getHeaderId(i));

        viewHolder.textViewHeader.setText(category.getTitle());

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        int count = 0;
        int categoryIndex = 0;
        for (MITLinksCategory category : linkCategories) {
            if (i >= count && i < count + category.getLinks().size()) {
                return categoryIndex;
            }

            count += category.getLinks().size();
            categoryIndex++;
        }

        return categoryIndex;
    }

    @Override
    public int getCount() {
        int totalCount = 0;
        for (MITLinksCategory category : linkCategories) {
            totalCount += category.getLinks().size();
        }
        return totalCount;
    }

    @Override
    public Object getItem(int position) {
        int count = 0;
        for (MITLinksCategory category : linkCategories) {
            if (position >= count && position < count + category.getLinks().size()) {
                return category.getLinks().get(position - count);
            }

            count += category.getLinks().size();
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.row_links_item, null);

            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.link_title);
            viewHolder.textViewUrl = (TextView) convertView.findViewById(R.id.link_url);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MITLink link = (MITLink) getItem(position);

        viewHolder.textViewTitle.setText(link.getName());
        viewHolder.textViewUrl.setText(link.getUrl());

        return convertView;
    }

    class ViewHolder {
        // header
        TextView textViewHeader;

        // link
        TextView textViewTitle;
        TextView textViewUrl;
    }
}
