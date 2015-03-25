package edu.mit.mitmobile2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.res.Resources;
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

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.news.NewsFragment;
import edu.mit.mitmobile2.shuttles.fragment.MainShuttleFragment;

public class MITModuleActivity extends MITActivity implements ActionBar.TabListener, ActionBar.OnNavigationListener {

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

    protected Boolean hasSearch = false;
    protected Handler handler;
    private NavigationArrayAdapter adapter;

    private String module; // name of module
    private static final String DEFAULT_MODULE = "shuttles";
    private String[] params;
    private NavItem navItem;
    public Context mContext;

    public static List<NavItem> navigationTitles = new ArrayList<>();
    public static Map<String, NavItem> navMap = null;
    public static Map<String, String> moduleMap = null;
    public static HashMap<String, Fragment> fragmentMap = new HashMap<>();

    private NavItem mNavItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view for MIT Module (includes navigation drawer)
        setContentView(R.layout.mit_module_layout);

        mContext = this;
        setTheme(R.style.Theme_MyTheme);
        loadNavigation(mContext);
        MITAPIClient.init(mContext);

        if (MitMobileApplication.mAccount == null) {
            MitMobileApplication.mAccount = createSyncAccount(this);
            ContentResolver.setIsSyncable(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, true);

            ContentResolver.addPeriodicSync(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, Bundle.EMPTY, MitMobileApplication.INTERVAL_SECS);
        }

        // get progress bar
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // get layout inflater
        this.inflater = getLayoutInflater();

        // inflate content layout
        /*if (contentLayoutId > 0) {
            this.contentViewStub = (ViewStub) findViewById(R.id.contentViewStub);
            this.contentViewStub.setLayoutResource(contentLayoutId);
            this.contentViewStub.inflate();
        }
*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setSelection(4);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        adapter = new NavigationArrayAdapter(this, R.layout.drawer_list_item, R.id.navItemText, MITModuleActivity.navigationTitles);
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

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MainShuttleFragment()).commit();
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
        String long_name = MITModuleActivity.navigationTitles.get(position).getLong_name();

        // get NavItem from long_name
        mNavItem = MITModuleActivity.navMap.get(long_name);
        String intentString = mNavItem.getIntent();

        mDrawerLayout.closeDrawer(mDrawerList);

        //Intent intent = new Intent(mContext,c);
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mNavItem.getIntent()));

        //TODO: Swap fragments in here
        /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mNavItem.getUrl()));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Log.d("ZZZ", "URI = " + intent.getData().toString());
        startActivity(intent);*/

        // TDOD: Based on URI, swap in the correct fragment

        if (position == 0) {
            NewsFragment newsFragment = new NewsFragment();
            getFragmentManager().beginTransaction().replace(R.id.content_frame, newsFragment).commit();
        } else if (position == 1) {
            MainShuttleFragment fragment = new MainShuttleFragment();
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

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

    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                MitMobileApplication.ACCOUNT, MitMobileApplication.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    private void loadNavigation(Context mContext) {
        Resources resources = mContext.getResources();
        JSONObject navigation;
        if (MITModuleActivity.navMap == null) {
            MITModuleActivity.navMap = new HashMap<>(); // this maps long_name to navItem, since the long_name is stored in the nav drawer
            MITModuleActivity.moduleMap = new HashMap<>(); // this maps module name to long_name for resolving intents

            String json = null;

            try {
                InputStream is = getAssets().open("navigation.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                Log.d("ZZZ", json);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                navigation = new JSONObject(json);

                Iterator n = navigation.keys();
                List<String> keysList = new ArrayList<>();
                while (n.hasNext()) {
                    try {
                        String key = (String) n.next();
                        JSONObject module = navigation.getJSONObject(key);
                        String long_name = module.getString("long_name");
                        Log.d("ZZZ", long_name);
                        keysList.add(long_name);
                        NavItem navItem = new NavItem();
                        navItem.setLong_name(long_name);
                        navItem.setShort_name(module.getString("short_name"));

                        // Get Home Icon
                        int resourceId = resources.getIdentifier(module.getString("home_icon"), "drawable", mContext.getPackageName());
                        navItem.setHome_icon(resourceId);

                        // Get Menu Icon
                        resourceId = resources.getIdentifier(module.getString("menu_icon"), "drawable", mContext.getPackageName());
                        navItem.setMenu_icon(resourceId);

                        navItem.setIntent(module.getString("intent"));
                        navItem.setUrl(module.getString("url"));

                        MITModuleActivity.navMap.put(long_name, navItem);
                        MITModuleActivity.moduleMap.put(key, long_name);
                        MITModuleActivity.navigationTitles.add(navItem);

                        //TODO: Add the appropriate Fragment
//                        MITModuleActivity.fragmentMap.put(long_name, new Fragment());


                    } catch (JSONException e) {
                        Log.d("ZZZ", e.getMessage().toString());
                    }
                }


            } catch (Exception e) {
                Log.d("ZZZ", e.getMessage().toString());
            }
        }
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

    @Override
    protected void onStart() {
        super.onStart();

/*        Intent intent = getIntent();
        Log.d("ZZZ", "Intent: " + intent.getDataString());
        Log.d("ZZZ", "Scheme: " + intent.getScheme());

        //If the intent has come from a URL
        //Could swap activities w/fragments, make the call to show fragment in here
        if (intent.getScheme() != null && intent.getScheme().equals("mitmobile2")) {
            String intentString = intent.getDataString();
            String[] data = intentString.split("://");
            if (data != null && data.length == 2) {
                params = data[1].split("/");
                this.module = params[0];
                Log.d("ZZZ", "module = " + this.module);
                // Get the long_name of the module
            }
        } else {
            //If it's coming from app startup
            this.module = DEFAULT_MODULE;
        }

        String long_name = ModuleSelectorActivity.moduleMap.get(this.module);

        // use the long_name to get the navItem object
        navItem = ModuleSelectorActivity.navMap.get(long_name);
        Log.d("ZZZ", "navItem = " + navItem.toString());
        if (navItem != null) {
            Class<?> c = null;
            if (navItem.getIntent() != null) {
                try {
                    c = Class.forName(navItem.getIntent());
                } catch (ClassNotFoundException e) {
                    Log.d("ZZZ", "CLASS NOT FOUND " + e.getMessage());
                    e.printStackTrace();
                }
                Intent i = new Intent(mContext, c);
                Bundle extras = new Bundle();
                extras.putString("long_name", long_name);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Log.d("ZZZ", "starting activity " + navItem.getLong_name());
                startActivity(i);
            }
        }*/

    }

}

