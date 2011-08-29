package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchBar;
import edu.mit.mitmobile2.TwoLineActionRow;

public class LibraryActivity extends ModuleActivity {

    private TwoLineActionRow accountRow;
    private TwoLineActionRow locationRow;
    private TwoLineActionRow askUsRow;
    private TwoLineActionRow tellUsRow;

    private FullScreenLoader mLoadingView;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViews();

        doSearch();
    }

    private void createViews() {
        setContentView(R.layout.library_main);

        mLinearLayout = (LinearLayout) findViewById(R.id.libraryMainLinearLayout);

        SearchBar searchBar = (SearchBar) findViewById(R.id.librarySearchBar);
        searchBar.setSearchHint(getString(R.string.library_search_hint));
        searchBar.setSystemSearchInvoker(this);

        accountRow = (TwoLineActionRow) findViewById(R.id.libraryAccount);
        locationRow = (TwoLineActionRow) findViewById(R.id.libraryLocationHours);
        askUsRow = (TwoLineActionRow) findViewById(R.id.libraryAskUs);
        tellUsRow = (TwoLineActionRow) findViewById(R.id.libraryTellUs); // librarySearch

        accountRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        locationRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LibraryActivity.this, LibraryLocationAndHour.class));
            }
        });
        askUsRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        tellUsRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });

        mLoadingView = (FullScreenLoader) findViewById(R.id.linkLoading);

    }

    @Override
    protected Module getModule() {
        return new LibraryModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return true;
    }

    @Override
    protected void prepareActivityOptionsMenu(Menu menu) {
    }

    private void doSearch() {
        mLinearLayout.setVisibility(View.GONE);

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();

        LibraryModel.fetchLinks(this, uiHandler);
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLoadingView.setVisibility(View.GONE);
            mLinearLayout.removeAllViews();

            if (msg.arg1 == MobileWebApi.SUCCESS) {
                @SuppressWarnings("unchecked")
                final ArrayList<LinkItem> results = (ArrayList<LinkItem>) msg.obj;

                if (results.size() == 0) {
                    Toast.makeText(LibraryActivity.this, "No links found", Toast.LENGTH_SHORT).show();
                }

                if (results.size() > 0) {
                    for (final LinkItem item : results) {
                        TwoLineActionRow row = new TwoLineActionRow(LibraryActivity.this);
                        row.setTitle(item.title);
                        row.setActionIconResource(R.drawable.action_external);
                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonActions.viewURL(LibraryActivity.this, item.url);
                            }
                        });

                        mLinearLayout.addView(row);
                        mLinearLayout.addView(new DividerView(LibraryActivity.this, null));
                    }

                    mLinearLayout.setVisibility(View.VISIBLE);

                }

            } else {
                mLoadingView.showError();
            }
        }
    };


    static class LinkItem {
        public String title;
        public String url;
    }

}
