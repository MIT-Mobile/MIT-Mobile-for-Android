package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MITDiningLinks implements Parcelable {
    protected String name;
    protected String url;
    protected MITDiningDining dining;

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public MITDiningDining getDining() {
		return dining;
	}

	@Override
	public String toString() {
		return "MITDiningLinks{" +
			"name='" + name + '\'' +
			", url='" + url + '\'' +
			", dining=" + dining +
			'}';
	}

	protected MITDiningLinks(Parcel in) {
		name = in.readString();
		url = in.readString();
		dining = (MITDiningDining) in.readValue(MITDiningDining.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(url);
		dest.writeValue(dining);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<MITDiningLinks> CREATOR = new Parcelable.Creator<MITDiningLinks>() {
		@Override
		public MITDiningLinks createFromParcel(Parcel in) {
			return new MITDiningLinks(in);
		}

		@Override
		public MITDiningLinks[] newArray(int size) {
			return new MITDiningLinks[size];
		}
	};
}