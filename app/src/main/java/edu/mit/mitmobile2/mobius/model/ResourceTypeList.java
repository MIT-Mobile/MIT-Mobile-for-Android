package edu.mit.mitmobile2.mobius.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;

/**
 * Created by sseligma on 4/4/15.
 */
public class ResourceTypeList extends ArrayList<ResourceType> {

    public ResourceTypeList(JSONArray jArray) {
        if (jArray != null) {
            try {
                for (int i = 0; i < jArray.length(); i++) {
                    this.add(new ResourceType(jArray.getJSONObject(i)));
                }
            }
            catch (JSONException e) {
                Timber.d(e.getMessage());
            }
        }

    }
}
