package edu.mit.mitmobile2.shared.collection;

import java.util.HashMap;

/**
 * Created by grmartin on 4/27/15.
 */
public class FluentMap<K,V> extends MapBuilder<HashMap<K,V>> {
    public FluentMap() {
        super(new HashMap<K, V>());
    }
}
