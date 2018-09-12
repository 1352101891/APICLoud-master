package com.bokecc.dwlivedemo_new;

import android.app.Application;
import android.content.Context;

import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.logging.LogHelper;
import com.bokecc.sdk.mobile.live.util.HttpUtil;
import com.bokecc.sdk.mobile.push.DWPushEngine;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.ref.WeakReference;

/**
 * @author CC
 */
public class DWApplication {
    private static WeakReference<Context> context;
    private static volatile DWApplication instance;
    private static volatile Application mApplication;

    private DWApplication() {
        onCreate();
    }

    public static DWApplication getInstance( Application  Application) {
        if (instance == null) {
            synchronized (DWApplication.class) {
                if (instance == null) {
                    if (context == null || mApplication==null) {
                        context = new WeakReference<Context>(Application);
                        mApplication =  Application;
                    }
                    instance = new DWApplication();
                }
            }
        }
        return instance;
    }
    public static int mAppStatus = -1; // 表示 force_kill

    public static final boolean RTC_AUDIO = false; // 是否使用语音连麦 （true-使用 ／ false-不使用，采用视频连麦）

    public static final boolean REPLAY_CHAT_FOLLOW_TIME = true; // 是否让回放的聊天内容随时间轴推进展示

    public static final boolean REPLAY_QA_FOLLOW_TIME = true; // 是否让回放的问答内容随时间轴推进展示

    public void onCreate() {
        HttpUtil.LOG_LEVEL = HttpUtil.HttpLogLevel.DETAIL;
        mAppStatus=0;
        // 异常统计
        CrashReport.initCrashReport(mApplication, "32421e5c07", true);
        // CC PUSH SDK
        DWPushEngine.init(mApplication, BuildConfig.LOG_FLAG);
        // 初始化日志记录模块
        LogHelper.getInstance().init(mApplication, true, null);
    }

    public static Context getContext() {
        return mApplication;
    }

}
