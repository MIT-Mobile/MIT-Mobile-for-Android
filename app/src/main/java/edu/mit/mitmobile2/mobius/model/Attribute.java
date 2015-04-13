package edu.mit.mitmobile2.mobius.model;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Created by sseligma on 4/13/15.
 */
public class Attribute {
    String _id;
    String label;
    String field_type;
    String widget_type;
    ValueSet _valueset;

    public Attribute(JSONObject jsonObject) {
        try {
            Timber.d("label = " + jsonObject.optString("label"));
            Timber.d("widget_type = " + jsonObject.optString("widget_type"));
            _id = jsonObject.getString("_id");
            label = jsonObject.getString("label");
            field_type = jsonObject.getString("field_type");
            widget_type = jsonObject.getString("widget_type");
            if (field_type.equalsIgnoreCase("options")) {
                if (jsonObject.has("_valueset") && !jsonObject.isNull("_valueset")) {
                    Timber.d("_valueset for " + jsonObject.optString("label") + " is not null");
                    _valueset = new ValueSet(jsonObject.getJSONObject("_valueset"));
                }
            }
        }
        catch (JSONException e) {
            Timber.d(e.getMessage());
        }
        Timber.d("after constructor widget_type ="  + widget_type);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public String getWidget_type() {
        return widget_type;
    }

    public void setWidget_type(String widget_type) {
        this.widget_type = widget_type;
    }

    public ValueSet get_valueset() {
        return _valueset;
    }

    public void set_valueset(ValueSet _valueset) {
        this._valueset = _valueset;
    }

}
