package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

/**
 * Created by grmartin on 4/20/15.
 */
public class DrawableUtils {
    public static Drawable applyTint(Drawable drawable, int color) {
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static Drawable applyTint(Context context, int drawableId, int color) {
        Drawable draw = context.getResources().getDrawable(drawableId);
        applyTint(draw, color);
        return draw;
    }
}
