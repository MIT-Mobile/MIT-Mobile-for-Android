package edu.mit.mitmobile2.shuttles.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;

public class MitMapFragment extends Fragment implements Animation.AnimationListener, LoaderManager.LoaderCallbacks<Cursor>, GoogleMap.OnMapLoadedCallback {
    private static final int PREDICTIONS_PERIOD = 15000;
    private static final int PREDICTIONS_TIMER_OFFSET = 10000;

    public static final int NO_TRANSLATION = 0;
    public static final int ANIMATION_LENGTH = 500;

    private static final String MAP_EXPANDED_KEY = "mapExpanded";

    private ListView mapItemsListView;
    private ArrayList mapItems;
    private MITMapView mapView;

    private LinearLayout mapItemsListViewWithFooter;
    private View transparentView;
    private FrameLayout predictionFragment;
    private View transparentLandscapeView;

    private boolean hasHeader;
    private RelativeLayout routeInfoSegment;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected boolean mapViewExpanded = false;
    private boolean animating = false;

    private int originalPosition = -1;

    private static Timer timer;

    private FloatingActionButton listButton;
    private FloatingActionButton myLocationButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_solo_map, null);

        if (savedInstanceState != null) {
            mapViewExpanded = savedInstanceState.getBoolean(MAP_EXPANDED_KEY, false);
        }

        predictionFragment = (FrameLayout) view.findViewById(R.id.prediction_fragment);

        mapItemsListView = (ListView) view.findViewById(R.id.map_list_view);
        mapItemsListViewWithFooter = (LinearLayout) view.findViewById(R.id.map_list_view_with_footer);
        listButton = (FloatingActionButton) view.findViewById(R.id.list_button);
        myLocationButton = (FloatingActionButton) view.findViewById(R.id.my_location_button);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.shuttle_route_refresh_layout);

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

        if (getActivity().getIntent().getExtras() != null) {
            predictionFragment.setVisibility(View.GONE);
        } else {
            mapItemsListView.setVisibility(View.GONE);
        }

        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().replace(R.id.route_map, mapFragment).commit();

        mapView = new MITMapView(getActivity(), getActivity().getFragmentManager(), mapFragment);
        mapView.getMap().getUiSettings().setAllGesturesEnabled(false);
        mapView.getMap().setOnMapLoadedCallback(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
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
        } else {
            transparentLandscapeView = view.findViewById(R.id.transparent_map_overlay_landscape);
            transparentLandscapeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleMapHorizontal();
                }
            });
        }

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

        if (mapViewExpanded) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mapItemsListViewWithFooter.setVisibility(View.INVISIBLE);
            } else {
                transparentLandscapeView.setVisibility(View.INVISIBLE);
                mapItemsListViewWithFooter.setVisibility(View.INVISIBLE);
            }
            listButton.setVisibility(View.VISIBLE);
            myLocationButton.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void showListView() {
        if (!animating) {
            listButton.setVisibility(View.INVISIBLE);
            myLocationButton.setVisibility(View.INVISIBLE);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                toggleMap();
            } else {
                toggleMapHorizontal();
            }
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

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            transparentView.setVisibility(View.GONE);
        } else {
            transparentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!animating) {
                        toggleMap();
                    }
                }
            });
        }
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
            mapView.adjustCameraToShowInHeader(true, ANIMATION_LENGTH, getResources().getConfiguration().orientation);
            mapItemsListViewWithFooter.setVisibility(View.VISIBLE);
        }
        mapItemsListViewWithFooter.startAnimation(translateAnimation);
    }

    private void toggleMapHorizontal() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        TranslateAnimation translateAnimation;

        if (mapViewExpanded) {
            translateAnimation = new TranslateAnimation(displayMetrics.widthPixels, NO_TRANSLATION, NO_TRANSLATION, NO_TRANSLATION);
        } else {
            translateAnimation = new TranslateAnimation(NO_TRANSLATION, displayMetrics.widthPixels, NO_TRANSLATION, NO_TRANSLATION);
        }

        translateAnimation.setDuration(ANIMATION_LENGTH);
        translateAnimation.setAnimationListener(this);

        if (!mapViewExpanded) {
            mapViewExpanded = true;
            mapView.setToDefaultBounds(true, ANIMATION_LENGTH);
            transparentLandscapeView.setVisibility(View.INVISIBLE);
        } else {
            mapViewExpanded = false;
            mapView.setToDefaultBounds(false, 0);
            mapView.adjustCameraToShowInHeader(true, ANIMATION_LENGTH, getResources().getConfiguration().orientation);
            transparentLandscapeView.setVisibility(View.VISIBLE);
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
    public void onPause() {
        timer.cancel();
        timer.purge();
        timer = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        startTimerTask();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MAP_EXPANDED_KEY, mapViewExpanded);
    }

    @Override
    public void onMapLoaded() {
        if (mapView.getDefaultBounds() != null) {
            mapView.setToDefaultBounds(false, 0);

            if (!mapViewExpanded) {
                mapView.adjustCameraToShowInHeader(false, 0, getActivity().getResources().getConfiguration().orientation);
            }
        }
    }

    public boolean isMapViewExpanded() {
        return mapViewExpanded;
    }
}
