package edu.mit.mitmobile2;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

public class SmallActivityCache<T> extends LinkedHashMap<Long, SoftReference<T>> {
	
	private static int MAXIMUM_CACHE_SIZE = 5;
	private static final long serialVersionUID = 1L;
	
	@Override
	protected boolean removeEldestEntry(Map.Entry<Long, SoftReference<T>> eldest) {
		return (size() > MAXIMUM_CACHE_SIZE);
	}
	
	public long put(T value) {
		long key = System.currentTimeMillis();
		super.put(key, new SoftReference<T>(value));
		return key;
	}
	
	public T getItem(Long key) {
		if(containsKey(key)) {
			SoftReference<T> softRef = super.get(key);  			
			T value = softRef.get();
			if (value != null) {
				// push this value to the front
				remove(key);
				super.put(key, softRef);
			}
			return value;
		}
		
		return null;
	}

}
