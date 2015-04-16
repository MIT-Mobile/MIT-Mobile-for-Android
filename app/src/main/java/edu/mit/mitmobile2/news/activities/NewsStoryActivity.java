package edu.mit.mitmobile2.news.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.models.MITNewsStory;
import edu.mit.mitmobile2.news.utils.NewsUtils;
import timber.log.Timber;

public class NewsStoryActivity extends MITActivity {

    @InjectView(R.id.story_image_view)
    ImageView storyImageView;
    @InjectView(R.id.story_title_text_view)
    TextView storyTitleTextView;
    @InjectView(R.id.story_author_text_view)
    TextView storyAuthorTextView;
    @InjectView(R.id.story_dek_text_view)
    TextView storyDekTextView;
    @InjectView(R.id.story_published_time_text_view)
    TextView storyPublishedTimeTextView;
    @InjectView(R.id.story_body_web_view)
    WebView storyBodyWebView;

    private MITNewsStory story;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_story);
        ButterKnife.inject(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        story = getIntent().getParcelableExtra(Constants.News.STORY);

        Picasso.with(this).load(story.getCoverImage().getRepresentations().get(0).getUrl())
                .placeholder(R.drawable.grey_rect).into(storyImageView);

        storyTitleTextView.setText(story.getTitle());

        if (story.getAuthor() != null) {
            storyAuthorTextView.setVisibility(View.VISIBLE);
            storyAuthorTextView.setText(story.getAuthor());
        } else {
            storyAuthorTextView.setVisibility(View.GONE);
        }

        String formattedPublishedTime = NewsUtils.formatNewsPublishedTime(story.getPublishedAt());
        storyPublishedTimeTextView.setText(formattedPublishedTime);

        if (story.getDek() != null) {
            storyDekTextView.setVisibility(View.VISIBLE);
            storyDekTextView.setText(story.getDek());
        } else {
            storyDekTextView.setVisibility(View.GONE);
        }

        String template = readInHtmlTemplate();
        template = template.replace("__BODY__", story.getBodyHtml());
        template = template.replace("__WIDTH__", String.valueOf(displayMetrics.widthPixels));

        storyBodyWebView.loadData(template, "text/html", "UTF-8");
    }

    private String readInHtmlTemplate() {
        String template = "";
        try {
            InputStream is = getAssets().open("news_story_template.html");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            template = new String(buffer, "UTF-8");

        } catch (IOException e) {
            Timber.e(e, "HTML read Failed");
        }
        return template;
    }
}
