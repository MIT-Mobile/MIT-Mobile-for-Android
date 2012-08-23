package edu.mit.mitmobile2.qrreader;

import java.util.Date;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class QRCode implements Parcelable {
	
	private String mId;
	private Bitmap mBitmap;
	private Date mDate;
	
	public QRCode(String displayName, Bitmap bitmap, Date date) {
		mId = displayName;
		mBitmap = bitmap;
		mDate = date;
	}

	public static final Parcelable.Creator<QRCode> CREATOR = new Parcelable.Creator<QRCode>() {

		@Override
		public QRCode createFromParcel(Parcel source) {
			String url = source.readString();
			Bitmap bitmap = source.readParcelable(Bitmap.class.getClassLoader());
			Date date = new Date(source.readLong());
			return new QRCode(url, bitmap, date);
		}

		@Override
		public QRCode[] newArray(int size) {
			return new QRCode[size];
		}
		
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mId);
		int height = mBitmap.getHeight();
		int width = mBitmap.getWidth();
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap, width/2, height/2, true);
		dest.writeParcelable(scaledBitmap, flags);
		dest.writeLong(mDate.getTime());
	}
	
	public String getId() {
		return mId;
	}
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	public Date getDate() {
		return mDate;
	}
}
