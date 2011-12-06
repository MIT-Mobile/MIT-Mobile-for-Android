package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SectionHeader;
import edu.mit.mitmobile2.SectionHeader.Prominence;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.libraries.BookItem.Holding;
import edu.mit.mitmobile2.libraries.BookItem.Holding.Availabilitys;

public class BookDetailView implements SliderInterface {

	enum DetailsLoadingStatus {
		Loaded,
		Loading,
		NotLoaded
	}
	
	private Activity mActivity;
    private BookItem mBookItem;
    private LockingScrollView mView;
    
    private DetailsLoadingStatus mLoadingStatus;
    private FullScreenLoader mFullScreenLoader;
    private TextView mTitleTextView;
    private LinearLayout mDetailsLinearLayout;
    private LinearLayout  mExtraItemsLayout;
    private View mEmailCitationButton;
    
    public BookDetailView(Activity activity, BookItem bookItem) {
    	mActivity = activity;
        mBookItem = bookItem;
        
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = (LockingScrollView) inflater.inflate(R.layout.book_detail, null);
    
        mFullScreenLoader = (FullScreenLoader) mView.findViewById(R.id.libraryWorldCatBookDetailLoader);
        mTitleTextView = (TextView) mView.findViewById(R.id.libraryWorldCatBookDetailsTitle);
        mDetailsLinearLayout = (LinearLayout) mView.findViewById(R.id.libraryWorldCatBookDetailLL);
        mExtraItemsLayout = (LinearLayout) mView.findViewById(R.id.libraryWorldCatDetailsExtraItems);
        mEmailCitationButton = mView.findViewById(R.id.libraryWorldCatEmailCitationsButton);
        
        mLoadingStatus = DetailsLoadingStatus.NotLoaded;
        

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
    	if (mLoadingStatus != DetailsLoadingStatus.Loaded) {
    		if (mBookItem.detailsLoaded) {
    			showBookDetails();
    			mLoadingStatus = DetailsLoadingStatus.Loaded;
    		} else if (mLoadingStatus != DetailsLoadingStatus.Loading) {
    			mFullScreenLoader.showLoading();
    			mLoadingStatus = DetailsLoadingStatus.Loading;
    			
    			LibraryModel.fetchWorldCatBookDetails(mBookItem, mActivity, new Handler() {
    				@Override
    				public void handleMessage(Message msg) {
    					if (msg.arg1 == MobileWebApi.SUCCESS) {
    						showBookDetails();
    						mLoadingStatus = DetailsLoadingStatus.Loaded;
    					} else {
    						mFullScreenLoader.showError();
    						mLoadingStatus = DetailsLoadingStatus.NotLoaded;
    					}
    				}
    			});
    		}
    	}
    }
    
    
    SpannableStringBuilder mSpanBuilder;
    public void showBookDetails() {
    	mFullScreenLoader.setVisibility(View.GONE);
    	
    	mTitleTextView.setText(mBookItem.title);
    	addRow(null, mBookItem.getAuthorsDisplayString());
    	if (mBookItem.format != null) {
    		addRow("Format", join(", ", mBookItem.format));
    	}
    	if (mBookItem.summary != null) {
    		addRow("Summary", join(" ", mBookItem.summary));
    	}
    	if (mBookItem.publisher != null) {
    		addRow("Publisher", join("", mBookItem.publisher));
    	}
    	if (mBookItem.editions != null) {
    		addRow("Edition", join(", ", mBookItem.editions));
    	}
    	if (mBookItem.extent != null) {
    		addRow("Description", join(" ", mBookItem.extent));
    	}
    	if (mBookItem.isbn != null) {
    		addRow("ISBN", join(" : ", mBookItem.isbn));
    	}
    	
    	mEmailCitationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
				intent.putExtra(Intent.EXTRA_SUBJECT, mBookItem.title);
				intent.putExtra(
						Intent.EXTRA_TEXT,
						Html.fromHtml(mBookItem.emailAndCiteMessage)
				);
				mActivity.startActivity(intent);
			}
    	});
    	
    	final List<Holding> mitHoldings = mBookItem.getHoldingsByOCLCCode(BookItem.MITLibrariesOCLCCode);
    	if (mitHoldings.size() > 0) {
    		mExtraItemsLayout.addView(new SectionHeader(mActivity, "MIT Libraries", Prominence.SECONDARY));

    		TwoLineActionRow requestItem = new TwoLineActionRow(mActivity);
    		requestItem.setTitle("RequestItem");
    		requestItem.setActionIconResource(R.drawable.action_external);
    		requestItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					CommonActions.viewURL(mActivity, mitHoldings.get(0).url);
				}
    		});
    		
    		mExtraItemsLayout.addView(requestItem);

    		for (Holding aMitHolding : mitHoldings) {
    			final Map<String, Availabilitys> availabilitiesByLibrary = aMitHolding.getAvailabilitys();
    			final ArrayList<String> locations = new ArrayList<String>(availabilitiesByLibrary.keySet());
    			Collections.sort(locations);
    			for (int i = 0; i < locations.size(); i++) {
    				final int index = i;
    				String location = locations.get(i);
    				Availabilitys availablitys = availabilitiesByLibrary.get(location);    	
    				
    				mExtraItemsLayout.addView(new DividerView(mActivity, null));
    				
    				TwoLineActionRow availabilityRow = new TwoLineActionRow(mActivity);
    				availabilityRow.setTitle(location);
    				availabilityRow.setSubtitle("" + availablitys.available + " of " + availablitys.total + " available");
    				
    				availabilityRow.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							LibrariesHoldingsSliderActivity.launch(mActivity, availabilitiesByLibrary, locations, index);
						}
    				});
    				
    				mExtraItemsLayout.addView(availabilityRow);
    			}
    		}
    	}
    	
    	int otherHoldingsCount = mBookItem.holdings.size() - mitHoldings.size();
    	if (otherHoldingsCount > 0) {
    		mExtraItemsLayout.addView(new SectionHeader(mActivity, "Boston Library Consortium", Prominence.SECONDARY));
			TwoLineActionRow blcHoldingsRow = new TwoLineActionRow(mActivity);
			blcHoldingsRow.setTitle("View Holdings");
			blcHoldingsRow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LibraryBLCHoldingsActivity.launch(mActivity, mBookItem);
				}
			});
			mExtraItemsLayout.addView(blcHoldingsRow);
    	}
    }

    
    public void addRow(String label, CharSequence value) {
    	Factory factory = Spannable.Factory.getInstance();
    	if (label != null) {
    		label += ": ";
    	} else {
    		label = "";
    	}
    	
    	Spannable span = factory.newSpannable(label + value);
    	if (label.length() > 0) {
    		span.setSpan(new TextAppearanceSpan(mActivity, R.style.BoldBodyText), 0, label.length()-1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    	}
    	span.setSpan(new TextAppearanceSpan(mActivity, R.style.BodyText), label.length(), span.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    	
    	TextView textView = new TextView(mActivity);
    	int padding = mActivity.getResources().getDimensionPixelSize(R.dimen.verticalPadding);
    	textView.setPadding(0, 0, 0, padding);
    	textView.setText(span);
    	
    	mDetailsLinearLayout.addView(textView);
    }
    
    private String join(String delimiter, List<String> parts) {
    	String out = "";
    	boolean isFirst = true;
    	for (String part : parts) {
    		if (isFirst) {
    			isFirst = false;
    		} else {
    			out += delimiter;
    		}
    		out += part;
    	}
    	return out;
    }
    
    @Override
    public void updateView() {

    }

}
