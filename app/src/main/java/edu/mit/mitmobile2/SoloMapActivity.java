package edu.mit.mitmobile2;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;
import timber.log.Timber;

public class SoloMapActivity extends MITActivity implements Animation.AnimationListener {

    private ListView mapItemsListview;
    private ArrayList mapItems;
    private MITMapView mapView;
    private LinearLayout routeInfoSegment;
    private View transparentView;
    private Button listButton;

    private boolean mapViewExpanded = false;

    private int originalPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_map);

        mapItemsListview = (ListView) findViewById(R.id.map_list_view);
        View header = View.inflate(this, R.layout.stop_list_header, null);
        mapItemsListview.addHeaderView(header);

        mapView = new MITMapView(this, getFragmentManager(), R.id.route_map);
        mapView.getMap().getUiSettings().setAllGesturesEnabled(false);

        routeInfoSegment = (LinearLayout) findViewById(R.id.route_info_segment);
        transparentView = findViewById(R.id.transparent_map_overlay);
        listButton = (Button) findViewById(R.id.list_button);

        mapItemsListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!mapViewExpanded) {
                    int[] location = new int[2];
                    routeInfoSegment.getLocationOnScreen(location);

                    int newPosition = location[1];

                    if (getMapView() != null && newPosition != 0) {
                        int translation;
                        if (originalPosition == -1) {
                            originalPosition = newPosition;
                        }

                        translation = (originalPosition - newPosition) / 2;
                        mapView.getMapFragment().getView().setTranslationY(-translation);
                    }
                }
            }
        });

        transparentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMap();
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listButton.setVisibility(View.INVISIBLE);
                toggleMap();
            }
        });
    }

    protected void setMapItems(ArrayList mapItems) {
        this.mapItems = mapItems;
    }

    protected void displayMapItems() {
        Timber.d(TAG, "displayMapItems()");
        ArrayAdapter<MapItem> arrayAdapter = this.getMapItemAdapter();
        mapItemsListview.setAdapter(arrayAdapter);

        if (mapView != null) {
            mapView.addMapItemList(this.mapItems);
        }
    }

    protected GoogleMap getMapView() {
        return mapView.getMap();
    }

    protected ArrayAdapter<MapItem> getMapItemAdapter() {
        return null;
    }

    protected void fillAdapter() {
    }

    private void toggleMap() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        View view = mapView.getMapFragment().getView();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();

        ResizeAnimation a = new ResizeAnimation(view);
        a.setDuration(250);
        a.setAnimationListener(this);

        if (!getMapViewStatus()) {
            mapViewExpanded = true;
            getMapItemAdapter().clear();
            mapItemsListview.setVerticalScrollBarEnabled(false);
            a.setParams(lp.height, displayMetrics.heightPixels);
            mapView.getMap().getUiSettings().setAllGesturesEnabled(true);

            listButton.setVisibility(View.VISIBLE);
            mapItemsListview.setVisibility(View.GONE);
        } else {
            mapViewExpanded = false;
            fillAdapter();
            routeInfoSegment.setVisibility(View.VISIBLE);
            mapItemsListview.setVisibility(View.VISIBLE);
            mapItemsListview.setVerticalScrollBarEnabled(true);
            a.setParams(lp.height, dpToPx(getResources(), 200));
        }
        view.startAnimation(a);
    }

    private boolean getMapViewStatus() {
        return mapViewExpanded;
    }

    public int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (!mapViewExpanded) {
            mapView.setToDefaultBounds();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
