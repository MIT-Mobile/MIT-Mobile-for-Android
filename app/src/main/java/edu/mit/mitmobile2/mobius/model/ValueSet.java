package edu.mit.mitmobile2.mobius.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sseligma on 4/13/15.
 */
public class ValueSet {
    String _id;
    String value_set;
    ArrayList<ValuesetValue> values;

    public ValueSet(JSONObject jsonObject) {
        values = new ArrayList<ValuesetValue>();
        try {
            this._id = jsonObject.getString("_id");
            this.value_set = jsonObject.getString("value_set");
            JSONArray jValues = jsonObject.getJSONArray("values");
            for (int i = 0; i < jValues.length(); i++) {
                values.add(new ValuesetValue(jValues.getJSONObject(i)));
            }

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

    public String getValue_set() {
        return value_set;
    }

    public void setValue_set(String value_set) {
        this.value_set = value_set;
    }

    public ArrayList<ValuesetValue> getValues() {
        return values;
    }

    public void setValues(ArrayList<ValuesetValue> values) {
        this.values = values;
    }
}

