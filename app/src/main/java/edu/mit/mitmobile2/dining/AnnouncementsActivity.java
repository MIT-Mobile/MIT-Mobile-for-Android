package edu.mit.mitmobile2.dining;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import edu.mit.mitmobile2.R;

/**
 * Created by philipcorriveau on 5/13/15.
 */
public class AnnouncementsActivity extends AppCompatActivity {

    public static final String ANNOUNCEMENTS_EXTRA = "ANNOUNCEMENTS_EXTRA";

    private WebView webView;

    private String announcementsHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        announcementsHtml = getIntent().getStringExtra(ANNOUNCEMENTS_EXTRA);
        webView = (WebView) findViewById(R.id.announcements_web_view);
        webView.loadData(announcementsHtml, "text/html;charset=utf-8", "utf-8");
    }
}
