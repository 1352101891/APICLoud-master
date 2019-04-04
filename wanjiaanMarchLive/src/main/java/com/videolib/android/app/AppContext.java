package com.videolib.android.app;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.videolib.android.utils.Constant;
import com.videolib.android.utils.SharedPreferUtils;
import com.worthcloud.avlib.basemedia.MediaControl;
import com.worthcloud.avlib.listener.OperateListener;

import static android.Manifest.permission.READ_PHONE_STATE;
import static com.worthcloud.avlib.basemedia.MediaNativeReturnCode.E_ERROR_CODE_LICENSE_CHECK_FAILURE;
import static com.worthcloud.avlib.basemedia.MediaNativeReturnCode.E_ERROR_CODE_LICENSE_EXPIRED;
import static com.worthcloud.avlib.basemedia.MediaNativeReturnCode.E_ERROR_READ_PHONE_STATE;

/**
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/4/24
 */
public class AppContext {
    private int initStatus = 0;
    public final static int PERMISSIONS_CODE = 0x1113;
    public SharedPreferUtils sharedPreferUtils;
    public Application application;
    private static AppContext appContext=null;

    public static AppContext initApp(Application app){
        if (appContext==null) {
            synchronized(AppContext.class) {
                appContext=new AppContext(app);
            }
        }
        return appContext;
    }

    private AppContext(Application app) {
        MediaControl.getInstance().setIsShowLog(true);
        sharedPreferUtils = SharedPreferUtils.getInstanse(app);
        application=app;
        initAVLib();
    }


    public Application getApplication() {
        return application;
    }

    //Private Cloud
    public void initAVLib() {
        int type = sharedPreferUtils.getInt(Constant.SP_SERVER_STATUS);
        MediaControl.Server server;
        String defaultAccessKey = sharedPreferUtils.getString(Constant.SP_ACCESSKEY);
        String defaultSecretKey = sharedPreferUtils.getString(Constant.SP_SECRETKEY);
        String token = sharedPreferUtils.getString(Constant.SP_TOKEN);
        if (TextUtils.isEmpty(defaultAccessKey) || TextUtils.isEmpty(defaultSecretKey) || TextUtils.isEmpty(token)) {
            sharedPreferUtils.putString(Constant.SP_ACCESSKEY, Constant.ACCESSKEY);
            sharedPreferUtils.putString(Constant.SP_SECRETKEY, Constant.SECRETKEY);
            sharedPreferUtils.putString(Constant.SP_TOKEN, Constant.TOKEN);
        }
        if (type == 2) {
            String passServer = sharedPreferUtils.getString(Constant.SP_PASS_SERVER);
            int passServerPort = sharedPreferUtils.getInt(Constant.SP_PASS_SERVER_PROT);
            String turnServer = sharedPreferUtils.getString(Constant.SP_TURN_SERVER);
            int turnServerPort = sharedPreferUtils.getInt(Constant.SP_TURN_SERVER_PROT);
            String authUrl = sharedPreferUtils.getString(Constant.SP_AUTH_URL);
            if (!TextUtils.isEmpty(passServer) && !TextUtils.isEmpty(turnServer) && !TextUtils.isEmpty(authUrl) && passServerPort > 0 && turnServerPort > 0) {
                server = MediaControl.Server.DEFAULT.changeServer(passServer, passServerPort, turnServer, turnServerPort, authUrl);
            } else {
                sharedPreferUtils.putString(Constant.SP_PASS_SERVER, Constant.PASSSERVER);
                sharedPreferUtils.putString(Constant.SP_TURN_SERVER, Constant.TURNSERVER);
                sharedPreferUtils.putString(Constant.SP_AUTH_URL, Constant.AUTH_URL);
                sharedPreferUtils.putInt(Constant.SP_PASS_SERVER_PROT, Integer.parseInt(Constant.PASSSERVER_PROT));
                sharedPreferUtils.putInt(Constant.SP_TURN_SERVER_PROT, Integer.parseInt(Constant.TURNSERVER_PROT));
                server = MediaControl.Server.DEFAULT;
                sharedPreferUtils.putInt(Constant.SP_SERVER_STATUS, 0);
            }

        } else if (type == 1) {
            server = MediaControl.Server.DEBUG;
        } else {
            server = MediaControl.Server.DEFAULT;
        }
        MediaControl.getInstance().initialize(getApplication(), server, new OperateListener() {
            @Override
            public void success() {
                initStatus = 1;
            }

            @Override
            public void fail(long code) {
                if (code == E_ERROR_READ_PHONE_STATE) {
                    initStatus = 2;
                } else {
                    initStatus = 3;
                }
            }
        });
    }

    /*显示通知*/
    public void showToast(String text) {
        Toast.makeText(getApplication(), text, Toast.LENGTH_LONG).show();
    }

    /*检查初始化状态*/
    public boolean checkInitStatus(Activity activity) {
        if (initStatus == 1) {
            return true;
        }
        if (initStatus == 0) {
            showToast("SDK initialization");
        } else if (initStatus == 2) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_PHONE_STATE}, PERMISSIONS_CODE);
        } else {
            showToast("SDK initialization failure");
        }
        return false;
    }

    public void detach() {
        sharedPreferUtils.close();
        MediaControl.getInstance().unInitialize();
    }
}
