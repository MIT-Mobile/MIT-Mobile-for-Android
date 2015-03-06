package edu.mit.mitmobile2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;

public class SoloMapActivity extends MITActivity {

    private ListView mapItemsListview;
    private ArrayList mapItems;
    private MITMapView mapView;
    private LinearLayout routeInfoSegment;
    private View transparentView;

    private int originalPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_map);

        mapItemsListview = (ListView) findViewById(R.id.map_list_view);
        View header = View.inflate(this, R.layout.stops_list_header, null);
        mapItemsListview.addHeaderView(header);

        mapView = new MITMapView(this, getFragmentManager(), R.id.route_map);
        routeInfoSegment = (LinearLayout) findViewById(R.id.route_info_segment);
        transparentView = findViewById(R.id.transparent_map_overlay);

        mapItemsListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int[] location = new int[2];
                routeInfoSegment.getLocationOnScreen(location);

                int newPosition = location[1];

                if (getMapView() != null && newPosition != 0) {
                    int translation;
                    if (originalPosition == -1) {
                        originalPosition = newPosition;
                    }

                    translation = (originalPosition - newPosition) / 2;
                    mapView.getMapFragment().getView().setTranslationY(translation);
                }
            }
        });

    }

    protected void setMapItems(ArrayList mapItems) {
        this.mapItems = mapItems;
    }

    protected void displayMapItems() {
        Log.d(TAG, "displayMapItems()");
        ArrayAdapter<MapItem> arrayAdapter = this.getMapItemAdapter();
        mapItemsListview.setAdapter(arrayAdapter);

        if (mapView != null) {
            mapView.addMapItemList(this.mapItems);
        }

//        mapListView.setOnItemClickListener(this.getOnItemClickListener());
//        toggleMap();
    }

    protected GoogleMap getMapView() {
        return mapView.getMap();
    }

    protected ArrayAdapter<MapItem> getMapItemAdapter() {
        return null;
    }
}
