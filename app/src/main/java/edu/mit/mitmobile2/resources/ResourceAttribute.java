package edu.mit.mitmobile2.resources;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.PrivateKey;
import java.util.ArrayList;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceAttribute implements Parcelable {
    private String _attribute;
    private String label;
    private ArrayList<String> value;
    private String value_id;

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

    public ArrayList getValue() {
        return value;
    }

    public void setValue(ArrayList value) {
        this.value = value;
    }

    public String getValue_id() {
        return value_id;
    }

    public void setValue_id(String value_id) {
        this.value_id = value_id;
    }

    public ResourceAttribute(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._attribute);
        dest.writeString(this.label);
        dest.writeSerializable(this.value);
        dest.writeString(this.value_id);
    }

    private ResourceAttribute(Parcel in) {
        this._attribute = in.readString();
        this.label = in.readString();
        this.value = (ArrayList<String>) in.readSerializable();
        this.value_id = in.readString();
    }

    public static final Parcelable.Creator<ResourceAttribute> CREATOR = new Parcelable.Creator<ResourceAttribute>() {
        public ResourceAttribute createFromParcel(Parcel source) {
            return new ResourceAttribute(source);
        }

        public ResourceAttribute[] newArray(int size) {
            return new ResourceAttribute[size];
        }
    };
}