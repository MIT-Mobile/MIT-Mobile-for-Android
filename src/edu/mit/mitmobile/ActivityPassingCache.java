package edu.mit.mitmobile;

import java.util.HashMap;

public class ActivityPassingCache<T> {
	
	private HashMap<Long, T> cacheValues = new HashMap<Long, T>();
	
	public long put(T value) {
		long timeKey = System.currentTimeMillis();
		cacheValues.put(timeKey, value);
		return timeKey;
	}
	
	public T get(long key) {
		return cacheValues.get(key);
	}
	
	

}
