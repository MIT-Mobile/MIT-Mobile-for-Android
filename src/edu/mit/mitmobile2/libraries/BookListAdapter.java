package edu.mit.mitmobile2.libraries;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;

public class BookListAdapter extends SimpleArrayAdapter<BookItem> {
    
    public BookListAdapter(Context context, List<BookItem> items) {
        super(context, items, R.layout.library_worldcat_book_row);
    }
    
    @Override
	public void updateView(BookItem book, View view) {          
    	TextView titleView = (TextView) view.findViewById(R.id.libraryWorldCatBookRowTitle);
    	TextView subtitleView = (TextView) view.findViewById(R.id.libraryWorldCatBookRowSubtitle);
    	titleView.setText(book.title);
    	subtitleView.setText(book.getAuthorsDisplayString());
    }


}
