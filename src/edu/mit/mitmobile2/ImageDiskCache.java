package edu.mit.mitmobile2;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ImageDiskCache {
	private Context mContext;
	
	public ImageDiskCache(Context context) {
		mContext = context;
	}
	
	private byte[] getImageData(ContentResolver contentResolver, String webUrl) {
		Cursor cursor = contentResolver.query(
			WebImageCacheProvider.CONTENT_URI,
			new String[] {WebImageCacheProvider.Columns.DATA},
			WebImageCacheProvider.Columns.URL + "=?",
			new String[] { webUrl },
			null
		);
		
		byte[] bytes = null;
		if(cursor.moveToFirst()) {
			int bytesIndex = cursor.getColumnIndex(WebImageCacheProvider.Columns.DATA);
			bytes = cursor.getBlob(bytesIndex);
		}
		cursor.close();
		return bytes;
	}
	
	public byte[] getImageBytes(String url) {
		ContentResolver contentResolver = mContext.getContentResolver();
		byte[] imageBytes = getImageData(contentResolver, url);
		if(imageBytes != null) {
			return imageBytes;
		}
		
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpGet request = new HttpGet(url);
    	HttpResponse response;
			
		try {
			response = httpClient.execute(request);

			if(response.getStatusLine().getStatusCode() == 200) {
				byte[] imageData = EntityUtils.toByteArray(response.getEntity());
	     		ContentValues imageValues = new ContentValues();
	     		imageValues.put(WebImageCacheProvider.Columns.URL, url);
	     		imageValues.put(WebImageCacheProvider.Columns.DATA, imageData);
	     		contentResolver.insert(WebImageCacheProvider.CONTENT_URI, imageValues);
	     		
	     		return imageData;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}