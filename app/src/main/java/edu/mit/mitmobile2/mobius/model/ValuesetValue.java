package edu.mit.mitmobile2.mobius.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sseligma on 4/13/15.
 */
public class ValuesetValue {
    public String _id;
    public String text;
    public String value;

    public ValuesetValue(JSONObject jsonObject) {
        try {
            this._id = jsonObject.getString("_id");
            this.text = jsonObject.getString("text");
            this.value = jsonObject.getString("value");
        }
        catch (JSONException e) {

        }
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
