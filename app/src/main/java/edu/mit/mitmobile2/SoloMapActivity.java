package edu.mit.mitmobile2;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

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

    private ListView mapItemsListView;
    private ArrayList mapItems;
    private MITMapView mapView;

    private LinearLayout mapItemsListViewWithFooter;
    private View transparentView;
    private FrameLayout predictionFragment;

    private boolean hasHeader;
    private RelativeLayout routeInfoSegment;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected boolean mapViewExpanded = false;
    private boolean animating = false;

    private int originalPosition = -1;

    private static Timer timer;

    private FloatingActionButton listButton;
    private FloatingActionButton myLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_map);

        predictionFragment = (FrameLayout) findViewById(R.id.prediction_fragment);

        mapItemsListView = (ListView) findViewById(R.id.map_list_view);
        mapItemsListViewWithFooter = (LinearLayout) findViewById(R.id.map_list_view_with_footer);
        listButton = (FloatingActionButton) findViewById(R.id.list_button);
        myLocationButton = (FloatingActionButton) findViewById(R.id.my_location_button);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.shuttle_route_refresh_layout);

        listButton.setSize(FloatingActionButton.SIZE_NORMAL);
        listButton.setColorNormalResId(R.color.mit_red);
        listButton.setColorPressedResId(R.color.mit_red_dark);
        listButton.setIcon(R.drawable.ic_list);
        listButton.setStrokeVisible(false);

        myLocationButton.setSize(FloatingActionButton.SIZE_NORMAL);
        myLocationButton.setColorNormalResId(R.color.white);
        myLocationButton.setColorPressedResId(R.color.medium_grey);
        myLocationButton.setIcon(R.drawable.ic_my_location);
        myLocationButton.setStrokeVisible(false);

        swipeRefreshLayout.setEnabled(false);

        if (getIntent().getExtras() != null) {
            predictionFragment.setVisibility(View.GONE);
        } else {
            mapItemsListView.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapView = new MITMapView(this, getFragmentManager(), R.id.route_map);
        mapView.getMap().getUiSettings().setAllGesturesEnabled(false);

        mapItemsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!mapViewExpanded && !animating) {
                    int newPosition = calculateScrollOffset();

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

        mapItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemClicked(position);
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListView();
            }
        });

        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLocation = getMapView().getMyLocation();
                getMapView().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 14f), 400, null);
            }
        });
    }

    protected void showListView() {
        if (!animating) {
            listButton.setVisibility(View.INVISIBLE);
            myLocationButton.setVisibility(View.INVISIBLE);
            toggleMap();
        }
    }

    protected void listItemClicked(int position) {

    }

    protected void updateMapItems(ArrayList mapItems) {
        if (mapItems.size() == 0 || ((MapItem) mapItems.get(0)).isDynamic()) {
            mapView.clearDynamic();
        }
        mapView.addMapItemList(mapItems, false);
    }

    protected void displayMapItems() {
        Timber.d(TAG, "displayMapItems()");
        ArrayAdapter<MapItem> arrayAdapter = this.getMapItemAdapter();
        mapItemsListView.setAdapter(arrayAdapter);
    }

    protected GoogleMap getMapView() {
        return mapView.getMap();
    }

    protected ArrayAdapter<MapItem> getMapItemAdapter() {
        return null;
    }

    protected void addHeaderView(View headerView) {
        hasHeader = true;
        mapItemsListView.addHeaderView(headerView);
        routeInfoSegment = (RelativeLayout) headerView.findViewById(R.id.route_info_segment);
        transparentView = headerView.findViewById(R.id.transparent_map_overlay);

        transparentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!animating) {
                    toggleMap();
                }
            }
        });

        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                updateData();
            }
        });
    }

    public void addTransparentView(View view) {
        transparentView = view;
    }

    private void toggleMap() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        View view = mapView.getMapFragment().getView();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        TranslateAnimation translateAnimation;

        int currentScrollOffset = calculateScrollOffset();

        float bottomY = displayMetrics.heightPixels - currentScrollOffset;
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

            if (mapView.getMapFragment().getView().getTranslationY() != 0) {
                final TranslateAnimation mapTranslateAnimation = new TranslateAnimation(NO_TRANSLATION, NO_TRANSLATION, mapView.getMapFragment().getView().getTranslationY(), NO_TRANSLATION);
                mapTranslateAnimation.setDuration(ANIMATION_LENGTH);
                //The way translatation animations are setup, this is done to avoid flicker
                mapView.getMapFragment().getView().setTranslationY(0);
                mapView.getMapFragment().getView().startAnimation(mapTranslateAnimation);
            }
        } else {
            mapViewExpanded = false;
            mapView.getMap().getUiSettings().setAllGesturesEnabled(false);
            mapView.setToDefaultBounds(false, 0);
            mapView.adjustCameraToShowInHeader(true, ANIMATION_LENGTH);
            mapItemsListViewWithFooter.setVisibility(View.VISIBLE);
        }
        mapItemsListViewWithFooter.startAnimation(translateAnimation);
    }

    private int calculateScrollOffset() {
        int[] location = new int[2];
        if (hasHeader) {
            routeInfoSegment.getLocationOnScreen(location);
        }
        return location[1];
    }

    private void startTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateData();
            }
        }, PREDICTIONS_TIMER_OFFSET, PREDICTIONS_PERIOD);
    }

    protected void updateData() {

    }

    @Override
    public void onAnimationStart(Animation animation) {
        animating = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (mapViewExpanded) {
            mapItemsListView.setSelection(0);
            mapItemsListViewWithFooter.setVisibility(View.GONE);
            mapView.getMap().getUiSettings().setAllGesturesEnabled(true);

            listButton.setVisibility(View.VISIBLE);
            myLocationButton.setVisibility(View.VISIBLE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
