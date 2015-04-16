package edu.mit.mitmobile2.news.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import android.widget.ImageView;

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

    @InjectView(R.id.story_web_view)
    WebView storyWebView;
    @InjectView(R.id.story_image_view)
    ImageView storyImageView;

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

        String template = readInHtmlTemplate();

        if (story.getTitle() != null) {
            template = template.replace("__TITLE__", story.getTitle());
        } else {
            template = template.replace("__TITLE__", "");
        }

        if (story.getAuthor() != null) {
            template = template.replace("__AUTHOR__", story.getAuthor());
        } else {
            template = template.replace("__AUTHOR__", "");
        }

        if (story.getPublishedAt() != null) {
            template = template.replace("__DATE__", NewsUtils.formatNewsPublishedTime(story.getPublishedAt()));
        } else {
            template = template.replace("__DATE__", "");
        }

        if (story.getDek() != null) {
            template = template.replace("__DEK__", story.getDek());
        } else {
            template = template.replace("__DEK__", "");
        }

        if (story.getBodyHtml() != null) {
            template = template.replace("__BODY__", story.getBodyHtml());
        } else {
            template = template.replace("__BODY__", "");
        }

        template = template.replace("__WIDTH__", String.valueOf(displayMetrics.widthPixels));

        storyWebView.loadData(template, "text/html", "UTF-8");
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
