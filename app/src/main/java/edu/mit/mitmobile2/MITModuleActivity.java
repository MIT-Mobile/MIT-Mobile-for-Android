package edu.mit.mitmobile2;

import java.util.List;

import android.app.SearchManager;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.util.Log;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

public abstract class MITModuleActivity extends MITActivity implements ActionBar.TabListener, ActionBar.OnNavigationListener {

    private static DrawerLayout mDrawerLayout;
    private Spinner mSpinner;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Tab mTab;
    private CharSequence mDrawerTitle;
    protected CharSequence mTitle;
    protected String long_name; // may be able to lose this in place of mTitle     
    protected List spinnerList;
    protected int contentLayoutId;
    private ViewStub contentViewStub;
    private ProgressBar progressBar;
    protected LayoutInflater inflater;

    private NavItem mNavItem;
    protected Boolean hasSearch = false;
    protected Handler handler;
    private NavigationArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view for MIT Module (includes navigation drawer)
        setContentView(R.layout.mit_module_layout);

        // get progress bar
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // get layout inflater
        this.inflater = getLayoutInflater();

        // inflate content layout
        if (contentLayoutId > 0) {
            this.contentViewStub = (ViewStub) findViewById(R.id.contentViewStub);
            this.contentViewStub.setLayoutResource(contentLayoutId);
            this.contentViewStub.inflate();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setSelection(4);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        adapter = new NavigationArrayAdapter(this, R.layout.drawer_list_item, R.id.navItemText, ModuleSelectorActivity.navigationTitles);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                syncState();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                syncState();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (this.getSpinnerList() != null) {
            // Create an ArrayAdapter using the string array and a default spinner layout
//	     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//	             R.array.planets_array, android.R.layout.simple_spinner_item);
//	     // Specify the layout to use when the list of choices appears
//	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	     // Apply the adapter to the spinner
//
//	     //mSpinner.setAdapter(adapter);
//	     getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//	     getActionBar().setListNavigationCallbacks(adapter, this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mitmodule, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                Log.d("ZZZ", "search triggered");
                searchView.clearFocus();
                return handleSearch(query);
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);


        // show or hide the search menu based on the hasSearch property
        MenuItem searchItem = menu.findItem(R.id.search);
        if (searchItem != null) {
            searchItem.setVisible(this.hasSearch);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.search) {
            this.handleSearch("test");
        }
        // Handle presses on the action bar items
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Placeholder handleSearch method
    // Override this method in subclass to define search functionality
    protected boolean handleSearch(String search) {
        return true;
    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // get long_name from position
        String long_name = ModuleSelectorActivity.navigationTitles.get(position).getLong_name();

        // get NavItem from long_name
        mNavItem = ModuleSelectorActivity.navMap.get(long_name);
        String intentString = mNavItem.getIntent();

        mDrawerLayout.closeDrawer(mDrawerList);

        //Intent intent = new Intent(mContext,c);
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mNavItem.getIntent()));

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mNavItem.getUrl()));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Log.d("ZZZ", "URI = " + intent.getData().toString());
        startActivity(intent);


//    	// update the main content by replacing fragments
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//        // update selected item and title, then close the drawer
//        mDrawerList.setItemChecked(position, true);
//        setTitle(navigationTitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mTitle != null) {
            getSupportActionBar().setTitle(mTitle);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onNavigationItemSelected(int arg0, long arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    public int getContentLayoutId() {
        return contentLayoutId;
    }

    public void setContentLayoutId(int contentLayoutId) {
        this.contentLayoutId = contentLayoutId;
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void showProgressBar() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.contentViewStub.setVisibility(View.GONE);
    }

    public void hideProgressBar() {
        this.progressBar.setVisibility(View.GONE);
        this.contentViewStub.setVisibility(View.VISIBLE);
    }

    public List getSpinnerList() {
        return spinnerList;
    }

    public void setSpinnerList(List spinnerList) {
        this.spinnerList = spinnerList;
    }

    public CharSequence getmTitle() {
        return mTitle;
    }

    public void setmTitle(CharSequence mTitle) {
        this.mTitle = mTitle;
    }

    public String getLong_name() {
        return long_name;
    }

    public void setLong_name(String long_name) {
        this.long_name = long_name;
    }


}

