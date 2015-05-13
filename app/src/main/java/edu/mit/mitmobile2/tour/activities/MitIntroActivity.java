package edu.mit.mitmobile2.tour.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;

public class MitIntroActivity extends AppCompatActivity {

    @InjectView(R.id.mit_introduction_web_view)
    WebView mitIntroductionWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_to_mit);

        ButterKnife.inject(this);
        mitIntroductionWebView.loadUrl("file:///android_asset/intro_to_mit.html");
    }
}
