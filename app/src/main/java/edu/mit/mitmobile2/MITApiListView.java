package edu.mit.mitmobile2;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

import edu.mit.mitmobile2.mobius.ResourceItem;
import timber.log.Timber;

/**
 * Created by sseligma on 4/4/15.
 * The MITApiListView class is an abstract class for generating list views that are populated from an API call
 *
 */
public abstract class MITApiListView {

    protected Activity activity;
    protected ListView listView;
    protected List listData;
    protected int layoutResourceId;
    protected ArrayAdapter<Object> apiArrayAdapter;

    public MITApiListView(Activity activity, int listViewId) {
        this.activity = activity;
        this.listView = (ListView)activity.findViewById(listViewId);
        Timber.d("listView = " + this.listView);
    }


    public void load(MITAPIClient apiClient, String api, String path, Map<String, String> params) {
        apiClient.getJson(api,path,params,this.getApiHandler());
    }

    protected abstract Handler getApiHandler();

    protected abstract ArrayAdapter<Object> getArrayAdapter();
}
