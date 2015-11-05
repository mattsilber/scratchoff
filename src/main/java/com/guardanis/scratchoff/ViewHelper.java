package com.guardanis.scratchoff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

public class ViewHelper {

    @SuppressLint("NewApi")
    public static void disableHardwareAcceleration(View v) {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @SuppressLint("NewApi")
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if(Build.VERSION.SDK_INT < 16)
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        else v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }

    public static int getPxFromDip(Context context, int dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

}
