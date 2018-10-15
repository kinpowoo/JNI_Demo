package com.szsszwl.jnipro.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.BaseAdapter;

/**
 * Created by DeskTop29 on 2018/8/20.
 */

public abstract class MyBaseAdapter extends BaseAdapter {

    public abstract int getCellWidth();
    abstract void calCellWidth();
    public abstract void setRowCount(int rowCount);
    public abstract void setHorizontalMargin(float horizontalMargin);

    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

}
