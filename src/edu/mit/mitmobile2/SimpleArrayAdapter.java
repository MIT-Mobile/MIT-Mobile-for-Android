package edu.mit.mitmobile2;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public abstract class SimpleArrayAdapter<T> extends ArrayAdapter<T> {
	protected Context mContext;
	protected int mRowResourceId;
	boolean mHasHeader;
	
	public SimpleArrayAdapter(Context context, List<T> items, int rowResourceId) {
		super(context, 0, 0, items);
		mContext = context;
		mRowResourceId = rowResourceId;
		mHasHeader = false;
	}
	
	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(mRowResourceId, null);
		}
		
		final T item = getItem(position);
		updateView(item, convertView);
		
		return convertView;
	}
		
	public abstract void updateView(T item, View view);
	
	public static interface OnItemClickListener<T> {
		public void onItemSelected(T item);
	}
	
	public void setOnItemClickListener(AdapterView<?> adapterView, final OnItemClickListener<T> listener) {
		adapterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if(mHasHeader) {
					position = position - 1;
				}
				
				T item = getItem(position);
				listener.onItemSelected(item);
			}
		});
	}
	
	public void setHasHeader(boolean hasHeader) {
		mHasHeader = true;
	}
}
