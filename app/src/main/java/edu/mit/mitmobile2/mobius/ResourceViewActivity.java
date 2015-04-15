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