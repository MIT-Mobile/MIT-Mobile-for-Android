package edu.mit.mitmobile;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

public class AnimationHelper {
	
	static int DURATION = 650;
	static int OUT_DURATION = 300;
	
	//for the previous movement
	public static Animation inFromRightAnimation() {

    	Animation inFromRight = new TranslateAnimation(
    	Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
    	Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
    	);
    	inFromRight.setDuration(DURATION);
    	inFromRight.setInterpolator(new AccelerateDecelerateInterpolator());
    	return inFromRight;
    	}
	
    public static Animation outToLeftAnimation() {
    	Animation outtoLeft = new TranslateAnimation(
    	 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
    	 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
    	);
    	outtoLeft.setDuration(OUT_DURATION);
    	outtoLeft.setInterpolator(new AccelerateInterpolator());
    	return outtoLeft;
    	}
    
    // for the next movement
    public static Animation inFromLeftAnimation() {
    	Animation inFromLeft = new TranslateAnimation(
    	Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
    	Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
    	);
    	inFromLeft.setDuration(DURATION);
    	inFromLeft.setInterpolator(new AccelerateDecelerateInterpolator());
    	return inFromLeft;
    	}
    
    public static Animation outToRightAnimation() {
    	Animation outtoRight = new TranslateAnimation(
    	 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
    	 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
    	);
    	outtoRight.setDuration(OUT_DURATION);
    	outtoRight.setInterpolator(new AccelerateInterpolator());
    	return outtoRight;
    	}

    public static Animation fadeIn() {
    	Animation fadein = new AlphaAnimation(0.0f, +1.0f);
    	fadein.setDuration(DURATION);
    	fadein.setInterpolator(new DecelerateInterpolator());  // start quickly...
    	return fadein;
    	}
    

    public static Animation fadeOut() {
    	Animation fadeout = new AlphaAnimation(+1.0f, 0.0f);
    	fadeout.setDuration(DURATION);
    	fadeout.setInterpolator(new AccelerateInterpolator());
    	return fadeout;
    	}    
}
