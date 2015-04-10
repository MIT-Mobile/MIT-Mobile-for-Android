package edu.mit.mitmobile2.mobius.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.mobius.ResourceAttribute;

/**
 * Created by sseligma on 4/2/15.
 */
public class Roomset extends MapItem implements Parcelable {

    String _id;
    String _dlc;
    String roomset_code;
    String roomset_name;
    String pi_name;
    String pi_kerberos;
    String ehs_rep_name;
    String ehs_rep_kerberos;
    ArrayList<RoomsetHours> hours;

    public Roomset() {
    }

    public Roomset(JSONObject data) {
        this._id = data.optString("_id","");
        this._dlc = data.optString("_dlc","");
        this.roomset_code = data.optString("roomset_code","");
        this.roomset_name = data.optString("roomset_name","");
        this.pi_name = data.optString("pi_name","");
        this.pi_kerberos = data.optString("pi_kerberos","");
        this.ehs_rep_name = data.optString("ehs_rep_name","");
        this.ehs_rep_kerberos = data.optString("ehs_rep_kerberos","");
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_dlc() {
        return _dlc;
    }

    public void set_dlc(String _dlc) {
        this._dlc = _dlc;
    }

    public String getRoomset_code() {
        return roomset_code;
    }

    public void setRoomset_code(String roomset_code) {
        this.roomset_code = roomset_code;
    }

    public String getRoomset_name() {
        return roomset_name;
    }

    public void setRoomset_name(String roomset_name) {
        this.roomset_name = roomset_name;
    }

    public String getPi_name() {
        return pi_name;
    }

    public void setPi_name(String pi_name) {
        this.pi_name = pi_name;
    }

    public String getPi_kerberos() {
        return pi_kerberos;
    }

    public void setPi_kerberos(String pi_kerberos) {
        this.pi_kerberos = pi_kerberos;
    }

    public String getEhs_rep_name() {
        return ehs_rep_name;
    }

    public void setEhs_rep_name(String ehs_rep_name) {
        this.ehs_rep_name = ehs_rep_name;
    }

    public String getEhs_rep_kerberos() {
        return ehs_rep_kerberos;
    }

    public void setEhs_rep_kerberos(String ehs_rep_kerberos) {
        this.ehs_rep_kerberos = ehs_rep_kerberos;
    }


    public ArrayList<RoomsetHours> getHours() {
        return hours;
    }

    public void setHours(ArrayList<RoomsetHours> hours) {
        this.hours = hours;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this._dlc);
        dest.writeString(this.roomset_code);
        dest.writeString(this.roomset_name);
        dest.writeString(this.pi_name);
        dest.writeString(this.pi_kerberos);
        dest.writeString(this.ehs_rep_name);
        dest.writeString(this.ehs_rep_kerberos);
        dest.writeSerializable(this.hours);
    }

    private Roomset(Parcel in) {
        this._id = in.readString();
        this._dlc = in.readString();
        this.roomset_code = in.readString();
        this.roomset_name = in.readString();
        this.pi_name = in.readString();
        this.pi_kerberos = in.readString();
        this.ehs_rep_name = in.readString();
        this.ehs_rep_kerberos = in.readString();
        this.hours = (ArrayList<RoomsetHours>) in.readSerializable();
    }

    public static final Creator<Roomset> CREATOR = new Creator<Roomset>() {
        public Roomset createFromParcel(Parcel source) {
            return new Roomset(source);
        }

        public Roomset[] newArray(int size) {
            return new Roomset[size];
        }
    };

    @Override
    public int getMapItemType() {
        return 0;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        return null;
    }

    @Override
    protected String getTableName() {
        return null;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {

    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {

    }
}
