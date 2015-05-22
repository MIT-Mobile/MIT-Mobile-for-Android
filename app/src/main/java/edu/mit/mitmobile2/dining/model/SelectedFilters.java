package edu.mit.mitmobile2.dining.model;


import java.util.Set;

public class SelectedFilters {

    public enum Filters {
        FARM_TO_FORK,
        FOR_YOUR_WELL_BEING,
        HALAL,
        HUMANE,
        IN_BALANCE,
        KOSHER,
        MADE_WITHOUT_GLUTEN,
        ORGANIC,
        SEAFOOD_WATCH,
        VEGAN,
        VEGETARIAN
    }
    private Set<Filters> filtersSet;

    public SelectedFilters() {

    }

    public Set<Filters> getFiltersSet() {
        return filtersSet;
    }

    public void setFiltersSet(Set<Filters> filtersSet) {
        this.filtersSet = filtersSet;
    }

    public void toggleFilter(Filters filter) {
        if (filtersSet.contains(filter)) {
            filtersSet.remove(filter);
        } else {
            filtersSet.add(filter);
        }
    }
}