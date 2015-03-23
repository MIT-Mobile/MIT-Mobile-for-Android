package edu.mit.mitmobile2.shuttles;

import edu.mit.mitmobile2.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ShuttlesStatusView extends View {

	enum ShuttleStatus {
		ON,
		OFF,
	}
	
	enum Position {
		START,
		BETWEEN,
		END,
	}
	
	ShuttleStatus mStatus;
	Position mPosition;
	
	public ShuttlesStatusView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}
	
	public void setStatus(ShuttleStatus status) {
		mStatus = status;
		invalidate();
	}
	
	public void setPosition(Position position) {
		mPosition = position;
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = getContext().getResources().getDimensionPixelSize(R.dimen.shuttlesStatusWidth);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		float width = getMeasuredWidth();
		float height = getMeasuredHeight();
		
		float lineWidth = getContext().getResources().getDimension(R.dimen.shuttlesStatusGrayWidth);
		RectF lineRect = new RectF();
		lineRect.left = (width - lineWidth) / 2.0f;
		lineRect.right = lineRect.left + lineWidth;
		
		
		lineRect.top = 0;
		if (mPosition == Position.START) {
			lineRect.top = height / 2.0f;
		}
		
		lineRect.bottom = height;
		if (mPosition == Position.END) {
			lineRect.bottom = height / 2.0f;
		}
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		int gray = getContext().getResources().getColor(R.color.shuttlesStatusOffColor);
		paint.setColor(gray);
		
		canvas.drawRect(lineRect, paint);
		
		float grayRadius = getContext().getResources().getDimension(R.dimen.shuttlesStatusGrayRadius);
		canvas.drawCircle(width/2.0f, height/2.0f, grayRadius, paint);
				

		int red = getContext().getResources().getColor(R.color.shuttlesStatusOnColor);
		paint.setColor(red);
		float redRadius = getContext().getResources().getDimension(R.dimen.shuttlesStatusRedRadius);
		if (mStatus == ShuttleStatus.ON) {
			canvas.drawCircle(width/2.0f, height/2.0f, redRadius, paint);
		}
		
	}
}
