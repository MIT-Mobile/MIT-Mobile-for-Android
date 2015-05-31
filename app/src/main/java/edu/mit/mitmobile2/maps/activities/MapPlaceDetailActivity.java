package edu.mit.mitmobile2.maps.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import edu.mit.mitmobile2.maps.model.MITMapPlaceContent;

public class MapPlaceDetailActivity extends AppCompatActivity {

    @InjectView(R.id.map_place_title)
    TextView placeTitle;

    @InjectView(R.id.map_place_location)
    TextView placeLocation;

    @InjectView(R.id.map_place_image)
    ImageView placeImage;

    @InjectView(R.id.map_place_viewangle)
    TextView placeViewAngle;

    @InjectView(R.id.map_place_contents)
    TextView placeContents;

    @InjectView(R.id.add_to_bookmarks_button)
    Button bookmarksButton;

    private MITMapPlace place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_place_detail);

        ButterKnife.inject(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.PLACES_KEY)) {
            place = savedInstanceState.getParcelable(Constants.PLACES_KEY);
        } else {
            place = getIntent().getParcelableExtra(Constants.PLACES_KEY);
        }

        placeTitle.setText(place.getName());
        placeLocation.setText(place.getStreet());
        Picasso.with(this).load(place.getBuildingImageUrl()).into(placeImage);

        setTitle("");

        buildContentString();

        placeViewAngle.setText(place.getViewangle());
        bookmarksButton.setText(DBAdapter.getInstance().placeIsBookmarked(place) ? getString(R.string.remove_from_bookmarks) : getString(R.string.add_to_bookmarks));
    }

    private void buildContentString() {
        if (place.getContents() != null && place.getContents().size() > 0) {
            StringBuilder builder = new StringBuilder();

            for (MITMapPlaceContent content : place.getContents()) {
                if (content.getCategory() == null || content.getCategory().size() == 0 || !content.getCategory().get(0).equals("room")) {
                    builder.append("&#8226; ");
                    builder.append(content.getName());
                    builder.append("<br/>");
                }
            }

            placeContents.setLineSpacing(0f, 1.5f);
            placeContents.setText(Html.fromHtml(builder.toString()));
        }
    }

    @OnClick(R.id.open_in_gmaps_button)
    void goToGoogleMaps() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("http://maps.google.com/maps?q=loc:%s,%s (%s)", place.getLatitude(), place.getLongitude(), place.getName())));
        startActivity(intent);
    }

    @OnClick(R.id.add_to_bookmarks_button)
    void toggleBookmarks() {
        if (!DBAdapter.getInstance().placeIsBookmarked(place)) {
            DBAdapter.getInstance().acquire(place);
            place.persistToDatabase();
            bookmarksButton.setText(getString(R.string.remove_from_bookmarks));
        } else {
            DBAdapter.getInstance().deletePlaceFromDb(place);
            bookmarksButton.setText(getString(R.string.add_to_bookmarks));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.PLACES_KEY, place);
    }

}
