package com.szsszwl.jnipro.view;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by DeskTop29 on 2018/8/21.
 */
public class ToastUtils {

    private static Toast toast;

    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

}
