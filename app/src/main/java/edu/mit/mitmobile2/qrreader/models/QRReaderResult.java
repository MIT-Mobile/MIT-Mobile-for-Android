package edu.mit.mitmobile2.qrreader.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;

/**
 * Created by serg on 6/16/15.
 */
public class QrReaderResult extends DatabaseObject implements Parcelable {

//    @property (nonatomic, strong) NSDate * date;
//    @property (nonatomic, strong) UIImage * thumbnail;
//    @property (nonatomic, copy) NSString * text;
//    @property (nonatomic, strong, readonly) UIImage * image;
//
//    @property (nonatomic, strong, readonly) MITScannerImage *imageData;
//    @property (nonatomic, strong) UIImage *scanImage;

    private long id;
    private Date date;
    private String text;

    public QrReaderResult() {
        // empty constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /* DatabaseObject */

    @Override
    protected String getTableName() {
        return Schema.QrReaderResult.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setId(cursor.getLong(cursor.getColumnIndex(Schema.QrReaderResult.ID_COL)));
        setDate(new Date(cursor.getLong(cursor.getColumnIndex(Schema.QrReaderResult.DATE))));
        setText(cursor.getString(cursor.getColumnIndex(Schema.QrReaderResult.TEXT)));
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.QrReaderResult.DATE, this.date.getTime());
        values.put(Schema.QrReaderResult.TEXT, this.text);
    }

    /* Parcelable */

    protected QrReaderResult(Parcel in) {
        id = in.readLong();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        text = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeString(text);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<QrReaderResult> CREATOR = new Parcelable.Creator<QrReaderResult>() {
        @Override
        public QrReaderResult createFromParcel(Parcel in) {
            return new QrReaderResult(in);
        }

        @Override
        public QrReaderResult[] newArray(int size) {
            return new QrReaderResult[size];
        }
    };
}
