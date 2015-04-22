package edu.mit.mitmobile2.shared.model;

/**
 * Created by grmartin on 4/20/15.
 */
public class MITSimpleTaggedActionItem {
    private final String name;
    private final int tag;

    public MITSimpleTaggedActionItem(String name, int tag) {
        this.name = name;
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MITSimpleTaggedActionItem{" +
                "name='" + name + '\'' +
                ", tag=" + tag +
                '}';
    }
}
