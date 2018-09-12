package com.bokecc.ccsskt.example;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;

import com.bokecc.ccsskt.example.entity.ScreenEntity;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.interact.InteractSessionManager;

import com.bokecc.sskt.CCInteractSDK;
import com.bokecc.sskt.CCInteractSession;
import com.tencent.bugly.crashreport.CrashReport;
import java.io.File;
import java.lang.ref.WeakReference;


/**
 * 作者 ${郭鹏飞}.<br/>
 */
public class CCApplication {
    private static WeakReference<Context> context;
    private static volatile CCApplication instance;
    private static volatile Application mApplication;

    private CCApplication() {
        onCreate();
    }

    public static CCApplication getInstance( Application  Application) {
        if (instance == null) {
            synchronized (CCApplication.class) {
                if (instance == null) {
                    if (context == null || mApplication==null) {
                        context = new WeakReference<Context>(Application);
                        mApplication =  Application;
                    }
                    instance = new CCApplication();
                }
            }
        }
        return instance;
    }


    public static final boolean RTC_AUDIO = false; // 是否使用语音连麦 （true-使用 ／ false-不使用，采用视频连麦）

    public static final boolean REPLAY_CHAT_FOLLOW_TIME = true; // 是否让回放的聊天内容随时间轴推进展示

    public static final boolean REPLAY_QA_FOLLOW_TIME = true; // 是否让回放的问答内容随时间轴推进展示


    private static final String TAG = "CCApp";

    public static int mAppStatus = -1; // 表示 force_kill


    public static int sClassDirection = 0; // 课堂模式（方向）0竖屏 1横屏
    public static ScreenEntity screenEntity;
    public static long mNamedTimeEnd = 0L;
    public static int mNamedTotalCount = 0;
    public static int mNamedCount = 0;

    public static boolean isConnect = false;

    private int mActivityCount = 0, mCount;

    public static String getCacheDir() {
        return CacheDir;
    }

    private static String CacheDir;
    public  void onCreate() {


        //2、通过Resources获取
        DisplayMetrics dm = mApplication.getResources().getDisplayMetrics();
        screenEntity=new ScreenEntity();
        screenEntity.height = dm.heightPixels;
        screenEntity.width = dm.widthPixels;

        CacheDir=mApplication.getCacheDir().getAbsolutePath()+ File.separator;
        AudioManager audioManager = (AudioManager) mApplication.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(false);

        Log.e("VERSION.SDK",""+android.os.Build.VERSION.SDK);


        CCInteractSDK.init(mApplication.getApplicationContext(), true);
        CrashReport.initCrashReport(mApplication, "99e9d291d1", true);

        mApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.i(TAG, "onActivityCreated: " );
                mActivityCount++;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.i(TAG, "onActivityStarted: " );
                if (mCount == 0) {
                    Log.i(TAG, "**********切到前台**********");
                    CCInteractSession.getInstance().switchCamera(null, new CCInteractSession.AtlasCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CCInteractSession.getInstance().enableVideo(false);
                        }

                        @Override
                        public void onFailure(String err) {

                        }
                    });
                }
                mCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i(TAG, "onActivityResumed: " );
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i(TAG, "onActivityPaused: " );
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.i(TAG, "onActivityStopped: " );
                mCount--;
                if (mCount == 0) {
                    Log.i(TAG, "**********切到后台**********");
                    CCInteractSession.getInstance().disableVideo(false);
                    CCInteractSession.getInstance().switchCamera(null, null);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.i(TAG, "onActivitySaveInstanceState: " );
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.i(TAG, "onActivityDestroyed: ");
                mActivityCount--;
                if (mActivityCount < 0) {
                    InteractSessionManager.getInstance().reset();
                    CCInteractSession.getInstance().releaseAll();
                }
            }
        });
    }

    public static Context getContext() {
        return context == null ? null : context.get();
    }

    public static String getVersion() {
        try {
            PackageManager manager = context.get().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.get().getPackageName(), 0);
            return "v" + info.versionName;
        } catch (Exception e) {
            return "v" + Config.APP_VERSION;
        }
    }

    public void clear(){
        if (mActivityCount<0) {
            if (context != null)
                context.clear();
            context = null;
            mApplication = null;
        }
    }


}
