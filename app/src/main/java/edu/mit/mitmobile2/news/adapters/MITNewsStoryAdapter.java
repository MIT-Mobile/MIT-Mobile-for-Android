package edu.mit.mitmobile2.news.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.DrawableUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.NewsFragmentCallback;
import edu.mit.mitmobile2.news.models.MITNewsCategory;
import edu.mit.mitmobile2.news.models.MITNewsStory;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MITNewsStoryAdapter extends MITNewsCategoryAdapter implements StickyListHeadersAdapter {

    private class HeaderViewHolder {
        ImageView indicatorIcon;
        TextView headerText;
    }

    private String[] headers;
    private String[] headerIds;
    private Context context;

    public MITNewsStoryAdapter(Context context, List<MITNewsStory> stories, NewsFragmentCallback callback) {
        super(context, stories, callback);
        this.context = context;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        HeaderViewHolder holder;

        if (view == null) {
            view = View.inflate(context, R.layout.news_header_view, null);
            holder = new HeaderViewHolder();

            holder.indicatorIcon = (ImageView) view.findViewById(R.id.header_indicator);
            holder.headerText = (TextView) view.findViewById(R.id.news_header_title);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        MITNewsStory story = (MITNewsStory) getItem(i);
        story.getCategory().getName();

        long headerId = getHeaderId(i);

        holder.headerText.setText(headers[((int) headerId)]);
        holder.indicatorIcon.setImageDrawable(DrawableUtils.applyTint(context, R.drawable.ic_right_arrow, context.getResources().getColor(R.color.arrow_ltgray)));

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        if (i < 5) {
            return 0;
        } else if (i < 10) {
            return 1;
        } else {
            return 2;
        }
    }

    public void setHeaders(List<MITNewsCategory> categories) {
        this.headers = new String[categories.size()];
        this.headerIds = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            this.headers[i] = categories.get(i).getName();
            this.headerIds[i] = categories.get(i).getId();
        }
    }

    public String getHeader(int i) {
        return headerIds[i];
    }

    public List<MITNewsStory> getStoriesByCategory(String categoryId) {
        List<MITNewsStory> storyList = new ArrayList<>();
        for (MITNewsStory story : stories) {
            if (story.getCategory().getId().equals(categoryId)) {
                storyList.add(story);
            }
        }

        return storyList;
    }
}