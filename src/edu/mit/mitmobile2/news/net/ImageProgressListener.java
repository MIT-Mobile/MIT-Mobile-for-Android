package edu.mit.mitmobile2.news.net;

import android.graphics.Bitmap;

public interface ImageProgressListener {
	void onProgressUpdateBitmap(Bitmap... bitmap);
	void onPostExecuteBitmap(Long imagesDownloaded);
}
