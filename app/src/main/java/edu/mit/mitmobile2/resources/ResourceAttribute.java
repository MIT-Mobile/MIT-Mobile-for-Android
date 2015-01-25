package edu.mit.mitmobile2.resources;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceAttribute implements Parcelable {
    private String _id;
    private String _attribute;
    private String label;
    private String[] value;

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String get_attribute() {
        return _attribute;
    }

    public void set_attribute(String _attribute) {
        this._attribute = _attribute;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public ResourceAttribute(){

    }

    protected ResourceAttribute(Parcel in) {
        _id = in.readString();
        _attribute = in.readString();
        label = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(_attribute);
        dest.writeString(label);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ResourceAttribute> CREATOR = new Parcelable.Creator<ResourceAttribute>() {
        @Override
        public ResourceAttribute createFromParcel(Parcel in) {
            return new ResourceAttribute(in);
        }

        @Override
        public ResourceAttribute[] newArray(int size) {
            return new ResourceAttribute[size];
        }
    };
}