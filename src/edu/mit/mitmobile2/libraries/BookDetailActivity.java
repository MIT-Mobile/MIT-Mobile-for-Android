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

public class BookDetailActivity extends SliderActivity {
    
    
    private static final String KEY = "key";
    private static final String KEY_POSITION = "key_position";
    private List<BookItem> mBooks = Collections.emptyList();
    
    static WeakHashMap<Long, List<BookItem>> sBookHashMap = new WeakHashMap<Long, List<BookItem>>();

    static void launchActivity(Context context, List<BookItem> items, int position) {
        // load the activity that shows all the detail search results
        Long key = System.currentTimeMillis();
        sBookHashMap.put(key, items);

        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.KEY, key);
        intent.putExtra(BookDetailActivity.KEY_POSITION, position);

        context.startActivity(intent);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            List<BookItem> bookItems = null;
            int position = 0;
            if (extras.containsKey(KEY)) {
                bookItems = sBookHashMap.get(extras.getLong(KEY));
                if(bookItems == null) {
                    finish();
                }
            }
            if (extras.containsKey(KEY_POSITION)) {
                position = extras.getInt(KEY_POSITION);
            }

            setBooks(bookItems, position);
        }
    }
    
    
    private void setBooks(List<BookItem> books, int position) {
        mBooks = books;
        for(BookItem book : mBooks) {
            addScreen(new BookDetailView(BookDetailActivity.this, book), book.id, "Book Detail");
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
