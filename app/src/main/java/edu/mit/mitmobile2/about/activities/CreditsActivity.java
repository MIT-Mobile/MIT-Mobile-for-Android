package edu.mit.mitmobile2.about.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import edu.mit.mitmobile2.R;

/**
 * Created by serg on 6/8/15.
 */
public class CreditsActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        webView = (WebView) findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        // TODO: add real names to "credits_current.html" on project release
        webView.loadUrl("file:///android_asset/credits_current.html");
    }
}
