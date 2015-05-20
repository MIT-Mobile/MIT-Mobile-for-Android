package edu.mit.mitmobile2.libraries.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesAskUsModel {

    @Expose
    private List<Object> topics;                // TODO: clarify proper type

    @Expose
    private List<Object> consultationLists;     // TODO: clarify proper type

}
