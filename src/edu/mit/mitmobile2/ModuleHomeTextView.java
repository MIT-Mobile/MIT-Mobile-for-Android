package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

public class ModuleHomeTextView extends TextView {

    public ModuleHomeTextView(Context context, AttributeSet attrs) {
	super(context, attrs);
    }
    
    public ModuleHomeTextView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
	
	if (isClickable()) {
	    int lineWidth = getContext().getResources().getDimensionPixelSize(R.dimen.moduleHomeUnderlineWidth);
	    int height = canvas.getHeight();
	    int width = canvas.getWidth();
	    float standardPadding = getContext().getResources().getDimension(R.dimen.standardPadding);
	    int horizontalPadding = (int) (standardPadding/2);
	    int bottomInset = (int) (standardPadding * (2.0f/3.0f));
	
	    RectF rect = new RectF(
		    horizontalPadding, height-lineWidth-bottomInset, 
		    width-horizontalPadding, height-bottomInset);
	    
	    Paint paint = new Paint();
	    paint.setColor(getContext().getResources().getColor(R.color.moduleHomeUnderlineColor));
	    canvas.drawRect(rect, paint);
	}
	
	super.onDraw(canvas);
    }
}
