package edu.mit.mitmobile2.libraries;

import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.SliderActivity;

public class LibraryDetailActivity extends SliderActivity {
    private static final String KEY = "key";
    private static final String KEY_POSITION = "key_position";
    private List<LibraryItem> mLibraries = Collections.emptyList();
    
    static WeakHashMap<Long, List<LibraryItem>> sLibraryHashMap = new WeakHashMap<Long, List<LibraryItem>>();

    static void launchActivity(Context context, List<LibraryItem> items, int position) {
        // load the activity that shows all the detail search results
        Long key = System.currentTimeMillis();
        sLibraryHashMap.put(key, items);

        Intent intent = new Intent(context, LibraryDetailActivity.class);
        intent.putExtra(LibraryDetailActivity.KEY, key);
        intent.putExtra(LibraryDetailActivity.KEY_POSITION, position);

        context.startActivity(intent);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            List<LibraryItem> libraryItems = null;
            int position = 0;
            if (extras.containsKey(KEY)) {
                libraryItems = sLibraryHashMap.get(extras.getLong(KEY));
                if(libraryItems == null) {
                    finish();
                }
            }
            if (extras.containsKey(KEY_POSITION)) {
                position = extras.getInt(KEY_POSITION);
            }

            setLibraries(libraryItems, position);
        }
    }
    
    
    private void setLibraries(List<LibraryItem> libraries, int position) {
        mLibraries = libraries;
        for(LibraryItem library : mLibraries) {
            addScreen(new LibraryDetailView(LibraryDetailActivity.this, library), library.library, "Library Detail");
        }
        
        setPosition(position);
    }
    
    @Override
    protected Module getModule() {
        return new LibrariesModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return false;
    }

    @Override
    protected void prepareActivityOptionsMenu(Menu menu) {
    }

}
