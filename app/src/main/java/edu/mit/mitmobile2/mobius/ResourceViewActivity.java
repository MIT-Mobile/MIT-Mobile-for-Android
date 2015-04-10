package edu.mit.mitmobile2.mobius;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import java.util.List;

import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.mobius.model.ResourceItem;

public class ResourceViewActivity extends MITActivity {

    private Context mContext;
    private ResourceItem r;
    private TableLayout resourceAttributeTable;
    private List resourceAttributeList;
    private ResourceAttributeAdapter resourceAttributeAdapter;

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_screen_slide);

        getSupportActionBar().setTitle("Machine Detail");

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
    }



        private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
            public ScreenSlidePagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                return ResourceSlidePageFragment.create(position);
            }

            @Override
            public int getCount() {
                return NUM_PAGES;
            }
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
