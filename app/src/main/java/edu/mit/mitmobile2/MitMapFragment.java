package edu.mit.mitmobile2;

import android.app.Fragment;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;

public abstract class MitMapFragment extends Fragment implements Animation.AnimationListener, GoogleMap.OnMapLoadedCallback, GoogleMap.InfoWindowAdapter {
    public static final int NO_TRANSLATION = 0;
    public static final int ANIMATION_LENGTH = 500;

    private static final String MAP_EXPANDED_KEY = "mapExpanded";

    protected ListView mapItemsListView;
    protected MITMapView mitMapView;

    protected View transparentView;
    protected View transparentLandscapeView;

    protected RelativeLayout headerInfoSegment;
    protected SwipeRefreshLayout swipeRefreshLayout;
    private boolean swipeRefreshEnabled = false;

    protected boolean mapViewExpanded = false;
    protected boolean animating = false;

    protected boolean stopMode = false;
    protected FrameLayout shuttleStopContent;

    private int originalPosition = -1;

    private FloatingActionButton listButton;
    private FloatingActionButton myLocationButton;

    protected int headerLayout = R.layout.default_transparent_header;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_solo_map, null);

        if (savedInstanceState != null) {
            mapViewExpanded = savedInstanceState.getBoolean(MAP_EXPANDED_KEY, false);
        }

        shuttleStopContent = (FrameLayout) view.findViewById(R.id.shuttle_stop_content);
        shuttleStopContent.setVisibility(View.GONE);

        MapView googleMapView = (MapView) view.findViewById(R.id.route_map);
        googleMapView.onCreate(savedInstanceState);
        mitMapView = new MITMapView(getActivity(), googleMapView, this);

        mapItemsListView = (ListView) view.findViewById(R.id.map_list_view);
        listButton = (FloatingActionButton) view.findViewById(R.id.list_button);
        myLocationButton = (FloatingActionButton) view.findViewById(R.id.my_location_button);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.shuttle_route_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.black, R.color.mit_red);
        transparentLandscapeView = view.findViewById(R.id.transparent_map_overlay_landscape);

        setupFabButtons();
        setupMapView();

        if (mapViewExpanded) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
            } else {
                transparentLandscapeView.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
            }
            listButton.setVisibility(View.VISIBLE);
            myLocationButton.setVisibility(View.VISIBLE);
        }

        addHeaderView(View.inflate(getActivity(), headerLayout, null));

        return view;
    }

    protected void setupFabButtons() {
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

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListView();
            }
        });

        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLocation = mitMapView.getMap().getMyLocation();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 14f);
                mitMapView.getMap().animateCamera(update, 400, null);
            }
        });
    }

    protected void setupMapView() {
        mitMapView.setMapViewExpanded(mapViewExpanded);
        if (!mapViewExpanded) {
            getMapView().getUiSettings().setAllGesturesEnabled(false);
        }

        mitMapView.getMap().setInfoWindowAdapter(this);
    }

    //Setup for shuttle route
    protected void addHeaderView(View headerView) {
        mapItemsListView.addHeaderView(headerView);
        headerInfoSegment = (RelativeLayout) headerView.findViewById(R.id.route_info_segment);
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
        swipeRefreshLayout.setEnabled(swipeRefreshEnabled);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                updateData();
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mapItemsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int topRowPosition = (mapItemsListView == null || mapItemsListView.getChildCount() == 0) ?
                            0 : mapItemsListView.getChildAt(0).getTop();
                    swipeRefreshLayout.setEnabled((topRowPosition >= 0) && swipeRefreshEnabled);

                    if (!mapViewExpanded && !animating) {
                        int newPosition = calculateScrollOffset();

                        if (getMapView() != null && newPosition != 0) {
                            int translation;
                            if (originalPosition == -1) {
                                originalPosition = newPosition;
                            }

                            translation = (originalPosition - newPosition) / 2;
                            mitMapView.getGoogleMapView().setTranslationY(-translation);
                        }
                    }
                }
            });
        } else {
            transparentLandscapeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleMapHorizontal();
                }
            });
            mapItemsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int topRowPosition = (mapItemsListView == null || mapItemsListView.getChildCount() == 0) ?
                            0 : mapItemsListView.getChildAt(0).getTop();
                    swipeRefreshLayout.setEnabled((topRowPosition >= 0) && swipeRefreshEnabled);
                }
            });
        }

        mapItemsListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                listItemClicked(position);
            }
        });
    }

    protected void addTransparentView(View view) {
        transparentView = view;
    }

    protected void updateMapItems(ArrayList mapItems, boolean fit) {
        if (mapItems.size() == 0 || ((MapItem) mapItems.get(0)).isDynamic()) {
            mitMapView.clearDynamic();
        }
        mitMapView.addMapItemList(mapItems, false, fit);
    }

    protected void displayMapItems() {
        ArrayAdapter<MapItem> arrayAdapter = this.getMapItemAdapter();
        mapItemsListView.setAdapter(arrayAdapter);
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

    protected void toggleMap() {
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

        updateMITMapView(mitMapView);

        if (!mapViewExpanded) {
            mapViewExpanded = true;
            if (stopMode) {
                updateStopModeCamera(mapViewExpanded);
            } else {
                mitMapView.setToDefaultBounds(true, ANIMATION_LENGTH);
                if (mitMapView.getGoogleMapView().getTranslationY() != 0) {
                    final TranslateAnimation mapTranslateAnimation = new TranslateAnimation(NO_TRANSLATION, NO_TRANSLATION, mitMapView.getGoogleMapView().getTranslationY(), NO_TRANSLATION);
                    mapTranslateAnimation.setDuration(ANIMATION_LENGTH);
                    //The way translation animations are setup, this is done to avoid flicker
                    mitMapView.getGoogleMapView().setTranslationY(0);
                    mitMapView.getGoogleMapView().startAnimation(mapTranslateAnimation);
                }
            }
        } else {
            mapViewExpanded = false;
            mitMapView.getMap().getUiSettings().setAllGesturesEnabled(false);
            if (stopMode) {
                shuttleStopContent.setVisibility(View.VISIBLE);
                updateStopModeCamera(mapViewExpanded);
            } else {
                mitMapView.setToDefaultBounds(false, 0);
                mitMapView.adjustCameraToShowInHeader(true, ANIMATION_LENGTH, getResources().getConfiguration().orientation);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        }
        if (stopMode) {
            shuttleStopContent.startAnimation(translateAnimation);
        } else {
            swipeRefreshLayout.startAnimation(translateAnimation);
        }
    }

    protected void toggleMapHorizontal() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        TranslateAnimation translateAnimation;

        if (mapViewExpanded) {
            translateAnimation = new TranslateAnimation(displayMetrics.widthPixels, NO_TRANSLATION, NO_TRANSLATION, NO_TRANSLATION);
        } else {
            translateAnimation = new TranslateAnimation(NO_TRANSLATION, displayMetrics.widthPixels, NO_TRANSLATION, NO_TRANSLATION);
        }

        translateAnimation.setDuration(ANIMATION_LENGTH);
        translateAnimation.setAnimationListener(this);

        updateMITMapView(mitMapView);

        if (!mapViewExpanded) {
            mapViewExpanded = true;
            transparentLandscapeView.setVisibility(View.INVISIBLE);
            if (stopMode) {
                updateStopModeCamera(mapViewExpanded);
            } else {
                mitMapView.setToDefaultBounds(true, ANIMATION_LENGTH);
            }
        } else {
            mapViewExpanded = false;
            transparentLandscapeView.setVisibility(View.VISIBLE);
            if (stopMode) {
                shuttleStopContent.setVisibility(View.VISIBLE);
                updateStopModeCamera(mapViewExpanded);
            } else {
                mitMapView.setToDefaultBounds(false, 0);
                mitMapView.adjustCameraToShowInHeader(true, ANIMATION_LENGTH, getResources().getConfiguration().orientation);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        }
        if (stopMode) {
            shuttleStopContent.startAnimation(translateAnimation);
        } else {
            swipeRefreshLayout.startAnimation(translateAnimation);
        }
    }

    protected void updateStopModeCamera(boolean mapViewExpanded) {

    }

    private int calculateScrollOffset() {
        int[] location = new int[2];
        if (headerInfoSegment != null) {
            headerInfoSegment.getLocationOnScreen(location);
        }
        return location[1];
    }

    public void updateMITMapView(MITMapView mapView) {
        mapView.updateStaticItems(mapViewExpanded);
    }

    protected void listItemClicked(int position) {

    }

    protected GoogleMap getMapView() {
        return mitMapView.getMap();
    }

    protected ArrayAdapter<MapItem> getMapItemAdapter() {
        return null;
    }


    public boolean isMapViewExpanded() {
        return mapViewExpanded;
    }

    protected void updateData() {

    }

    protected void queryDatabase() {

    }

    public void setSwipeRefreshEnabled(boolean swipeRefreshEnabled) {
        this.swipeRefreshEnabled = swipeRefreshEnabled;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        animating = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (mapViewExpanded) {
            if (stopMode) {
                shuttleStopContent.setVisibility(View.GONE);
            } else {
                mapItemsListView.setSelection(0);
                swipeRefreshLayout.setVisibility(View.GONE);
            }
            mitMapView.getMap().getUiSettings().setAllGesturesEnabled(true);
            listButton.setVisibility(View.VISIBLE);
            myLocationButton.setVisibility(View.VISIBLE);
        }
        animating = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onPause() {
        mitMapView.getGoogleMapView().onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mitMapView.getGoogleMapView().onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mitMapView.getGoogleMapView().onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MAP_EXPANDED_KEY, mapViewExpanded);
        mitMapView.getGoogleMapView().onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mitMapView.getGoogleMapView().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onMapLoaded() {
        if (mitMapView.getDefaultBounds() != null) {
            mitMapView.setToDefaultBounds(false, 0);

            if (!mapViewExpanded) {
                mitMapView.adjustCameraToShowInHeader(false, 0, getActivity().getResources().getConfiguration().orientation);
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
