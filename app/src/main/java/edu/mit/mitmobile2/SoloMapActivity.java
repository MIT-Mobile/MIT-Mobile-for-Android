package edu.mit.mitmobile2;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;
import timber.log.Timber;

public class SoloMapActivity extends MITActivity implements Animation.AnimationListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PREDICTIONS_PERIOD = 15000;
    private static final int PREDICTIONS_TIMER_OFFSET = 10000;

    public static final int NO_TRANSLATION = 0;
    public static final int ANIMATION_LENGTH = 500;

    private ListView mapItemsListview;
    private ArrayList mapItems;
    private MITMapView mapView;
    private LinearLayout routeInfoSegment;
    private View transparentView;
    private Button listButton;

    private boolean mapViewExpanded = false;
    private boolean animating = false;

    private int originalPosition = -1;

    private static Timer timer;

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
                if (!mapViewExpanded && !animating) {
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
                if (!animating) {
                    toggleMap();
                }
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!animating) {
                    listButton.setVisibility(View.INVISIBLE);
                    toggleMap();
                }
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
            mapView.adjustCameraToShowInHeader(false, 0);
        }
    }

    protected GoogleMap getMapView() {
        return mapView.getMap();
    }

    protected ArrayAdapter<MapItem> getMapItemAdapter() {
        return null;
    }

    private void toggleMap() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        View view = mapView.getMapFragment().getView();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();

        TranslateAnimation translateAnimation;

        int[] location = new int[2];
        routeInfoSegment.getLocationOnScreen(location);
        int currentRouteInfoTop = location[1];

        float bottomY = displayMetrics.heightPixels - currentRouteInfoTop;
        if (mapViewExpanded) {
            translateAnimation = new TranslateAnimation(NO_TRANSLATION, NO_TRANSLATION, bottomY, NO_TRANSLATION);
        } else {
            translateAnimation = new TranslateAnimation(NO_TRANSLATION, NO_TRANSLATION, NO_TRANSLATION, bottomY);
        }
        translateAnimation.setDuration(ANIMATION_LENGTH);
        translateAnimation.setAnimationListener(this);

        if (!mapViewExpanded) {
            mapViewExpanded = true;
            mapView.setToDefaultBounds(true, ANIMATION_LENGTH);
            listButton.setVisibility(View.VISIBLE);

            if (mapView.getMapFragment().getView().getTranslationY() != 0) {
                final TranslateAnimation mapTranslateAnimation = new TranslateAnimation(NO_TRANSLATION, NO_TRANSLATION, mapView.getMapFragment().getView().getTranslationY(), NO_TRANSLATION);
                mapTranslateAnimation.setDuration(ANIMATION_LENGTH);
                //The way translatation animations are setup, this is done to avoid flicker
                mapView.getMapFragment().getView().setTranslationY(0);
                mapView.getMapFragment().getView().startAnimation(mapTranslateAnimation);
            }
        } else {
            mapViewExpanded = false;
            mapView.adjustCameraToShowInHeader(true, ANIMATION_LENGTH);
            routeInfoSegment.setVisibility(View.VISIBLE);
            mapItemsListview.setVisibility(View.VISIBLE);
        }
        mapItemsListview.startAnimation(translateAnimation);
    }

    private void startTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updatePredictions();
            }
        }, PREDICTIONS_TIMER_OFFSET, PREDICTIONS_PERIOD);
    }

    protected void updatePredictions() {

    }

    @Override
    public void onAnimationStart(Animation animation) {
        animating = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (mapViewExpanded) {
            mapItemsListview.setSelection(0);
            mapItemsListview.setVisibility(View.GONE);
            mapView.getMap().getUiSettings().setAllGesturesEnabled(true);
        }
        animating = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onPause() {
        timer.cancel();
        timer.purge();
        timer = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        startTimerTask();
    }
}
