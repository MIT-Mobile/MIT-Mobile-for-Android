package edu.mit.mitmobile2.facilities.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FacilitiesBuilding implements Parcelable {

    public FacilitiesBuilding() {
        this.floors = new ArrayList<>();
    }

    private List<Floor> floors;

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    protected FacilitiesBuilding(Parcel in) {
        if (in.readByte() == 0x01) {
            floors = new ArrayList<>();
            in.readList(floors, Floor.class.getClassLoader());
        } else {
            floors = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (floors == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(floors);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FacilitiesBuilding> CREATOR = new Parcelable.Creator<FacilitiesBuilding>() {
        @Override
        public FacilitiesBuilding createFromParcel(Parcel in) {
            return new FacilitiesBuilding(in);
        }

        @Override
        public FacilitiesBuilding[] newArray(int size) {
            return new FacilitiesBuilding[size];
        }
    };

    public static class Floor implements Parcelable {

        public Floor() {
            this.rooms = new ArrayList<>();
        }

        public List<String> rooms;

        public List<String> getRooms() {
            return rooms;
        }

        public void setRooms(List<String> rooms) {
            this.rooms = rooms;
        }

        protected Floor(Parcel in) {
            if (in.readByte() == 0x01) {
                rooms = new ArrayList<>();
                in.readList(rooms, String.class.getClassLoader());
            } else {
                rooms = null;
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (rooms == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeList(rooms);
            }
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Floor> CREATOR = new Parcelable.Creator<Floor>() {
            @Override
            public Floor createFromParcel(Parcel in) {
                return new Floor(in);
            }

            @Override
            public Floor[] newArray(int size) {
                return new Floor[size];
            }
        };
    }
}
