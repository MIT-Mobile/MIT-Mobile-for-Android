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
import edu.mit.mitmobile2.mobius.model.Roomset;
import edu.mit.mitmobile2.mobius.model.RoomsetList;
import timber.log.Timber;

public class ResourceShopsActivity extends MITActivity   {

    int contentLayoutId = R.layout.content_resource_shops;
    Context context;
    List listData;
    ListView listView;

    MITApiListView resourceShopListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.content_resource_shops);
        getSupportActionBar().setTitle("Shops & Labs");
        listView = (ListView)findViewById(R.id.resourceShopListView);

        apiClient.getJson("resourceroomset","",null,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                APIJsonResponse response = (APIJsonResponse) msg.obj;
                if (response != null) {
                    Timber.d("num roomset = " + response.jsonArray.length());
                    listData  = new RoomsetList(response.jsonArray);
                    Timber.d("listview null =" + (listView == null));
                    listView.setAdapter(new ArrayAdapter<Roomset>(context,R.layout.row_resource_roomset,listData){

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater inflater = getLayoutInflater();
                            convertView = inflater.inflate(R.layout.row_resource_roomset, null, false);
                            TextView tvRoomsetName = (TextView)convertView.findViewById(R.id.roomset_name);
                            Roomset roomset = (Roomset)getItem(position);
                            if (roomset != null) {
                                tvRoomsetName.setText(roomset.getRoomset_name());
                            }
                            else {
                                tvRoomsetName.setText("roomset");
                            }
                            return convertView;


                        }
                     });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Roomset roomset = (Roomset)listData.get(position);
                            Intent i = new Intent(context,ResourceListActivity.class);
                            QuickSearch qs = new QuickSearch();
                            qs.setType("ROOMSET");
                            qs.setValue(roomset.get_id());
                            i.putExtra("quicksearch",qs);
                            startActivity(i);
                        }
                    });
                }
            }
        });
    }

}
