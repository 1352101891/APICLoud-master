package com.yunbao.phonelive;

import android.app.Application;
import android.content.Context;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;

import java.lang.ref.WeakReference;


/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext  {

    public static volatile AppContext sInstance=null;
    public static volatile Application mApplication=null;

    private AppContext() {
        onCreate();
    }

    public static AppContext getInstance( Application  Application) {
        if (sInstance == null) {
            synchronized (AppContext.class) {
                if (sInstance == null) {
                    if ( mApplication==null) {
                        mApplication =  Application;
                    }
                    sInstance = new AppContext();
                }
            }
        }
        return sInstance;
    }

    public void onCreate() {

        sInstance = this;
        if (mApplication!=null)
            CrashReport.initCrashReport(mApplication);
        TXLiveBase.setConsoleEnabled(true);
        TXLiveBase.setLogLevel(TXLiveConstants.LOG_LEVEL_ERROR);
    }

}
