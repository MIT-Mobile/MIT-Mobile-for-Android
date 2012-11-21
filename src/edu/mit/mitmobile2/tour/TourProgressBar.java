package edu.mit.mitmobile2.tour;

import edu.mit.mitmobile2.R;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TourProgressBar extends LinearLayout {

	private Context ctx;
	
	private int total = 0;
	@SuppressWarnings("unused")
	private int done = 0;
	
	/*****************************************************************************/
	public TourProgressBar(Context context) {
		
		super(context);
		
		ctx = context;
		
		//LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		//LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,10);  // should be this but hdpi disappears
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,20);  // works for hdpi but not mdpi
		
		//lp.setMargins(6, 0, 6, 2);  // this does not extend background but does push segments inside
		
		setLayoutParams(lp);

		// margin is outside, padding is inside 
		
		setPadding(6, 0, 6, 2);  // no effect??

		setOrientation(LinearLayout.HORIZONTAL);

		setBackgroundResource(R.drawable.progress_bkgrd);

	}
	/*****************************************************************************/
	void init(int total, int done) {

		if (done>=total) return;
		
		this.done = done;
		this.total = total;
		
		this.removeAllViews();
		
		ImageView img;
		LayoutParams lp;
		
		for (int x=0; x<total; x++) {
			
			img = new ImageView(ctx);
			
			//lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, 1);
			lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT, 1);  // if parent has fixed height
			if (x==0) lp.setMargins(6, 0, 0, 2);
			else if (x==total-1) lp.setMargins(0, 0, 6, 2);
			else lp.setMargins(0, 0, 0, 2);
			img.setLayoutParams(lp);
			
			//img.setBackgroundResource(android.R.color.transparent);  // otherwise gray background??
			img.setImageResource(android.R.color.transparent);
			
			if (x<done) {
				//img.setImageResource(R.drawable.progress_segmentpast);  
				img.setBackgroundResource(R.drawable.progress_segmentpast);  // do 9-patches work only in backgrounds?
			} else if (x==done) {
				//img.setImageResource(R.drawable.progress_current);
				img.setBackgroundResource(R.drawable.progress_current);
			} else {
				//img.setImageResource(R.drawable.progress_trench);
				img.setBackgroundResource(R.drawable.progress_trench);
			}
			this.addView(img);
			
		}
		
		requestLayout();
		
		invalidate();
		
	}
	
	void setProgress(int done) {
		this.done = done;
		
		for(int x=0; x < this.total; x++) {
			ImageView img = (ImageView) getChildAt(x);
			
			if (x<done) {
				img.setBackgroundResource(R.drawable.progress_segmentpast);
			} else if (x==done) {
				img.setBackgroundResource(R.drawable.progress_current);
			} else {
				img.setBackgroundResource(R.drawable.progress_trench);
			}
		}	
	}
}
