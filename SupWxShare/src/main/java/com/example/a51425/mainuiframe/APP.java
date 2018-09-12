package com.example.a51425.mainuiframe;


import android.app.Application;

import com.cyxk.wrframelibrary.utils.SharedPreferanceUtils;

/**
 * Created by XinYue on 2016/12/14.
 */

public class APP extends com.cyxk.wrframelibrary.base.APP {
    private static APP myApplication;
    private static Application application;

    public static  APP get_Instance(Application mapplication) {
        if (myApplication == null) {
            synchronized (com.cyxk.wrframelibrary.base.APP.class) {
                if (myApplication == null) {
                    getInstance(mapplication);
                    myApplication=new APP();
                    application=mapplication;
                    context=application.getApplicationContext();
                    SharedPreferanceUtils  sharedPreferanceUtils = SharedPreferanceUtils.getSp(mapplication);
                    onCreate();
                }
            }
        }
        return myApplication;
    }
}