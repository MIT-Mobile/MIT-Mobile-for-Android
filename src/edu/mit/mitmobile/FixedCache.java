package edu.mit.mitmobile;

import java.util.LinkedHashMap;
import java.util.Map;


public class FixedCache<T> extends LinkedHashMap<String, T> {
	
	private static final long serialVersionUID = 1L;
	
	private int mMaximum;
	
	public FixedCache(int maximum) {
		mMaximum = maximum;
	}
	
	@Override
	protected boolean removeEldestEntry(Map.Entry<String, T> eldest) {
		return (size() > mMaximum);
	}
	
	@Override
	public T put(String key, T value) {
		if(containsKey(key)) {
			remove(value);
		}
		
		return super.put(key, value);
	}
	
	@Override 
	public T get(Object key) {
		if(containsKey(key)) {
			T value = super.get(key);
			// push this value to the front
			put((String) key, value);
			return value;
		}
		
		return null;
	}
}
