package tk.nkduy.cropimage.util;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;

public class ResUtil {

    private Context context;

    public ResUtil(Context context) {
        this.context = context;
    }

    @ColorInt
    public int color(@ColorRes int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    public int dimen(@DimenRes int dimRes) {
        return Math.round(context.getResources().getDimension(dimRes));
    }
}
