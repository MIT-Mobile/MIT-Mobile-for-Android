package edu.mit.mitmobile2.resources;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;

public class ResourceViewActivity extends Activity {

    private Context mContext;
    private ListView resourceAttributeListView;
    private List resourceAttributeList;
    private ResourceAttributeAdapter resourceAttributeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_resource_view);
        Intent intent = getIntent();
        if(intent.hasExtra("resourceItem")) {
            ResourceItem r = intent.getExtras().getParcelable("resourceItem");

            TextView resource_view_name = (TextView)findViewById(R.id.resource_view_name);
            resource_view_name.setText(r.getName());

            TextView resource_view_status = (TextView)findViewById(R.id.resource_view_status);
            resource_view_status.setText(r.getStatus());
            if (r.getStatus().equalsIgnoreCase(ResourceItem.OFFLINE)) {
                resource_view_status.setTextColor(Color.RED);
            }

            TextView resource_view_room = (TextView)findViewById(R.id.resource_view_room);
            resource_view_room.setText(r.getRoom());

            resourceAttributeListView = (ListView)findViewById(R.id.resource_view_attribute_list);
            Log.d("ZZZ","list view null = " + (resourceAttributeListView == null));
            Log.d("ZZZ","adapter  null = " + (resourceAttributeAdapter == null));
            Log.d("ZZZ","attributes null = " + (r.getAttributes() == null));


            //resourceAttributeAdapter = new ResourceAttributeAdapter(mContext, R.id.row_label_value, r.getAttributes());
            //resourceAttributeListView.setAdapter(resourceAttributeAdapter);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resource_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
