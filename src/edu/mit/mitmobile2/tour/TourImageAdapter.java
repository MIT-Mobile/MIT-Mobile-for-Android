package edu.mit.mitmobile2.tour;

import java.util.ArrayList;

import edu.mit.mitmobile2.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;

public class TourImageAdapter extends BaseAdapter {

	private Context mContext;

	private ContentResolver crThumb;
    
	ArrayList<String> filepaths = new ArrayList<String>();
	ArrayList<String> titles = new ArrayList<String>();

	static final int THUMB_DIM  = 150;

	private int mGalleryItemBackground;
    private int unfocusedSize;
    
	public TourImageAdapter(Context c, int unfocusedSize) {
        mContext = c;
        crThumb = mContext.getContentResolver();
        this.unfocusedSize = unfocusedSize;
        
        TypedArray a = mContext.obtainStyledAttributes(R.styleable.TourGallery);
        mGalleryItemBackground = a.getResourceId(R.styleable.TourGallery_android_galleryItemBackground, 0);
        a.recycle();
    }

    public int getCount() {
        return filepaths.size();
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	
        ImageView imageView;
        
        if (convertView == null) {  
            imageView = new ImageView(mContext);
            /*
            // Grid
            imageView.setLayoutParams(new GridView.LayoutParams(THUMB_DIM,THUMB_DIM));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(4, 4, 4, 4);
            */
            // Gallery
            imageView.setLayoutParams(new Gallery.LayoutParams(unfocusedSize, unfocusedSize));
           //imageView.setLayoutParams(new Gallery.LayoutParams(200, 150));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundResource(mGalleryItemBackground);
            
        } else {
            imageView = (ImageView) convertView;
        }

        
        String p = filepaths.get(position);
        

		BitmapFactory.Options options = new BitmapFactory.Options();
		//options.inSampleSize = 8;
		options.inSampleSize = 4;

		
		Bitmap bitMap;
		bitMap = BitmapFactory.decodeFile(p, options);
		//bitMap = BitmapFactory.decodeFile(u.getPath(), options);
        imageView.setImageBitmap(bitMap);
		
        
        return imageView;
    }

	@Override
	public Object getItem(int position) {
		return position;
	}

	public void reset() {
		filepaths = new ArrayList<String>();
		titles = new ArrayList<String>();
	}

	public void addImage(String filename, String title) {
		filepaths.add(filename);
		titles.add(title);
    	notifyDataSetChanged();
	}

}
