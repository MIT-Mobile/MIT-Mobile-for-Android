package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import edu.mit.mitmobile2.APIJsonResponse;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.MITApiListView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.mobius.model.QuickSearch;
import edu.mit.mitmobile2.mobius.model.ResourceType;
import edu.mit.mitmobile2.mobius.model.ResourceTypeList;
import edu.mit.mitmobile2.mobius.model.Roomset;
import edu.mit.mitmobile2.mobius.model.RoomsetList;
import timber.log.Timber;

public class ResourceTypesActivity extends MITActivity   {

    int contentLayoutId = R.layout.content_resource_types;
    Context context;
    List listData;
    ListView listView;

    MITApiListView resourceShopListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.content_resource_types);
        getSupportActionBar().setTitle("Machine Types");
        listView = (ListView)findViewById(R.id.resourceTypeListView);

        apiClient.getJson("resourcetype", "", null, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                APIJsonResponse response = (APIJsonResponse) msg.obj;
                if (response != null) {
                    listData = new ResourceTypeList(response.jsonArray);
                    Timber.d("listview null =" + (listView == null));
                    listView.setAdapter(new ArrayAdapter<ResourceType>(context, R.layout.row_resource_type, listData) {

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater inflater = getLayoutInflater();
                            convertView = inflater.inflate(R.layout.row_resource_type, null, false);
                            TextView tvResourceType = (TextView) convertView.findViewById(R.id.resource_type);
                            ResourceType resourceType = (ResourceType) getItem(position);
                            tvResourceType.setText(resourceType.getType());
                            return convertView;
                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ResourceType resourceType = (ResourceType) listData.get(position);
                            Timber.d("clicked type " + resourceType.get_id());
                            Intent i = new Intent(context,ResourceListActivity.class);
                            QuickSearch qs = new QuickSearch();
                            qs.setType("TYPE");
                            qs.setValue(resourceType.get_id());
                            i.putExtra("quicksearch",qs);
                            startActivity(i);
                        }
                    });
                }
            }
        });
    }

}
