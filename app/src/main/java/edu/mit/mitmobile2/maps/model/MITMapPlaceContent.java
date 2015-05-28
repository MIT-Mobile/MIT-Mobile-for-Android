package edu.mit.mitmobile2.maps.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;

public class MITMapPlaceContent extends DatabaseObject implements Parcelable {

    @Expose
    private String name;
    @Expose
    private List<String> category = new ArrayList<>();
    @Expose
    private String url;
    @Expose
    private List<String> altname = new ArrayList<>();

    private String placeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getAltname() {
        return altname;
    }

    public void setAltname(List<String> altname) {
        this.altname = altname;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public MITMapPlaceContent() {
    }

    @Override
    protected String getTableName() {
        return Schema.MapPlaceContent.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setName(cursor.getString(cursor.getColumnIndex(Schema.MapPlaceContent.CONTENT_NAME)));
        String categoriesString = cursor.getString(cursor.getColumnIndex(Schema.MapPlaceContent.CATEGORIES));
        if (!TextUtils.isEmpty(categoriesString)) {
            //noinspection unchecked
            setCategory((List<String>) new Gson().fromJson(categoriesString, new TypeToken<List<String>>() {
            }.getType()));
        }
        setUrl(cursor.getString(cursor.getColumnIndex(Schema.MapPlaceContent.URL)));
        String altNamesString = cursor.getString(cursor.getColumnIndex(Schema.MapPlaceContent.ALT_NAMES));
        if (!TextUtils.isEmpty(altNamesString)) {
            //noinspection unchecked
            setAltname((List<String>) new Gson().fromJson(altNamesString, new TypeToken<List<String>>() {
            }.getType()));
        }
        setPlaceId(cursor.getString(cursor.getColumnIndex(Schema.MapPlaceContent.PLACE_ID)));
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.MapPlaceContent.CONTENT_NAME, this.name);
        values.put(Schema.MapPlaceContent.CATEGORIES, new Gson().toJson(this.category));
        values.put(Schema.MapPlaceContent.URL, this.url);
        values.put(Schema.MapPlaceContent.ALT_NAMES, new Gson().toJson(this.altname));
        values.put(Schema.MapPlaceContent.PLACE_ID, this.placeId);
    }

    protected MITMapPlaceContent(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            category = new ArrayList<>();
            in.readList(category, String.class.getClassLoader());
        } else {
            category = null;
        }
        url = in.readString();
        if (in.readByte() == 0x01) {
            altname = new ArrayList<>();
            in.readList(altname, String.class.getClassLoader());
        } else {
            altname = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (category == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(category);
        }
        dest.writeString(url);
        if (altname == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(altname);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITMapPlaceContent> CREATOR = new Parcelable.Creator<MITMapPlaceContent>() {
        @Override
        public MITMapPlaceContent createFromParcel(Parcel in) {
            return new MITMapPlaceContent(in);
        }

        @Override
        public MITMapPlaceContent[] newArray(int size) {
            return new MITMapPlaceContent[size];
        }
    };
}