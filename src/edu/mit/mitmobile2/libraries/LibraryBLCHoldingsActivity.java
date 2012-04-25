package edu.mit.mitmobile2.libraries;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SectionHeader;
import edu.mit.mitmobile2.SectionHeader.Prominence;
import edu.mit.mitmobile2.SmallActivityCache;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.libraries.BookItem.Holding;

public class LibraryBLCHoldingsActivity extends NewModuleActivity {

	static SmallActivityCache<BookItem> sBookItemsCache = new SmallActivityCache<BookItem>();
	
	private static final String BOOK_ITEM_CACHE_KEY = "book_item_cache";
	
	public static void launch(Context context, BookItem book) {
		long bookCacheKey = sBookItemsCache.put(book);
		
		Intent intent = new Intent(context, LibraryBLCHoldingsActivity.class);
		intent.putExtra(BOOK_ITEM_CACHE_KEY, bookCacheKey);
		context.startActivity(intent);
	}
	
	Context mContext;
	
	TextView mTitleView;
	TextView mAuthorYearView;
	View mWorldCatLink;
	LinearLayout mBLCHoldingsLayout;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		mContext = this;
		
		setContentView(R.layout.library_worldcat_blc_holdings);
		
		final BookItem book = sBookItemsCache.getItem(getIntent().getLongExtra(BOOK_ITEM_CACHE_KEY, -1));
		if (book == null) {
			finish();
			return;
		}
		
		mTitleView = (TextView) findViewById(R.id.libraryWorldCatBookDetailsTitle);
		mAuthorYearView = (TextView) findViewById(R.id.libraryWorldCatBookAuthorYear);
		mWorldCatLink = findViewById(R.id.libraryBLCHoldingsWorldCatLink);
		mBLCHoldingsLayout = (LinearLayout) findViewById(R.id.libraryWorldCatBLCHoldingsLayout);
		
		mTitleView.setText(book.title);
		mAuthorYearView.setText(book.getAuthorsDisplayString());
		
		mWorldCatLink.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CommonActions.viewURL(mContext, book.url);
			}
		});
		
		mBLCHoldingsLayout.addView(new SectionHeader(this, "Owned by", Prominence.SECONDARY));
		for (Holding holding : book.holdings) {
			if (!holding.code.equals(BookItem.MITLibrariesOCLCCode)) {
				mBLCHoldingsLayout.addView(new DividerView(this, null));
				TwoLineActionRow blcHoldingRow = new TwoLineActionRow(this);
				blcHoldingRow.setTitle(holding.library);
				mBLCHoldingsLayout.addView(blcHoldingRow);
			}
		}	
	}
	
	@Override
	protected NewModule getNewModule() {
		return new LibrariesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}


	@Override
	protected boolean isScrollable() {
	    return true;
	}


	@Override
	protected void onOptionSelected(String optionId) {
	    // TODO Auto-generated method stub
	    
	}

}
