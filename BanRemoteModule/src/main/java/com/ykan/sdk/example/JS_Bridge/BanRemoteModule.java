package com.ykan.sdk.example.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.google.gson.Gson;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.yaokan.sdk.ir.InitYkanListener;
import com.yaokan.sdk.wifi.DeviceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2018/4/9.
 */

public class BanRemoteModule extends UZModule implements RemoteProxy.RemoteCallback,InitYkanListener{
    private AlertDialog.Builder mAlert;
    private UZModuleContext mJsCallback;
    private final static int REQUEST_CODE=112;
    private static String error= "{info:\'没有选择图片\'}";
    private static String error1= "{info:\'测试过期，请重新安装应用！\'}";
    private static String message= "{registerCode:\"1\",errorMessage:\"error\" }";
    private JSONObject object= null;
    private RemoteProxy remoteProxy;
    private String TAG="BanRemoteModule";
    private boolean isInitSuccess=false;
    public BanRemoteModule(UZWebView webView) {
        super(webView);
        object=getObject(error);
        remoteProxy=new RemoteProxy(this,this);
    }

    /** 初始化
     * {appid:"15349258932063"}
     * @param moduleContext
     */
    public void jsmethod_moduleBeginAction(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        String appid= moduleContext.optString("appid","");
        remoteProxy.initSdk(appid,this);
    }
    /** 注册
     * {account:"123456789",password:"123456789"}
     * @param moduleContext
     */
    public void jsmethod_moduleRegist(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        String username= moduleContext.optString("account","");
        String password= moduleContext.optString("password","");
        remoteProxy.register(username,password);
    }

    /** 登陆
     * {account:"123456789",password:"123456789"}
     * @param moduleContext
     */
    public void jsmethod_modulelogin(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        String username= moduleContext.optString("account","");
        String password= moduleContext.optString("password","");
        remoteProxy.login(username,password);
    }


    public void progress( constants.HandlerKey key){
        switch (key){
            case START_TIMER:
                break;
            case TIMER_TEXT:
                break;
            case SUCCESSFUL:
                break;
            case FAILED:
                break;
        }
    }

    @Override
    public void loginSuccess(String mss) {
        mJsCallback.success(getJSONObject(mss),false);
    }

    @Override
    public void registerSuccess(String mss) {
        mJsCallback.success(getJSONObject(mss),false);
    }

    @Override
    public void failed(String mss) {
        mJsCallback.success(getJSONObject(mss),false);
    }

    @Override
    public void onInitStart() {
        Log.e(TAG,"onInitStart");
    }

    @Override
    public void onInitFinish(final int status,final String errorMsg) {
        Log.e(TAG,"onInitFinish");
        if (status == INIT_SUCCESS) {
            isInitSuccess = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mJsCallback.success(getJSONObject("SDK初始化成功"),false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<GizWifiDevice> gizWifiDevices = DeviceManager.instanceDeviceManager(mContext).getCanUseGizWifiDevice();
                            if (gizWifiDevices != null) {
                                Log.e("MainActivity", gizWifiDevices.size() + "");
                            }
                        }
                    }, 2000);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mJsCallback.success(getJSONObject(errorMsg),false);
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK  && requestCode==REQUEST_CODE){
            String path=  data.getStringExtra("path");
            if (path==null){
                mJsCallback.error(object,object,true);
            }else {
                String json= "{path:\""+path+"\"}";
                JSONObject jsonObject=getObject(json);
                mJsCallback.success(jsonObject, true);
            }
        }else {
            mJsCallback.error(object,object,true);
        }
    }

    public JSONObject getJSONObject(String str){
        String temp=message.replace("error",str);
        return getObject(temp);
    }

    public JSONObject getObject(String er){
        JSONObject object=null;
        try {
            object = new JSONObject(er);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public JSONObject getObject(Object er){
        JSONObject object=null;
        try {
            Gson gson=new Gson();
            String temp=gson.toJson(er);
            object = new JSONObject(temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }



    @Override
    protected void onClean() {
        if(null != mAlert){
            mAlert = null;
        }
        if(null != mJsCallback){
            mJsCallback = null;
        }
        if (remoteProxy!=null){
            if (remoteProxy.mHandler!=null){
                remoteProxy.mHandler.removeCallbacksAndMessages(null);
                remoteProxy.mHandler=null;
                remoteProxy=null;
            }
        }
    }

}
