package com.baidu.idl.face.sample.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by lvqiu on 2019/1/28.
 */

public class MyDIaLog extends Dialog {
    //    style引用style样式
    public MyDIaLog(Context context , View layout ) {

        super(context);

        setContentView(layout);

        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();

        params.gravity = Gravity.CENTER;
        params.width= WindowManager.LayoutParams.MATCH_PARENT;
        params.height= WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
}
