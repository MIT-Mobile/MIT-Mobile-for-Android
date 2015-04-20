package edu.mit.mitmobile2.people.model;

import android.support.annotation.NonNull;

/**
 * Created by grmartin on 4/20/15.
 */
public class MITPersonIndexPath {
    public static final int NO_INDEX = Integer.MIN_VALUE;

    private final MITPersonAttribute attributeType;
    private final int index;

    public MITPersonIndexPath(@NonNull MITPersonAttribute attributeType, int index) {
        this.attributeType = attributeType;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public MITPersonAttribute getAttributeType() {
        return attributeType;
    }

    @Override
    public String toString() {
        return "MITPersonIndexPath{" +
                "attributeType=" + attributeType +
                ", index=" + (index == NO_INDEX ? "NO_INDEX" : index) +
                '}';
    }
}
