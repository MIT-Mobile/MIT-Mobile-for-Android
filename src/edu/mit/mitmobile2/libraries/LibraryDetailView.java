package edu.mit.mitmobile2.libraries;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.TwoLineActionRow;

public class LibraryDetailView implements SliderInterface {

    private Activity mActivity;
    private LibraryItem mLibraryItem;
    private LockingScrollView mView;
    
    private FullScreenLoader mLoaderView;
    private TwoLineActionRow mDetailTitle;
    private TwoLineActionRow mDetailPhone;
    private View mDetailPhoneDivider;
    private TwoLineActionRow mDetailRoom;
    private View mDetailRoomDivider;
    private TextView mDetailInfo;

    public LibraryDetailView(Activity activity, LibraryItem libraryItem) {

        mActivity = activity;
        mLibraryItem = libraryItem;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = (LockingScrollView) inflater.inflate(R.layout.library_detail, null);

        mLoaderView = (FullScreenLoader) mView.findViewById(R.id.libraryDetailLoading);

        mDetailTitle = (TwoLineActionRow) mView.findViewById(R.id.libraryDetailTitle);
        mDetailTitle.setTitle(mLibraryItem.library);

        mDetailPhone = (TwoLineActionRow) mView.findViewById(R.id.libraryDetailPhone);
        ImageView phoneImage = (ImageView) mDetailPhone.findViewById(R.id.simpleRowActionIcon);
        phoneImage.setImageResource(R.drawable.action_phone);
        mDetailPhone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CommonActions.callPhone(mActivity, mLibraryItem.tel);
            }
        });
        mDetailPhoneDivider = mView.findViewById(R.id.libraryDetailPhoneDivider);

        mDetailRoom = (TwoLineActionRow) mView.findViewById(R.id.libraryDetailRoom);
        ImageView mapImage = (ImageView) mDetailRoom.findViewById(R.id.simpleRowActionIcon);
        mapImage.setImageResource(R.drawable.action_map);
        mDetailRoom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CommonActions.searchMap(mActivity, mLibraryItem.location);
            }
        });
        mDetailRoomDivider = mView.findViewById(R.id.libraryDetailRoomDivider);
        
        mDetailInfo = (TextView) mView.findViewById(R.id.libraryDetailInfo);
        
        if(mLibraryItem.isDetailLoaded) {
        	loadDetails();
        }
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
        if (mLibraryItem.isDetailLoaded) {
            return;
        }

//        mLoaderView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mLoaderView.setVisibility(View.VISIBLE);
        mLoaderView.showLoading();
        LibraryModel.fetchLibraryDetail(mLibraryItem, mActivity, new Handler() {

            @Override
            public void handleMessage(Message message) {

                if (message.arg1 == MobileWebApi.SUCCESS) {
                    loadDetails();
                } else {
                    mLoaderView.showError();
                }
            }
        });

    }

    private void loadDetails() {
        mLoaderView.setVisibility(View.GONE);
        mDetailPhone.setTitle(mLibraryItem.tel);
        mDetailPhone.setVisibility(View.VISIBLE);
        mDetailPhoneDivider.setVisibility(View.VISIBLE);
      
        mDetailRoom.setTitle("Room " + mLibraryItem.location);
        mDetailRoom.setVisibility(View.VISIBLE);
        mDetailRoomDivider.setVisibility(View.VISIBLE);
        
        mDetailInfo.setText(composeDetailInfo(), TextView.BufferType.SPANNABLE);
        mDetailInfo.setVisibility(View.VISIBLE);   	
    }
    
    @Override
    public void updateView() {
    }

    private Spannable composeDetailInfo() {
    	
    	SpannableStringBuilder builder = new SpannableStringBuilder();
    	builder.append(bold("Today's Hours: "));
    	builder.append(normal(mLibraryItem.hoursToday));
    	builder.append("\n\n");
    	
    	String hoursTitle = mLibraryItem.currentTerm.name + " Hours (";
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        String start = formatter.format(mLibraryItem.currentTerm.range_start);
        String end = formatter.format(mLibraryItem.currentTerm.range_end);
        hoursTitle += start + "-" + end + "):";
 
        builder.append(bold(hoursTitle.trim() + "\n"));
    	
        for (String key : mLibraryItem.currentTerm.hours.keySet()) {
        	String aDetailLine = key + " " + mLibraryItem.currentTerm.hours.get(key) + "\n";
        	builder.append(normal(aDetailLine));
        }
    	
    	return builder;
    	
    }

    private Spannable bold(String text) {
    	Spannable span = new SpannableString(text);
    	span.setSpan(new TextAppearanceSpan(mActivity, R.style.ListItemPrimary), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	return span;
    }
    
    private Spannable normal(String text) {
    	Spannable span = new SpannableString(text);
    	span.setSpan(new TextAppearanceSpan(mActivity, R.style.ListItemSecondary), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	return span;
    }
}
