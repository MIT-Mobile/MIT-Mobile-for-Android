package edu.mit.mitmobile2.mobius.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;

/**
 * Created by sseligma on 4/4/15.
 */
public class ResourceType extends ArrayList<Roomset> {

    String _id;
    String type;
    String _category;
    String category_name;

    public ResourceType(JSONObject data) {
        try {
            this._id = data.optString("_id", "");
            this.type = data.optString("type", "");
            this._category = data.getJSONObject("category").optString("_id", "");
            this.category_name = data.getJSONObject("category").optString("category", "");
        }
        catch (JSONException e) {
            Timber.d(e.getMessage());
        }
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String get_category() {
        return _category;
    }

    public void set_category(String _category) {
        this._category = _category;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
