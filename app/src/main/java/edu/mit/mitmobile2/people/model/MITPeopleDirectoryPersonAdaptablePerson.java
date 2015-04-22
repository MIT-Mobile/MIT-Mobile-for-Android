package edu.mit.mitmobile2.people.model;

/**
 * Created by grmartin on 4/20/15.
 */
public interface MITPeopleDirectoryPersonAdaptablePerson extends MITPeopleDirectoryPersonAdaptable {
    String getName();
    String getTitle();
    String valueForIndexPath(MITPersonIndexPath indexPath);
}
