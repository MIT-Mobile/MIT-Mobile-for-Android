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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.util.Log;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
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

import timber.log.Timber;

public class MITModuleActivity extends MITActivity implements ActionBar.TabListener, ActionBar.OnNavigationListener {

    private static DrawerLayout mDrawerLayout;
    private Spinner mSpinner;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Tab mTab;
    private CharSequence mDrawerTitle;
    protected CharSequence mTitle;
    protected String longName; // may be able to lose this in place of mTitle
    protected List spinnerList;
    protected int contentLayoutId;
    private ViewStub contentViewStub;
    private ProgressBar progressBar;
    protected LayoutInflater inflater;

    protected Boolean hasSearch = false;
    private NavigationArrayAdapter adapter;

    private String module; // name of module
    private static final String DEFAULT_MODULE = "news";
    private static final String LAST_MODULE = "module";
    private String[] params;
    private NavItem navItem;
    public Context mContext;
    private SharedPreferences appModulePref;
    private String currentModule;

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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setSelection(1);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mitmodule, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.action_search);
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchItem.setVisible(this.hasSearch);
        }
        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.mitmodule, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        MenuItem item = menu.findItem(R.id.action_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//        if (null != searchView) {
//            searchView.setSearchableInfo(searchManager
//                    .getSearchableInfo(getComponentName()));
//            searchView.setIconifiedByDefault(false);
//        }
//
//        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
//            public boolean onQueryTextChange(String newText) {
//                // this is your adapter that will be filtered
//                return true;
//            }
//
//            public boolean onQueryTextSubmit(String query) {
//                //Here u can get the value "query" which is entered in the search box.
//                Log.d("ZZZ", "search triggered");
//                searchView.clearFocus();
//                return handleSearch(query);
//            }
//        };
//
//        searchView.setOnQueryTextListener(queryTextListener);
//
//
//        // show or hide the search menu based on the hasSearch property
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        if (searchItem != null) {
//            searchItem.setVisible(this.hasSearch);
//        }
//        return true;
//    }
//

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        // Handle presses on the action bar items
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//
//
//    // Placeholder handleSearch method
//    // Override this method in subclass to define search functionality
//    protected boolean handleSearch(String search) {
//        return true;
//    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // get long_name from position
        String longName = MITModuleActivity.navigationTitles.get(position).getLongName();

        currentModule = longName;

        // get NavItem from long_name
        mNavItem = MITModuleActivity.navMap.get(longName);
        String intentString = mNavItem.getIntent();
        String title = navigationTitles.get(position).getLongName();

        mDrawerLayout.closeDrawer(mDrawerList);

        swapInFragment(intentString, title);
    }

    private void swapInFragment(String intentString, String title) {
        Log.d("ZZZ","intent string = " + intentString);
        Fragment f = null;
        try {
            f = (Fragment) Class.forName(intentString).newInstance();
        } catch (ClassNotFoundException e) {
            Timber.e(e, "Swap Fragments");
        } catch (InstantiationException e) {
            Timber.e(e, "Swap Fragments");
        } catch (IllegalAccessException e) {
            Timber.e(e, "Swap Fragments");
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
        setTitle(title);
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
                        String longName = module.getString("long_name");
                        Log.d("ZZZ", longName);
                        keysList.add(longName);
                        NavItem navItem = new NavItem();
                        navItem.setLong_name(longName);
                        navItem.setShortName(module.getString("short_name"));

                        // Get Home Icon
                        int resourceId = resources.getIdentifier(module.getString("home_icon"), "drawable", mContext.getPackageName());
                        navItem.setHomeIcon(resourceId);

                        // Get Menu Icon
                        resourceId = resources.getIdentifier(module.getString("menu_icon"), "drawable", mContext.getPackageName());
                        navItem.setMenuIcon(resourceId);

                        navItem.setIntent(module.getString("intent"));
                        navItem.setUrl(module.getString("url"));

                        MITModuleActivity.navMap.put(longName, navItem);
                        MITModuleActivity.moduleMap.put(key, longName);
                        MITModuleActivity.navigationTitles.add(navItem);

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

    @Override
    protected void onStart() {
        super.onStart();

        //TODO: Fix this part, it handles external URI calls into the app

        Intent intent = getIntent();
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

        String longName = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this).getString(LAST_MODULE, null);

        // use the long_name to get the navItem object
        if (longName != null) {
            navItem = MITModuleActivity.navMap.get(longName);
        } else {
            navItem = MITModuleActivity.navMap.get(MITModuleActivity.moduleMap.get(this.module));
        }

        Log.d("ZZZ", "navItem = " + navItem.toString());
        if (navItem != null) {
            swapInFragment(navItem.getIntent(), navItem.getLongName());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentModule != null) {
            SharedPreferences.Editor editor = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this).edit();
            editor.putString(LAST_MODULE, currentModule);
            editor.commit();
        }
    }
}
