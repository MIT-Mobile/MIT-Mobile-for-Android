package edu.mit.mitmobile2.people.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema.Person;

import static edu.mit.mitmobile2.DatabaseObject.SchemaTable;
import static edu.mit.mitmobile2.Schema.Person.IS_FAVORITE;
import static edu.mit.mitmobile2.Schema.Person.TABLE_NAME;

@SchemaTable(edu.mit.mitmobile2.Schema.Person.class)
public class MITPerson extends DatabaseObject implements Parcelable, MITPeopleDirectoryPersonAdaptablePerson {
    @NonAtomicExclude
    private String uid;
    private String affiliation;
    private String city;
    private String dept;
    private ArrayList<String> email;
    private ArrayList<String> fax;
    private String givenname;
    private String name;
    private ArrayList<String> office;
    private ArrayList<String> phone;
    private ArrayList<String> home; // homephone
    private String state;
    private String street;
    private String surname;
    private String title;
    private String url;
    private ArrayList<String> website;
    private Calendar lastUpCalendar;

    @NonAtomicExclude
    private boolean favorite;

    private int favoriteIndex;

    public MITPerson() {
    }

    public int getFavoriteIndex() {
        return favoriteIndex;
    }

    public void setFavoriteIndex(int favoriteIndex) {
        this.favoriteIndex = favoriteIndex;
    }

    @FieldName(IS_FAVORITE)
    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Calendar getLastUpCalendar() {
        return lastUpCalendar;
    }

    public void setLastUpCalendar(Calendar lastUpCalendar) {
        this.lastUpCalendar = lastUpCalendar;
    }

    public ArrayList<String> getWebsite() {
        return website;
    }

    public void setWebsite(ArrayList<String> website) {
        this.website = website;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<String> getHome() {
        return home;
    }

    public void setHome(ArrayList<String> home) {
        this.home = home;
    }

    public ArrayList<String> getPhone() {
        return phone;
    }

    public void setPhone(ArrayList<String> phone) {
        this.phone = phone;
    }

    public ArrayList<String> getOffice() {
        return office;
    }

    public void setOffice(ArrayList<String> office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public ArrayList<String> getFax() {
        return fax;
    }

    public void setFax(ArrayList<String> fax) {
        this.fax = fax;
    }

    public ArrayList<String> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<String> email) {
        this.email = email;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFormattedAddress() {
        if (!TextUtils.isEmpty(this.street) && !TextUtils.isEmpty(this.city) && !TextUtils.isEmpty(this.state)) {
            return String.format("%s\n%s, %s", this.street, this.city, this.state);
        }

        return null;
    }

    public String valueForIndexPath(MITPersonIndexPath index) {
        if (index != null && index.getAttributeType() != null && index.getIndex() != MITPersonIndexPath.NO_INDEX) {
            Object o = null;
            try {
                o = index.getAttributeType().invokeGetterOn(this);
            } catch (NoSuchMethodException e) {
                Log.e(this.getClass().getSimpleName(), "Method Exception => " +this, e);
            } catch (InvocationTargetException e) {
                Log.e(this.getClass().getSimpleName(), "Target Exception => " +this, e);
            } catch (IllegalAccessException e) {
                Log.e(this.getClass().getSimpleName(), "Access Exception => " +this, e);
            }

            if (o != null) {
                if (o instanceof String) {
                    if (index.getIndex() == 0) return (String) o;
                } else if (o instanceof List){
                    return (String) ((List<?>)o).get(index.getIndex());
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "MITPerson{" +
                "uid='" + uid + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", city='" + city + '\'' +
                ", dept='" + dept + '\'' +
                ", email=" + email +
                ", fax=" + fax +
                ", givenname='" + givenname + '\'' +
                ", name='" + name + '\'' +
                ", office=" + office +
                ", phone=" + phone +
                ", home=" + home +
                ", state='" + state + '\'' +
                ", street='" + street + '\'' +
                ", surname='" + surname + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", website=" + website +
                ", lastUpCalendar=" + lastUpCalendar +
                ", favorite=" + favorite +
                ", favoriteIndex=" + favoriteIndex +
                '}';
    }

    /* DatabaseObject */
    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
//        this.uid =  getDatabaseId();
////        long id = getDatabaseId();
////        this.stops = new ArrayList<>();
////
////        while (cursor.getLong(cursor.getColumnIndex(Schema.Route.ID_COL)) == id) {
////            MITShuttleStop stopWrapper = new MITShuttleStop();
////            stopWrapper.buildSubclassFromCursor(cursor, dbAdapter);
////            this.stops.add(stopWrapper);
////            boolean itemsRemaining = cursor.moveToNext();
////            if (!itemsRemaining) {
////                break;
////            }
////        }
////        // Move back 1 since we looked ahead to the next ID
////        cursor.moveToPrevious();
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Person.PERSON_ID, this.uid);
        values.put(IS_FAVORITE, this.isFavorite());
        values.put(Person.EXTENDED_DATA, NONATOMIC_ENCODER.toJson(this));
    }

    /* PArcelable */
    protected MITPerson(Parcel in) {
        uid = in.readString();
        affiliation = in.readString();
        city = in.readString();
        dept = in.readString();
        if (in.readByte() == 0x01) {
            email = new ArrayList<String>();
            in.readList(email, String.class.getClassLoader());
        } else {
            email = null;
        }
        if (in.readByte() == 0x01) {
            fax = new ArrayList<String>();
            in.readList(fax, String.class.getClassLoader());
        } else {
            fax = null;
        }
        givenname = in.readString();
        name = in.readString();
        if (in.readByte() == 0x01) {
            office = new ArrayList<String>();
            in.readList(office, String.class.getClassLoader());
        } else {
            office = null;
        }
        if (in.readByte() == 0x01) {
            phone = new ArrayList<String>();
            in.readList(phone, String.class.getClassLoader());
        } else {
            phone = null;
        }
        if (in.readByte() == 0x01) {
            home = new ArrayList<String>();
            in.readList(home, String.class.getClassLoader());
        } else {
            home = null;
        }
        state = in.readString();
        street = in.readString();
        surname = in.readString();
        title = in.readString();
        url = in.readString();
        if (in.readByte() == 0x01) {
            website = new ArrayList<String>();
            in.readList(website, String.class.getClassLoader());
        } else {
            website = null;
        }
        lastUpCalendar = (Calendar) in.readValue(Calendar.class.getClassLoader());
        favorite = in.readByte() != 0x00;
        favoriteIndex = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(affiliation);
        dest.writeString(city);
        dest.writeString(dept);
        if (email == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(email);
        }
        if (fax == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(fax);
        }
        dest.writeString(givenname);
        dest.writeString(name);
        if (office == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(office);
        }
        if (phone == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(phone);
        }
        if (home == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(home);
        }
        dest.writeString(state);
        dest.writeString(street);
        dest.writeString(surname);
        dest.writeString(title);
        dest.writeString(url);
        if (website == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(website);
        }
        dest.writeValue(lastUpCalendar);
        dest.writeByte((byte) (favorite ? 0x01 : 0x00));
        dest.writeInt(favoriteIndex);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITPerson> CREATOR = new Parcelable.Creator<MITPerson>() {
        @Override
        public MITPerson createFromParcel(Parcel in) {
            return new MITPerson(in);
        }

        @Override
        public MITPerson[] newArray(int size) {
            return new MITPerson[size];
        }
    };

}
