package edu.mit.mitmobile2;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.widget.ImageView;

public class LoadingUIHelper {
	
	public static void startLoadingImage(Handler handler, ImageView view) {
		final AnimationDrawable animation = (AnimationDrawable) view.getDrawable();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				animation.start();
			}	
		}, 500);
	}
	
	public static void stopLoadingImage(Handler handler, ImageView view) {
		final AnimationDrawable animation = (AnimationDrawable) view.getDrawable();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				animation.stop();
			}	
		}, 500);
	}

}
