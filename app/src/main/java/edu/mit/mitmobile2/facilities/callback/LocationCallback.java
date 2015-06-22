package edu.mit.mitmobile2.facilities.callback;


import java.util.HashSet;

public interface LocationCallback {
    void fetchPlacesByCategories(String name, HashSet<String> locations);

    void fetchPlace(String name);
}
