package edu.mit.mitmobile2.mit150;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;

public class MIT150RemoteView extends RemoteImageView {

	int width;
	Context ctx;
	TextView mTextView;
	int color;
	
	public MIT150RemoteView(Context context, AttributeSet attrs, int width, int color) {
		
		super(context, attrs);
		this.width = width;
		this.ctx = context;
		this.color = color;
		
	}
	/****************************************************************************/
	public void inflateLayout(Context context) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.mit150_remote_imageview, this); 
	}
	/****************************************************************************/
	// This rounds corners of image as well as textview and darkens latters background
	public void updateImage(Bitmap image) {
	
		LayoutParams params = new LayoutParams(width,LayoutParams.WRAP_CONTENT);
		
		setLayoutParams(params);

		
		// Image 
		Bitmap bm;
		bm = BitmapUtils.getRoundedCornerBitmap(ctx, image,0);
		mContentView.setImageBitmap(bm);
		
		
		// TextView background
		mTextView = (TextView) findViewById(R.id.mit150CorridorTV);
		bm = BitmapUtils.createRoundedBottomBitmap(ctx, width, 50, color);
		BitmapDrawable bd = new BitmapDrawable(bm);
		mTextView.setBackgroundDrawable(bd);
		
	}

}
