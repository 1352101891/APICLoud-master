package com.videolib.android.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Vibrator;
import android.text.util.Linkify;

import com.alibaba.fastjson.JSON;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.videolib.android.app.AppContext;
import com.worthcloud.avlib.bean.TFRemoteFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.videolib.android.JS_Bridge.util.dp2px;
import static com.videolib.android.JS_Bridge.util.getObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class VideoLiveSDKModule extends UZModule {

    private AppContext appConText;
    private AlertDialog.Builder mAlert;
    private UZModuleContext mJsCallback;
    private final static int REQUEST_CODE=112;
    private static String error= "{info:\'没有回调任何信息！\'}";
    private static String error1= "{info:\'测试过期，请重新安装应用！\'}";

    private static String openSuccess= "{alert:\'打开成功！\'}";
    private static String jieTu= "{alert:\'jieTu\'}";
    private static String shiJian= "{operate:\'shijian\'}";
    private static String huifang= "{operate:\'huifang\'}";
    private static String closeSuccess= "{alert:\'关闭成功\'}";

    private VideoProxy videoProxy;
    public VideoLiveSDKModule(UZWebView webView) {
        super(webView);
        appConText=AppContext.initApp(getContext().getApplication());
        videoProxy=new VideoProxy(this);
    }


    /**
     * 初始化SDK接口
     * * @param moduleContext
     */
    public void jsmethod_initMarchLive(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,false);
            return;
        }
    }


    /**
     * 挂载界面到任意h5标签上
     * * @param moduleContext
     */
    public void jsmethod_openMarchLive(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,false);
            return;
        }
        int[] a=new int[4];
        a[0]= moduleContext.optInt("x");//JS传入的x
        a[1]= dp2px(getContext(),moduleContext.optInt("y"));//JS传入的y
        a[2]= moduleContext.optInt("w");//JS传入的w
        a[3]= moduleContext.optInt("h");//JS传入的h
//        for (int i=0;i< a.length;i++){
//            a[i]=dp2px(getContext(),a[i]);
//        }

        String access_key=util.getNull(moduleContext.optString("access_key"));
        String secret_key=util.getNull(moduleContext.optString("secret_key"));
        String devCode=util.getNull(moduleContext.optString("devCode"));
        String userId=util.getNull(moduleContext.optString("userId"));
        String devPassword=util.getNull(moduleContext.optString("devPassword"));
        String framename=util.getNull(moduleContext.optString("framename"));
        videoProxy.initSDK(null,access_key,secret_key,devCode,userId,devPassword);
        videoProxy.openFragment(a[0],a[1],a[2],a[3],framename,true);
    }


    public void jsmethod_closeMarchLive(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,false);
            return;
        }
        videoProxy.closeFragment();
    }

    /**
     * 云台控制
     * @param moduleContext
     */
    public void jsmethod_changedevice(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,false);
            return;
        }
        String deviceid=util.getNull(moduleContext.optString("deviceid"));
        int direction=moduleContext.optInt("direction",0);

        videoProxy.changedevice(deviceid,direction);
    }

    /**
     * 视频配网
     * @param moduleContext
     */
    public void jsmethod_shengboConntionClick(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,false);
            return;
        }
        String wifiNameTF=util.getNull(moduleContext.optString("wifiNameTF"));
        String wifipasswordTF=util.getNull(moduleContext.optString("wifipasswordTF"));
        String userId=util.getNull(moduleContext.optString("userId"));
        String appId=util.getNull(moduleContext.optString("appId"));

        videoProxy.sendVoice(wifiNameTF,wifipasswordTF,userId,appId);
    }


    public void jsmethod_luzhiListAction(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        long startTime=Long.parseLong(util.getNull(moduleContext.optString("startTime")));
        long endTime=Long.parseLong(util.getNull(moduleContext.optString("endTime")));
        String devCode=util.getNull(moduleContext.optString("devCode"));
        String typeMask=util.getNull(moduleContext.optString("typeMask"));
        String devPassword=util.getNull(moduleContext.optString("devPassword"));
        videoProxy.getFirstData(devCode,devPassword,startTime,endTime);
    }

    public void jsmethod_playluzhiAction(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        String access_key=util.getNull(moduleContext.optString("access_key"));
        String secret_key=util.getNull(moduleContext.optString("secret_key"));
        String devCode=util.getNull(moduleContext.optString("devCode"));
        String userId=util.getNull(moduleContext.optString("userId"));
        String fileName=util.getNull(moduleContext.optString("fileName"));
        TFRemoteFile tfRemoteFile=new TFRemoteFile();
        tfRemoteFile.setFileName(fileName);
    }

    public void alertMess(String str){
        Map<String,String> map=new HashMap<>();
        map.put("message",str);
        String json= JSON.toJSONString(map);
        JSONObject jsonObject =  getObject(json);
        mJsCallback.success(jsonObject, false);
    }


    public void alertMess(JSONObject object){
        mJsCallback.success(object, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode== Activity.RESULT_OK  && requestCode==REQUEST_CODE){
            String path=  data.getStringExtra("path");
            if (path==null){
              //  mJsCallback.error(object,object,true);
            }else {
             //   alertMess(path);
            }
        }else {
            //mJsCallback.error(object,object,true);
        }
    }



    @Override
    protected void onClean() {
        if(null != mAlert){
            mAlert = null;
        }
        if(null != mJsCallback){
            mJsCallback = null;
        }
        if (null!=videoProxy){
            videoProxy.destroy();
        }
    }

}
