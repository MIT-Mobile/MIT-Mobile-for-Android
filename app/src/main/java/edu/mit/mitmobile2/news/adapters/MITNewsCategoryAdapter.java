package edu.mit.mitmobile2.news.adapters;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.NewsFragmentCallback;
import edu.mit.mitmobile2.news.models.MITNewsStory;

public class MITNewsCategoryAdapter extends BaseAdapter {

    private class ViewHolder {
        ImageView storyImage;
        TextView storyTitle;
        TextView storyLede;

        ImageView mediaImage;
        TextView storySnippet;
    }

    protected Context context;
    protected List<MITNewsStory> stories;
    protected NewsFragmentCallback callback;

    public MITNewsCategoryAdapter(Context context, List<MITNewsStory> stories, NewsFragmentCallback callback) {
        this.callback = callback;
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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (stories.get(position).getCategory().getId().equals(Constants.News.IN_THE_MEDIA)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        final MITNewsStory story = (MITNewsStory) getItem(position);

        if (convertView == null) {
            holder = new ViewHolder();

            if (getItemViewType(position) == 0) {
                view = View.inflate(context, R.layout.news_list_row, null);

                holder.storyImage = (ImageView) view.findViewById(R.id.news_article_image);
                holder.storyTitle = (TextView) view.findViewById(R.id.news_article_title);
                holder.storyLede = (TextView) view.findViewById(R.id.news_article_lede);
            } else {
                view = View.inflate(context, R.layout.news_list_row_media, null);

                holder.mediaImage = (ImageView) view.findViewById(R.id.media_image);
                holder.storySnippet = (TextView) view.findViewById(R.id.news_snippet);
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (getItemViewType(position) == 0) {
            try {
                String smallCoverImageUrl = story.getSmallCoverImageUrl();
                Picasso.with(context).load(smallCoverImageUrl).placeholder(R.drawable.grey_rect).into(holder.storyImage);
            } catch (NullPointerException e) {
                Picasso.with(context).load(R.drawable.grey_rect).placeholder(R.drawable.grey_rect).into(holder.storyImage);
            }

            holder.storyTitle.setText(story.getTitle());
            holder.storyLede.setText(story.getDek());
        } else {
            Picasso.with(context).load(story.getOriginalCoverImageUrl()).placeholder(R.drawable.grey_rect).into(holder.mediaImage);
            holder.storySnippet.setText(Html.fromHtml(story.getDek()));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.itemClicked(story);
            }
        });

        return view;
    }

    public void updateItems(List<MITNewsStory> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }

    public void addItems(List<MITNewsStory> stories) {
        this.stories.addAll(stories);
        notifyDataSetChanged();
    }

    public List<MITNewsStory> getStories() {
        return stories;
    }
}
