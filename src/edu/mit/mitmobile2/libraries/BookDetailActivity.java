package edu.mit.mitmobile2.libraries;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.SmallActivityCache;

public class BookDetailActivity extends SliderActivity {
    
    
    private static final String KEY = "key";
    private List<BookItem> mBooks = Collections.emptyList();
    
    static SmallActivityCache<List<BookItem>> sBookHashMap = new SmallActivityCache<List<BookItem>>();

    static void launchActivity(Context context, List<BookItem> items, int position) {
        // load the activity that shows all the detail search results
        Long key = sBookHashMap.put(items);

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

            if (extras.containsKey(KEY)) {
                bookItems = sBookHashMap.getItem(extras.getLong(KEY));
                if(bookItems == null) {
                    finish();
                    return;
                }
            }

            setBooks(bookItems);
        }
    }
    
    
    private void setBooks(List<BookItem> books) {
        mBooks = books;
        for(BookItem book : mBooks) {
            addScreen(new BookDetailView(BookDetailActivity.this, book), book.id, "Book Detail");
        }
        
        setPosition(getPositionValue());
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
