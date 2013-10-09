package edu.mit.mitmobile2.objs;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;

public abstract class MapUpdater {

	Context context;
	HashMap<String,Object> params;
	Handler handler;

	
	public abstract void updateMap(Context mContext);
	
	public void init(Context mContext,HashMap<String,Object> mParams, Handler mHandler) {
		this.context = mContext;
		this.params = mParams;
		this.handler = mHandler;
	}

	public abstract void stop(); 
}
