package edu.mit.mitmobile2.people.model;

/**
 * Created by grmartin on 4/20/15.
 */
public interface MITPeopleDirectoryPersonAdaptableSurrogate extends MITPeopleDirectoryPersonAdaptable {
    String getName();
    String getValue();
    MITPersonAttribute getAttributeType();
}
