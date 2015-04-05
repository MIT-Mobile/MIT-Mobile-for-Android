package edu.mit.mitmobile2.mobius.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import edu.mit.mitmobile2.APIJsonResponse;
import timber.log.Timber;

/**
 * Created by sseligma on 4/4/15.
 */
public class RoomsetList extends ArrayList<Roomset> {

    public RoomsetList(JSONArray jArray) {
        if (jArray != null) {
            try {
                for (int i = 0; i < jArray.length(); i++) {
                    this.add(new Roomset(jArray.getJSONObject(i)));
                }
            }
            catch (JSONException e) {
                Timber.d(e.getMessage());
            }
        }

    }
}
