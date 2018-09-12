package com.yunbao.phonelive.utils;

import android.widget.Toast;

import com.yunbao.phonelive.AppContext;

/**
 * Created by cxf on 2017/8/3.
 */

public class ToastUtil {

    private static Toast sToast;

    static {
        sToast = Toast.makeText(AppContext.mApplication, "", Toast.LENGTH_SHORT);
    }

    public static void show(String s) {
        sToast.setText(s);
        sToast.show();
    }
}
