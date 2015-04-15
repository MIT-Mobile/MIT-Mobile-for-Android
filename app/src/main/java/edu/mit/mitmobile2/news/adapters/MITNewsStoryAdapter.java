package edu.mit.mitmobile2.news.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.models.MITNewsStory;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MITNewsStoryAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private class ViewHolder {
        ImageView storyImage;
        TextView storyTitle;
        TextView storyLede;
    }

    private class HeaderViewHolder {
        ImageView indicatorIcon;
        TextView headerText;
    }

    private Context context;
    private List<MITNewsStory> stories;
    private String[] headers = new String[]{"Header 1", "Header 2"};

    public MITNewsStoryAdapter(Context context, List<MITNewsStory> stories) {
        this.context = context;
        this.stories = stories;
    }

    @Override
    public int getCount() {
        return stories.size();
    }

    @Override
    public Object getItem(int position) {
        return stories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            view = View.inflate(context, R.layout.news_list_row, null);
            holder = new ViewHolder();

            holder.storyImage = (ImageView) view.findViewById(R.id.news_article_image);
            holder.storyTitle = (TextView) view.findViewById(R.id.news_article_title);
            holder.storyLede = (TextView) view.findViewById(R.id.news_article_lede);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MITNewsStory story = (MITNewsStory) getItem(position);

        Picasso.with(context).load(story.getSmallCoverImageUrl()).into(holder.storyImage);
        holder.storyTitle.setText(story.getTitle());
        holder.storyLede.setText(story.getDek());

        return view;
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
        holder.indicatorIcon.setImageResource(R.drawable.ic_right_arrow);

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        //TODO: Get correct header
        return i < 4 ? 0 : 1;
    }

    public void updateItems(List<MITNewsStory> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }
}
