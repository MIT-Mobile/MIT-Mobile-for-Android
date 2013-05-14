package edu.mit.mitmobile2.dining;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderView.ScreenPosition;

class DiningComparisionSliderAdapter implements SliderView.Adapter {

	private Context mContext;
	public DiningComparisionSliderAdapter(Context context) {
		mContext = context;
	}
	
	int mCurrentPosition = 0;
	@Override
	public boolean hasScreen(ScreenPosition screenPosition) {
		return true;
	}

	@Override
	public View getScreen(ScreenPosition screenPosition) {
		int position = mCurrentPosition;
		switch (screenPosition) {
			case Previous:
				position--;
				break;
			
			case Next:
				position++;
				break;
				default:		
		}
		int r = 991 * (349 + position) % 256;
		int g = 991 * (349 + position + 3) % 256;
		int b = 991 * (349 + position + 5) % 256;
		
		ScrollView scrollView = new ScrollView(mContext);
		scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		scrollView.setBackgroundColor(Color.rgb(r, g, b));
		TextView view = new TextView(mContext);
		view.setText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
		view.setBackgroundColor(Color.rgb(r, g, b));
		scrollView.addView(view, new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, 200));
		return scrollView;
	}

	@Override
	public void destroyScreen(ScreenPosition screenPosition) { }

	@Override
	public void seek(ScreenPosition screenPosition) {
		switch (screenPosition) {
			case Previous:
				mCurrentPosition--;
				break;
				
			case Next:
				mCurrentPosition++;
				break;
			default:		
		}
	}

	@Override
	public void destroy() { }
	
	
}