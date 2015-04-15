package edu.mit.mitmobile2.mobius.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceAttribute implements Parcelable {
    private String _id;
    private String _attribute;
    private String label;
    private String[] value;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_attribute() {
        return _attribute;
    }

    public void set_attribute(String _attribute) {
        this._attribute = _attribute;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this._attribute);
        dest.writeString(this.label);
        dest.writeStringArray(this.value);
    }


    public ResourceAttribute(JSONObject jsonObject) {
        super();
        try {
            this.set_id(jsonObject.getString("_id"));
            // Attribute
            if (jsonObject.has("_attribute") && !jsonObject.isNull("_attribute")) {
                JSONObject jAttribute = jsonObject.getJSONObject("_attribute");
                this.set_attribute(jAttribute.getString("_id"));
                this.setLabel(jAttribute.getString("label"));
            }

            // Values
            JSONArray jValue = jsonObject.getJSONArray("value");
            this.value = new String[jValue.length()];
            for (int v = 0; v < jValue.length(); v++) {
                this.value[v] = jValue.getString(v);
            }
        }
        catch (JSONException e) {
            Timber.d(e.getMessage());
        }
    }

    private ResourceAttribute(Parcel in) {
        this._id = in.readString();
        this._attribute = in.readString();
        this.label = in.readString();
        this.value = in.createStringArray();
    }

    public static final Creator<ResourceAttribute> CREATOR = new Creator<ResourceAttribute>() {
        public ResourceAttribute createFromParcel(Parcel source) {
            return new ResourceAttribute(source);
        }

        public ResourceAttribute[] newArray(int size) {
            return new ResourceAttribute[size];
        }
    };
}