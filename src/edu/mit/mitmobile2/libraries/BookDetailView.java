package edu.mit.mitmobile2.libraries;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.TwoLineActionRow;

public class BookDetailView implements SliderInterface {

    private Activity mActivity;
    private BookItem mBookItem;
    private LockingScrollView mView;
    
    private TwoLineActionRow mDetailTitle;
    private TwoLineActionRow mDetailAuthor;
    private TwoLineActionRow mDetailYear;
    
    public BookDetailView(Activity activity, BookItem bookItem) {
        mActivity = activity;
        mBookItem = bookItem;
        
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = (LockingScrollView) inflater.inflate(R.layout.book_detail, null);
        
        mDetailTitle = (TwoLineActionRow) mView.findViewById(R.id.bookDetailTitle);
        mDetailAuthor = (TwoLineActionRow) mView.findViewById(R.id.bookDetailAuthor);
        mDetailYear = (TwoLineActionRow) mView.findViewById(R.id.bookDetailYear);
        
        mDetailTitle.setTitle(mBookItem.title);
        StringBuilder builder = new StringBuilder();
        for(String author : mBookItem.author) {
            builder.append(author + ", ");
        }
        mDetailAuthor.setTitle(builder.toString());

        StringBuilder builder2 = new StringBuilder();
        for(String year : mBookItem.year) {
            builder2.append(year + ", ");
        }
        mDetailYear.setTitle(builder2.toString());
    }
    
    
    @Override
    public LockingScrollView getVerticalScrollView() {
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSelected() {

    }

    @Override
    public void updateView() {

    }

}
