package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
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
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;

public class LibraryLocationAndHour extends NewModuleActivity {

    private ListView mListView;
    private FullScreenLoader mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_location_hour);

        mListView = (ListView) findViewById(R.id.listLibraryLocation);
        mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);
        
        addSecondaryTitle("Locations & Hours");        
        
        doFetch();
    }

    private void doFetch() {
        mListView.setVisibility(View.GONE);

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();

        LibraryModel.fetchLocationsAndHours(this, uiHandler);
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
                @SuppressWarnings("unchecked")
                final ArrayList<LibraryItem> results = (ArrayList<LibraryItem>) msg.obj;

                if (results.size() == 0) {
                    Toast.makeText(LibraryLocationAndHour.this, "No libraries found", Toast.LENGTH_SHORT).show();
                }
                
                
                LibraryListAdapter adapter = new LibraryListAdapter(results);
                mListView.setAdapter(adapter);
                adapter.setLookupHandler(mListView, null);
                mListView.setVisibility(View.VISIBLE);

            } else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                mLoadingView.showError();
            }
        }
    };

    @Override
    protected NewModule getNewModule() {
        return new LibrariesModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return false;
    }

    private class LibraryListAdapter extends SimpleArrayAdapter<LibraryItem> {

        private List<LibraryItem> libraryItems;

        public LibraryListAdapter(List<LibraryItem> items) {
            super(LibraryLocationAndHour.this, items, R.layout.boring_action_row);
            libraryItems = items;
        }

        public void setLookupHandler(ListView listView, final String extras) {
            setOnItemClickListener(listView, new SimpleArrayAdapter.OnItemClickListener<LibraryItem>() {
                @Override
                public void onItemSelected(LibraryItem item) {
                    LibraryDetailActivity.launchActivity(getContext(), libraryItems, libraryItems.indexOf(item));
                }
            });
        }

        @Override
        public void updateView(LibraryItem item, View view) {
            TwoLineActionRow twoLineActionRow = (TwoLineActionRow) view;
            twoLineActionRow.setTitle(item.library);
            twoLineActionRow.setSubtitle(item.status);
        }

    }

    @Override
    protected boolean isScrollable() {
	return false;
    }

    @Override
    protected void onOptionSelected(String optionId) {
	// TODO Auto-generated method stub
	
    }

}
