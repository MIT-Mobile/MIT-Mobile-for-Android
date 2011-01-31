package edu.mit.mitmobile2.mit150;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class BitmapUtils {
	
	private static final int ROUND_DIPS = 8;

	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, int color) {
	   
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    color = 0xff424242;  // FIXME
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = convertDipsToPixels(context, ROUND_DIPS);

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    return output;
	}

	public static Bitmap createRoundedBottomBitmap(Context context, int width, int height, int color) {
	   
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    //color = 0x80424242;  // FIXME
	    
	    final Paint paint = new Paint();
	    final int roundPxInt = convertDipsToPixels(context, ROUND_DIPS);
	    final float roundPx = roundPxInt;
	    
	    final Rect rect = new Rect(0, 0, width, height-roundPxInt);
	    final RectF rectF = new RectF(rect);
	    final Rect rectRound = new Rect(roundPxInt, height-roundPxInt, width-roundPxInt, height);
	    final RectF rectFRound = new RectF(rectRound);
	    
	    paint.setAntiAlias(true);
	    paint.setColor(color);
	    
	    canvas.drawARGB(0, 0, 0, 0);

	    // Corners
	    Rect oval = new Rect(0, height-2*roundPxInt, 2*roundPxInt, height);
	    RectF ovalF = new RectF(oval);
	    canvas.drawArc(ovalF, 90.0f, 90.0f, true, paint);
	    
	    oval = new Rect(width-2*roundPxInt, height-2*roundPxInt, width, height);
	    ovalF = new RectF(oval);
	    canvas.drawArc(ovalF, 0.0f, 90.0f, true, paint);
	    
	    // Big and small rectangles
	    canvas.drawRect(rectF, paint);
	    canvas.drawRect(rectFRound, paint);

	    return output;
	}
	
	public static int convertDipsToPixels(Context context, int dips) {
	    final float scale = context.getResources().getDisplayMetrics().density;
	    return (int) (dips * scale + 0.5f);
	}
}