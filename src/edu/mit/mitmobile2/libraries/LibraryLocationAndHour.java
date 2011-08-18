package edu.mit.mitmobile2.libraries;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.SearchResults;

public class LibraryLocationAndHour extends ModuleActivity {

    private ListView mListView;
    private FullScreenLoader mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_location_hour);

        mListView = (ListView) findViewById(R.id.listLibraryLocation);
        mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

        doSearch();
    }

    private void doSearch() {
        mListView.setVisibility(View.GONE);

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();

        LibraryModel.executeSearch(this, uiHandler);
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
                @SuppressWarnings("unchecked")
                final SearchResults<LibraryItem> searchResults = (SearchResults<LibraryItem>) msg.obj;

                if (searchResults.getResultsList().size() == 0) {
                    Toast.makeText(LibraryLocationAndHour.this, "No libraries found", Toast.LENGTH_SHORT).show();
                }

                mListView.setAdapter(new LibraryListAdapter(searchResults.getResultsList()));
                mListView.setVisibility(View.VISIBLE);

            } else if (msg.arg1 == MobileWebApi.ERROR) {
                // TODO:
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                // TODO:
            }
        }
    };

    @Override
    protected Module getModule() {
        return new LibraryModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return false;
    }

    @Override
    protected void prepareActivityOptionsMenu(Menu menu) {

    }

    private class LibraryListAdapter extends SimpleArrayAdapter<LibraryItem> {

        public LibraryListAdapter(List<LibraryItem> items) {
            super(LibraryLocationAndHour.this, items, R.layout.boring_action_row);
        }

        @Override
        public void updateView(LibraryItem item, View view) {
            TwoLineActionRow twoLineActionRow = (TwoLineActionRow) view;
            twoLineActionRow.setTitle(item.library);
            twoLineActionRow.setSubtitle(item.status);
        }

    }

}
