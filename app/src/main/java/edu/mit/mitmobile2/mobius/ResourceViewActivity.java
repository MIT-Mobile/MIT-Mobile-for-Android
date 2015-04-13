package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TableLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.mobius.model.ResourceItem;
import timber.log.Timber;

public class ResourceViewActivity extends MITActivity {

    private Context mContext;
    private ResourceItem r;
    private ArrayList<ResourceItem> resourceList;
    private TableLayout resourceAttributeTable;
    private List resourceAttributeList;
    private ResourceAttributeAdapter resourceAttributeAdapter;

    ResourcePageAdapter pageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        if (getIntent().hasExtra("resources")) {
            resourceList = getIntent().getExtras().getParcelableArrayList("resources");
        }

        List<Fragment> fragments = getFragments();
        pageAdapter = new ResourcePageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager =
                (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(pageAdapter);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();

        if (resourceList != null) {
            Timber.d("sent " + resourceList.size() + " resource items");
            for (int i = 0; i < resourceList.size(); i++) {
                fList.add(ResourceViewFragment.newInstance(resourceList.get(i)));
            }
        }

        return fList;
    }
}
/*
        if(intent.hasExtra("resourceItem")) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.r = intent.getExtras().getParcelable("resourceItem");

            TextView resource_view_name = (TextView)findViewById(R.id.resource_view_name);
            resource_view_name.setText(r.getName());

            TextView resource_view_status = (TextView)findViewById(R.id.resource_view_status);
            resource_view_status.setText(r.getStatus());
            if (r.getStatus().equalsIgnoreCase(ResourceItem.OFFLINE)) {
                resource_view_status.setTextColor(Color.RED);
            }

            TextView resource_view_room = (TextView)findViewById(R.id.resource_view_room);
            resource_view_room.setText(r.getRoom());

            ImageView resource_view_map = (ImageView)findViewById(R.id.resource_view_map);
            resource_view_map.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ArrayList mapItems = new ArrayList();
                    mapItems.add(r);
                    Intent i = new Intent(mContext, ResourcesActivity.class);
                    i.putExtra(MapsActivity.MAP_ITEMS,mapItems);
                    startActivity(i);
                }
            });


            resourceAttributeTable = (TableLayout)findViewById(R.id.resource_view_attribute_table);

            // add attributes to the attribute view
            for (int i = 0; i < r.getAttributes().size(); i++) {
                ResourceAttribute a = r.getAttributes().get(i);
                TableRow tr = (TableRow)vi.inflate(R.layout.row_label_value, null);

                //Label
                TextView label = (TextView)tr.findViewById(R.id.row_label);
                label.setText(a.getLabel());

                //Value
                TextView value = (TextView)tr.findViewById(R.id.row_value);
                String valueString = "";
                if (!a.getValue().isEmpty()) {
                    for (int v = 0; v < a.getValue().size(); v++) {
                        String s = (String)a.getValue().get(v);
                        if (!s.trim().equals("")) {
                            valueString += s + "\n";
                        }
                    }
                }

                value.setText(valueString);

                // only add the attribute if the value is not empty
                if (!valueString.isEmpty()) {
                    resourceAttributeTable.addView(tr);
                }
            }


        }
*/
