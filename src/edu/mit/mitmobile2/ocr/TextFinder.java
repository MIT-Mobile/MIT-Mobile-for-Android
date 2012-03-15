package edu.mit.mitmobile2.ocr;

import android.graphics.Bitmap;

public class TextFinder {
    static {
        System.loadLibrary("ocrtextfinder");
    }
    
    public native RegionBounds[] findTextImages(byte[] source, int width, int height, Bitmap bitmap);

}
