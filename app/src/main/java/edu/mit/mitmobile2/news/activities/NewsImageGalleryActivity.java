package edu.mit.mitmobile2.news.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.adapters.MITNewsGalleryPagerAdapter;
import edu.mit.mitmobile2.news.models.MITNewsGalleryImage;

public class NewsImageGalleryActivity extends ActionBarActivity {

    @InjectView(R.id.image_gallery_view_pager)
    ViewPager viewPager;

    @InjectView(R.id.gallery_image_text)
    TextView imageText;

    @InjectView(R.id.credits)
    TextView credits;

    private String title;
    private String url;
    private List<MITNewsGalleryImage> images;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_image_gallery);

        getSupportActionBar().setTitle("");

        ButterKnife.inject(this);

        images = getIntent().getParcelableArrayListExtra(Constants.News.IMAGES_KEY);
        title = getIntent().getStringExtra(Constants.News.TITLE_KEY);
        url = getIntent().getStringExtra(Constants.News.URL_KEY);

        MITNewsGalleryPagerAdapter pagerAdapter = new MITNewsGalleryPagerAdapter(getFragmentManager(), images);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPosition = position;
                imageText.setText(images.get(position).getDescription());
                credits.setText(images.get(position).getCredits());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_image_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, images.get(currentPosition).getDescription() + "\n\n" + url);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_via)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
