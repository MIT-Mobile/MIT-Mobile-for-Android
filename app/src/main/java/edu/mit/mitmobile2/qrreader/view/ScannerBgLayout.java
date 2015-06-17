package edu.mit.mitmobile2.qrreader.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import edu.mit.mitmobile2.R;

/**
 * Created by serg on 6/12/15.
 */
public class ScannerBgLayout extends RelativeLayout {

    private Paint basicPaint;
    private Paint linesPaint;

    private int squareMargin;
    private int linesWidth;

    public ScannerBgLayout(Context context) {
        super(context);
        init();
    }

    public ScannerBgLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScannerBgLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScannerBgLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        linesWidth = getResources().getDimensionPixelSize(R.dimen.scanner_lines_width);
        squareMargin = getResources().getDimensionPixelSize(R.dimen.scanner_padding);

        basicPaint = new Paint();
        basicPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        basicPaint.setColor(getResources().getColor(R.color.scanner_bg));

        linesPaint = new Paint();
        linesPaint.setStyle(Paint.Style.STROKE);
        linesPaint.setStrokeWidth(linesWidth);
        linesPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        linesPaint.setColor(getResources().getColor(R.color.scanner_lines));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawPaint(basicPaint);

        // draw transparent square
        int squareSize = getWidth() - squareMargin * 2;

        int left = squareMargin;
        int top = getHeight() / 2 - squareSize / 2;
        int right = squareMargin + squareSize;
        int bottom = getHeight() / 2 + squareSize / 2;

        DemoArea roundedRectDemoArea = new SquareDemoArea(left, top, right, bottom);
        roundedRectDemoArea.draw(canvas);

        // draw border lines
        int lineLength = squareSize / 4;
        int lineMargin = linesWidth / 2;

        int lineLeft = left - lineMargin;
        int lineTop = top - lineMargin;
        int lineRight = right + lineMargin;
        int lineBottom = bottom + lineMargin;

        // top left corner
        canvas.drawLine(lineLeft, lineTop, lineLeft + lineLength, lineTop, linesPaint);
        canvas.drawLine(lineLeft, lineTop, lineLeft, lineTop + lineLength, linesPaint);

        // top right corner
        canvas.drawLine(lineRight, lineTop, lineRight - lineLength, lineTop, linesPaint);
        canvas.drawLine(lineRight, lineTop, lineRight, lineTop + lineLength, linesPaint);

        // bottom left corner
        canvas.drawLine(lineLeft, lineBottom, lineLeft + lineLength, lineBottom, linesPaint);
        canvas.drawLine(lineLeft, lineBottom, lineLeft, lineBottom - lineLength, linesPaint);

        // bottom right corner
        canvas.drawLine(lineRight, lineBottom, lineRight - lineLength, lineBottom, linesPaint);
        canvas.drawLine(lineRight, lineBottom, lineRight, lineBottom - lineLength, linesPaint);

        super.dispatchDraw(canvas);
    }

    public abstract static class DemoArea extends RectF {
        protected Paint clearPaint;

        public DemoArea(int left, int top, int right, int bottom) {
            super(left, top, right, bottom);

            clearPaint = new Paint();
            clearPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            clearPaint.setColor(Color.TRANSPARENT);
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        public abstract void draw(Canvas canvas);
    }

    public class SquareDemoArea extends DemoArea {

        public SquareDemoArea(int left, int top, int right, int bottom) {
            super(left, top, right, bottom);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawRect(left, top, right, bottom, clearPaint);
        }
    }
}
