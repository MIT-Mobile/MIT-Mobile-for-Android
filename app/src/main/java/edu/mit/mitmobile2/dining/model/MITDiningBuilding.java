package edu.mit.mitmobile2.dining.model;

/**
 * Created by serg on 5/15/15.
 */
public class MITDiningBuilding {

    public static final int TYPE_FAVORITES = 0;
    public static final int TYPE_NAMED = 1;
    public static final int TYPE_OTHER = 2;

    private String name;
    private String sortableName;
    private int type;

    public MITDiningBuilding() {
        // empty constructor
    }

    public MITDiningBuilding(int type) {
        this.type = type;
    }

    public MITDiningBuilding(String name, String sortableName) {
        this.name = name;
        this.sortableName = sortableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortableName() {
        return sortableName;
    }

    public void setSortableName(String sortableName) {
        this.sortableName = sortableName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MITDiningBuilding building = (MITDiningBuilding) o;

        if (type != building.type) return false;
        if (name != null ? !name.equals(building.name) : building.name != null) return false;
        return !(sortableName != null ? !sortableName.equals(building.sortableName) : building.sortableName != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (sortableName != null ? sortableName.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }
}
