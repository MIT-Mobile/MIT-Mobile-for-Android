package edu.mit.mitmobile2.dining;

import java.io.ObjectInputStream.GetField;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderView.Adapter;
import edu.mit.mitmobile2.SliderView.OnSeekListener;
import edu.mit.mitmobile2.SliderView.ScreenPosition;
import edu.mit.mitmobile2.dining.DiningModel.DiningDietaryFlag;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

public class PortraitDiningScheduleScreen extends DiningScheduleScreen {
	
	protected DiningVenues mVenues;
	protected HouseDiningHall mSelectedHouse;
	protected GregorianCalendar mInitialDate;
	
	LinearLayout mMainLayout;
	protected LinearLayout mFiltersContainer;
	private Context mContext;
	private DiningHouseScheduleSliderAdapter mDiningAdapter;
	private SliderView mSliderView;

	public PortraitDiningScheduleScreen(DiningVenues venues, HouseDiningHall selectedHouse, GregorianCalendar initialDate) {
		mVenues = venues;
		mSelectedHouse = selectedHouse;
		mInitialDate = initialDate;
	}
	
	@Override
	public View initializeView(Context context) {
		mContext = context;
		mMainLayout = new LinearLayout(context);
		mMainLayout.setOrientation(LinearLayout.VERTICAL);
		
		addHeader();
		addSliderView();
		
		return mMainLayout;
	}
	
	protected LayoutInflater getLayoutInflater() {
		 LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 return inflater;
	}
	
	protected void addHeader() {
		DiningHallHeaderView headerView = new DiningHallHeaderView(mContext, mSelectedHouse, DiningModel.currentTimeMillis());
		headerView.setActionImageResourceId(R.drawable.menu_info);
		headerView.setActionClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, DiningHouseInfoActivity.class);
				i.putExtra("hall_id", mSelectedHouse.getID());
				mContext.startActivity(i);
			}			
		});
		mMainLayout.addView(headerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	protected View getAppliedFiltersView() {
		
		LayoutInflater inflater = getLayoutInflater();
		List <DiningDietaryFlag> appliedFilters = DiningDietaryFlag.loadFilters(mContext);
		// filter header
		float density = mContext.getResources().getDisplayMetrics().density;
		int iconSquare = (int)Math.ceil(18 * density);
		int iconMargin = (int)Math.ceil(7 * density);
		Log.d("MARGIN", iconMargin + "");
		if (!appliedFilters.isEmpty()) {
			View filterHeader = inflater.inflate(R.layout.dining_filter_header, null);
			LinearLayout iconContainer = (LinearLayout)filterHeader.findViewById(R.id.filterIdContainer);
			LayoutParams params = new LayoutParams(iconSquare, iconSquare);
			params.setMargins(iconMargin, 0, 0, 0);
			for (DiningDietaryFlag flag : appliedFilters) {
				ImageView iconView = new ImageView(mContext);
				iconView.setLayoutParams(params);
				iconView.setBackgroundResource(flag.getIconId());
				iconContainer.addView(iconView);
			}
			return filterHeader;
		}
		return null;
	}
	
	protected void addSliderView() {
		
		LayoutInflater inflater = getLayoutInflater();
		View diningSliderBar = inflater.inflate(R.layout.dining_slider_bar, null);
		final View leftArrow = diningSliderBar.findViewById(R.id.diningSliderLeftArrow);
		final View rightArrow = diningSliderBar.findViewById(R.id.diningSliderRightArrow);
		final TextView sliderTitle = (TextView) diningSliderBar.findViewById(R.id.diningSliderTitle);
		mMainLayout.addView(diningSliderBar);
		
		LinearLayout container = new LinearLayout(mContext);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mFiltersContainer = container;
		
		View appliedFilters = getAppliedFiltersView();
		if (appliedFilters != null) {
			mFiltersContainer.addView(appliedFilters);
			mFiltersContainer.addView(new DividerView(mContext, null));
		}
		
		mMainLayout.addView(mFiltersContainer);
		
		mSliderView = new SliderView(mContext);
		mMainLayout.addView(mSliderView);
		
		mDiningAdapter = new DiningHouseScheduleSliderAdapter(mContext, mSelectedHouse, mInitialDate.getTimeInMillis());
		mSliderView.setAdapter(mDiningAdapter);
		leftArrow.setEnabled(mDiningAdapter.hasScreen(ScreenPosition.Previous));
		rightArrow.setEnabled(mDiningAdapter.hasScreen(ScreenPosition.Next));
		sliderTitle.setText(mDiningAdapter.getCurrentTitle());
		
		leftArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSliderView.slideLeft();
			}			
		});
		rightArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSliderView.slideRight();
			}			
		});
		
		mSliderView.setOnSeekListener(new OnSeekListener() {

			@Override
			public void onSeek(SliderView view, Adapter adapter) {
				leftArrow.setEnabled(adapter.hasScreen(ScreenPosition.Previous));
				rightArrow.setEnabled(adapter.hasScreen(ScreenPosition.Next));
				sliderTitle.setText(mDiningAdapter.getCurrentTitle());
			}			
		});
	}

	@Override
	public Calendar getSelectedDate(HouseDiningHall house) {
		return mDiningAdapter.getSelectedDate(house);
	}
	
	@Override
	public void refreshScreen() {
		mSliderView.refreshScreens();
		refreshAppliedFilters();
	}
	
	public void refreshAppliedFilters() {
		mFiltersContainer.removeAllViews();
		View appliedFilters = getAppliedFiltersView();
		if (appliedFilters != null) {
			mFiltersContainer.addView(appliedFilters);
			mFiltersContainer.addView(new DividerView(mContext, null));
		}
	}
}

