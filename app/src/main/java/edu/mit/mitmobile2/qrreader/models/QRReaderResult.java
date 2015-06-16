package edu.mit.mitmobile2.qrreader.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.Date;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;

/**
 * Created by serg on 6/16/15.
 */
public class QRReaderResult extends DatabaseObject {

//    @property (nonatomic, strong) NSDate * date;
//    @property (nonatomic, strong) UIImage * thumbnail;
//    @property (nonatomic, copy) NSString * text;
//    @property (nonatomic, strong, readonly) UIImage * image;
//
//    @property (nonatomic, strong, readonly) MITScannerImage *imageData;
//    @property (nonatomic, strong) UIImage *scanImage;

    private Date date;
    private String text;
    private Bitmap thumbnail;
    private Bitmap image;

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

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    /* DatabaseObject */

    @Override
    protected String getTableName() {
        return Schema.QrReaderResult.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setDate(new Date(cursor.getLong(cursor.getColumnIndex(Schema.QrReaderResult.DATE))));
        setText(cursor.getString(cursor.getColumnIndex(Schema.QrReaderResult.TEXT)));
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.QrReaderResult.DATE, this.date.getTime());
        values.put(Schema.QrReaderResult.TEXT, this.text);
    }
}
