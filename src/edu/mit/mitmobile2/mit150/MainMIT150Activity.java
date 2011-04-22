package edu.mit.mitmobile2.mit150;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SectionHeader;

public class MainMIT150Activity extends ModuleActivity {
	
	FullScreenLoader mLoader;
	
	LinearLayout top;
	
	Context ctx;

	MIT150Model mit150model = new MIT150Model(this);
	
	public static final String TAG = "MainMIT150Activity";

	@Override
	protected void onCreate(Bundle savedInstance) {
		
		super.onCreate(savedInstance);
		Log.d(TAG, "onCreate()");
	     
		setContentView(R.layout.mit150_home);

		ctx = this;

		mLoader = (FullScreenLoader) findViewById(R.id.mit150HomeLoader);
		
		mLoader.showLoading();
		Handler mMIT150LoadedHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.arg1 == MobileWebApi.SUCCESS) {
					mLoader.setVisibility(View.GONE);
					createMoreView();
					setContent();
				} else {
					// TODO show cached?
					mLoader.showError();
				}
			}		
		};
		mit150model.fetchMIT150(ctx, mMIT150LoadedHandler);
		
	}
	/*********************************************************************/
	void setContent() {

		ImageView btn;
		TextView tv;
		RelativeLayout rl;
		FrameLayout fl;
		
		Bitmap bm;
		BitmapDrawable bd;
		LayoutParams params;

		int[] heights = new int[3];
		int[] widths = new int[3];
		
	    int h,w;
	    float scale;
	    
		MIT150FeatureItem f;

		Display display = getWindowManager().getDefaultDisplay(); 
	    int screenWidth = display.getWidth();
		
	    ArrayList<MIT150FeatureItem> fs = mit150model.getFeatures(ctx);
	    if (fs.size()<3) return;

	    //////////////////////////////////////////////////
	    // Set button images and click handlers
	    
	    int[] image_ids = {R.id.mit150WelcomeIV,R.id.mit150CorridorIV,R.id.mit150EventsIV};
	    int[] tv_ids = {R.id.mit150WelcomeTV,R.id.mit150CorridorTV,R.id.mit150EventsTV};
	    
	    for (int fx=0; fx<fs.size(); fx++) {

			f = fs.get(fx);
			
	    	bm = BitmapUtils.getRoundedCornerBitmap(this, f.bm,0);
			btn = (ImageView) findViewById(image_ids[fx]);
			bd = new BitmapDrawable(bm);
			btn.setBackgroundDrawable(bd);  

			heights[fx] = bm.getHeight();
			widths[fx] = bm.getWidth();
			
			// Textview background and colors
			tv = (TextView) findViewById(tv_ids[fx]);

			if ((f.subtitle==null) || ("".equalsIgnoreCase(f.subtitle))) {
				tv.setText(f.title);
				tv.setTextColor(f.getTitleColor() | 0xFF000000);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv.getLayoutParams();
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				tv.setLayoutParams(layoutParams);
			} else {
				setText(tv,f.title,f.subtitle,f.getTitleColor() | 0xFF000000);
				bm = BitmapUtils.createRoundedBottomBitmap(this, widths[fx], 50, f.getTintColor() | 0xA0000000);
				bd = new BitmapDrawable(bm);
				tv.setBackgroundDrawable(bd);
			}

			
			// Action
			final String url = f.url;
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonActions.doAction(ctx, url);
				}
			});
	    	
	    }
	    
	    //////////////////////////////////////////////////
		// Welcome
		f = fs.get(0);
		if (f!=null) {
			
			rl = (RelativeLayout) findViewById(R.id.mit150WelcomeFL);
			params = new LinearLayout.LayoutParams((int) (0.333*screenWidth),LayoutParams.FILL_PARENT);
			rl.setLayoutParams(params);
			
		}
		
		// Corridor
		f = fs.get(1);
		if (f!=null) {
			
			rl = (RelativeLayout) findViewById(R.id.mit150CorridorRL);;
			params = new LinearLayout.LayoutParams((int) (0.66*screenWidth),LayoutParams.FILL_PARENT);
			rl.setLayoutParams(params);
		
			// FIXME
			// U+25B9	WHITE RIGHT-POINTING SMALL TRIANGLE
			//String rightArrow = "<html><body>&#x25B9;</body></html>";
			//tv.setText(Html.fromHtml(rightArrow).toString());	
			
			//Typeface tf;
			//tf = Typeface.createFromAsset(getAssets(), "fonts/marvosym.ttf");
			//tv.setTypeface(tf);
			//tv.setText("\u2219\u00B7");	

		}
		
		//////////////////////////////////
		// Adjust layout

		h = (int) Math.round((float)heights[0] / (float) widths[0] * 0.33 * (float)screenWidth);
		LinearLayout ll = (LinearLayout) findViewById(R.id.mit150FirstRowLL);
		params = new LinearLayout.LayoutParams(screenWidth,h);
		ll.setLayoutParams(params);

		
		scale = (float) screenWidth / (float) widths[2];	
		h = (int) (scale * (float) heights[2]);
		w = (int) (scale * (float) widths[2]);
		
		
		rl = (RelativeLayout) findViewById(R.id.mit150EventsRL);
		params = new LinearLayout.LayoutParams(w,h+7);
		rl.setLayoutParams(params);
	    
		
		top = (LinearLayout) findViewById(R.id.mit150Top);
		top.requestLayout();
		top.invalidate();


		// Scroll to top of screen
		final ScrollView sv = (ScrollView) findViewById(R.id.mit150SV);
		sv.post(new Runnable() {
			@Override
			public void run() {
				sv.scrollTo(0, 0);
			}
		});
		
	}
	/*********************************************************************/
	void createMoreView() {

		LayoutParams lp;

		LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		
		int height;

		top = (LinearLayout) findViewById(R.id.mit150Top);
		
		for (MIT150MoreFeaturesItem mf : mit150model.more_features) {

			// Section...
			
			lp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
			
			SectionHeader sh = new SectionHeader(this,mf.section_title);
			sh.setLayoutParams(lp);
			sh.setPadding(0, 7, 0, 0);
			
			top.addView(sh);
			
			// List...
			
			final ArrayList<MIT150MoreItem> items = mf.items;
			
			ListView lv = new ListView(this);
			
			lv = (ListView) vi.inflate(R.layout.mit150_lv, null);
			
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					MIT150MoreItem mi = items.get(position);
					CommonActions.doAction(ctx, mi.url);
				}
			});
			
			
			MIT150MoreAdapter mma = new MIT150MoreAdapter(ctx, 0, items);

			int row_height = BitmapUtils.convertDipsToPixels(ctx, 75);
			
			height = items.size()*row_height;
			
			lp = new LayoutParams(LayoutParams.FILL_PARENT,height);
			lv.setLayoutParams(lp);
			lv.setAdapter(mma);

			top.addView(lv);
		
		}
	
		
	}
	/*********************************************************************/
	void setText(TextView tv, String title, String body, int color) {
		
		String all = title + "\n"+ body;
		tv.setText(all, TextView.BufferType.SPANNABLE);
		Spannable str = (Spannable) tv.getText();
		int len = title.length();
		
		str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		str.setSpan(new ForegroundColorSpan(color), 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		str.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), len+1, all.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		str.setSpan(new ForegroundColorSpan(0xFFFFFFFF), len+1, all.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
	}
	/*********************************************************************/
	@Override
	protected Module getModule() {
		return new MIT150Module();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		
	}
	
}










