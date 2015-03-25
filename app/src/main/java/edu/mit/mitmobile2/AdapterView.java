package edu.mit.mitmobile2;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

/**
 * Created by philipcorriveau on 3/23/15.
 */
public class AdapterView extends LinearLayout {

    private ListAdapter adapter;

    private final DataSetObserver adapterObserver = new DataSetObserver(){
        @Override
        public void onChanged() {
            removeAllViews();
            addViews();
        }

        @Override
        public void onInvalidated() {
            removeAllViews();
        }
    };

    public AdapterView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setOrientation(VERTICAL);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdapterView, defStyle, 0);
        Drawable divider = a.getDrawable(R.styleable.AdapterView_android_divider);
        final int dividerHeight = a.getDimensionPixelSize(R.styleable.AdapterView_android_dividerHeight, -1);
        a.recycle();

        if ((divider != null) && (divider.getIntrinsicHeight() < 0) && (dividerHeight >= 0)) {
            divider = new InsetDrawable(divider, 0) {
                @Override
                public int getIntrinsicHeight() {
                    return dividerHeight;
                }
            };
        }

        if (divider != null) {
            setDividerDrawable(divider);
            setDividerPadding(0);
            setShowDividers(SHOW_DIVIDER_MIDDLE);
        }
    }

    public void setAdapter(ListAdapter adapter) {
        if (this.adapter != adapter) {
            if (this.adapter != null) {
                this.adapter.unregisterDataSetObserver(adapterObserver);
            }

            this.adapter = adapter;

            removeAllViews();

            if (this.adapter == null) {
                return;
            }

            addViews();

            this.adapter.registerDataSetObserver(adapterObserver);
        }
    }

    private void addViews() {
        int rows = adapter.getCount();
        for (int i = 0; i < rows; i++) {
            View rowView = adapter.getView(i, null, this);
            if (rowView != null) {
                addView(rowView);
            }
        }
    }
}
