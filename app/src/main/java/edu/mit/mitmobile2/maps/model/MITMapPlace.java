package edu.mit.mitmobile2.maps.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.maps.MapItem;

/**
 * Created by serg on 5/18/15.
 */

public class MITMapPlace extends MapItem implements Parcelable {

    public class MITMapPlaceSnippet {
        String name;
        String id;
        String buildingNumber;

        public MITMapPlaceSnippet(String buildingNumber, String id, String name) {
            this.buildingNumber = buildingNumber;
            this.id = id;
            this.name = name;
        }

        public String getBuildingNumber() {
            return buildingNumber;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("lat_wgs84")
    private double latitude;

    @SerializedName("long_wgs84")
    private double longitude;

    @SerializedName("bldgnum")
    private String buildingNumber;

    @SerializedName("bldgimg")
    private String buildingImageUrl;

    @SerializedName("street")
    private String street;

    @SerializedName("architect")
    private String architect;

    @SerializedName("mailing")
    private String mailing;

    @SerializedName("viewangle")
    private String viewangle;

    @Expose
    private MITMapCategory mitCategory;

    private int index;
    // TODO: add fields:
    // category
    // contents
    private List<String> category = new ArrayList<>();

    private List<MITMapPlaceContent> contents = new ArrayList<>();

    public MITMapPlace() {
        // empty constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getBuildingImageUrl() {
        return buildingImageUrl;
    }

    public void setBuildingImageUrl(String buildingImageUrl) {
        this.buildingImageUrl = buildingImageUrl;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getArchitect() {
        return architect;
    }

    public void setArchitect(String architect) {
        this.architect = architect;
    }

    public String getMailing() {
        return mailing;
    }

    public void setMailing(String mailing) {
        this.mailing = mailing;
    }

    public String getViewangle() {
        return viewangle;
    }

    public void setViewangle(String viewangle) {
        this.viewangle = viewangle;
    }

    public MITMapCategory getMitCategory() {
        return mitCategory;
    }

    public void setMitCategory(MITMapCategory category) {
        this.mitCategory = category;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<MITMapPlaceContent> getContents() {
        return contents;
    }

    public void setContents(List<MITMapPlaceContent> contents) {
        this.contents = contents;
    }

    /* Helpers */

    public String getTitle(Context context) {
        if (TextUtils.isEmpty(buildingNumber)) {
            return name;
        } else {
            return context.getString(R.string.map_categories_detail_place_title, buildingNumber);
        }
    }

    public String getSubtitle(Context context) {
        if (!name.equals(getTitle(context))) {
            return name;
        }

        return "";
    }

    /* Parcelable */

    protected MITMapPlace(Parcel in) {
        id = in.readString();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        buildingNumber = in.readString();
        buildingImageUrl = in.readString();
        street = in.readString();
        architect = in.readString();
        mailing = in.readString();
        viewangle = in.readString();
        if (in.readByte() == 0x01) {
            category = new ArrayList<>();
            in.readList(category, String.class.getClassLoader());
        } else {
            category = null;
        }
        if (in.readByte() == 0x01) {
            contents = new ArrayList<>();
            in.readList(contents, MITMapPlaceContent.class.getClassLoader());
        } else {
            contents = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(buildingNumber);
        dest.writeString(buildingImageUrl);
        dest.writeString(street);
        dest.writeString(architect);
        dest.writeString(mailing);
        dest.writeString(viewangle);
        if (category == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(category);
        }
        if (contents == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(contents);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITMapPlace> CREATOR = new Parcelable.Creator<MITMapPlace>() {
        @Override
        public MITMapPlace createFromParcel(Parcel in) {
            return new MITMapPlace(in);
        }

        @Override
        public MITMapPlace[] newArray(int size) {
            return new MITMapPlace[size];
        }
    };

    @Override
    public int getMapItemType() {
        return MARKERTYPE;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(latitude, longitude));
        String snippet = new Gson().toJson(new MITMapPlaceSnippet(buildingNumber, id, name));
        options.snippet(snippet);
        return options;
    }

    @Override
    public int getIconResource() {
        return R.drawable.ic_pin_red;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    protected String getTableName() {
        return Schema.MapPlace.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setId(cursor.getString(cursor.getColumnIndex(Schema.MapPlace.PLACE_ID)));
        setName(cursor.getString(cursor.getColumnIndex(Schema.MapPlace.PLACE_NAME)));
        setLatitude(cursor.getDouble(cursor.getColumnIndex(Schema.MapPlace.LATITUDE)));
        setLongitude(cursor.getDouble(cursor.getColumnIndex(Schema.MapPlace.LONGITUDE)));
        setBuildingNumber(cursor.getString(cursor.getColumnIndex(Schema.MapPlace.BUILDING_NUM)));
        setBuildingImageUrl(cursor.getString(cursor.getColumnIndex(Schema.MapPlace.BUILDING_IMAGE_URL)));
        setStreet(cursor.getString(cursor.getColumnIndex(Schema.MapPlace.STREET)));
        setArchitect(cursor.getString(cursor.getColumnIndex(Schema.MapPlace.ARCHITECT)));
        setMailing(cursor.getString(cursor.getColumnIndex(Schema.MapPlace.MAILING)));
        setViewangle(cursor.getString(cursor.getColumnIndex(Schema.MapPlace.VIEW_ANGLE)));

        String categoriesString = cursor.getString(cursor.getColumnIndex(Schema.MapPlace.CATEGORIES));
        if (!TextUtils.isEmpty(categoriesString)) {
            //noinspection unchecked
            setCategory((List<String>) new Gson().fromJson(categoriesString, new TypeToken<List<String>>() {
            }.getType()));
        }
        setContents(dbAdapter.getMapPlaceContent(this.id));
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.MapPlace.PLACE_ID, this.id);
        values.put(Schema.MapPlace.PLACE_NAME, this.name);
        values.put(Schema.MapPlace.LATITUDE, this.latitude);
        values.put(Schema.MapPlace.LONGITUDE, this.longitude);
        values.put(Schema.MapPlace.BUILDING_NUM, this.buildingNumber);
        values.put(Schema.MapPlace.BUILDING_IMAGE_URL, this.buildingImageUrl);
        values.put(Schema.MapPlace.STREET, this.street);
        values.put(Schema.MapPlace.ARCHITECT, this.architect);
        values.put(Schema.MapPlace.MAILING, this.mailing);
        values.put(Schema.MapPlace.VIEW_ANGLE, this.viewangle);
        values.put(Schema.MapPlace.CATEGORIES, new Gson().toJson(this.category));

        for (MITMapPlaceContent content : this.contents) {
            content.setPlaceId(this.id);
            dbAdapter.acquire(content);
            content.persistToDatabase();
        }
    }
}
