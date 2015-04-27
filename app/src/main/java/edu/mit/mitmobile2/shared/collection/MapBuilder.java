package edu.mit.mitmobile2.shared.collection;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by grmartin on 4/27/15.
 */
public class MapBuilder<MT> {
    Map<Object, Object> map;

    @SuppressWarnings("unchecked")
    public MapBuilder(Map<?,?> map) {
        this.map = (Map<Object, Object>)map;
    }

    public MapBuilder() {
        this(new Hashtable<Object,Object>());
    }

    public MapBuilder<MT> add(Object key, Object value) {
        this.map.put(key, value);
        return this;
    }

    public MapBuilder<MT> put(Object key, Object value) {
        this.map.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public MT object() {
        return (MT)this.map;
    }
}