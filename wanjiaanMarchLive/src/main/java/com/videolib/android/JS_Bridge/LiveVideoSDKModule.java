package com.videolib.android.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Vibrator;

import com.alibaba.fastjson.JSON;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.videolib.android.app.AppContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/9.
 */

public class LiveVideoSDKModule extends UZModule {

    private AppContext appConText;
    private AlertDialog.Builder mAlert;
    private Vibrator mVibrator;
    private UZModuleContext mJsCallback;
    private final static int REQUEST_CODE=112;
    private static String error= "{info:\'没有回调任何信息！\'}";
    private static String error1= "{info:\'测试过期，请重新安装应用！\'}";
    private JSONObject object= null;
    private VideoProxy videoProxy;
    public LiveVideoSDKModule(UZWebView webView) {
        super(webView);
        object=getObject(error);
        appConText=AppContext.initApp(getContext().getApplication());
        videoProxy=new VideoProxy(this);
    }



    /**
     * 用户注册接口
     * @param moduleContext
     */
    public void jsmethod_registeFaceSDK(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,true);
            return;
        }
        String path=moduleContext.optString("path");
        String name=moduleContext.optString("name");

    }


    /**
     * 初始化SDK接口
     * * @param moduleContext
     */
    public void jsmethod_initFaceSDK(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,true);
            return;
        }
//        videoProxy.initSDK();
    }


    /**
     * 挂载人脸识别界面到任意h5标签上
     * * @param moduleContext
     */
    public void jsmethod_openFaceRecognition(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,true);
            return;
        }

        videoProxy.openFaceRecognition(0,0,1080,1920,"index_frm",true);
    }

    /**
     * 关闭人脸识别界面
     * * @param moduleContext
     */
    public void jsmethod_closeFaceRecognition(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,true);
            return;
        }
        videoProxy.closeFragment();
    }

    public void alertMess(String str){
        Map<String,String> map=new HashMap<>();
        map.put("message",str);
        String json= JSON.toJSONString(map);
        JSONObject jsonObject =  getObject(json);
        mJsCallback.success(jsonObject, true);
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

    public JSONObject getObject(String er){
        JSONObject object=null;
        try {
            object = new JSONObject(er);
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
        if (null!=videoProxy){
            videoProxy.destroy();
        }
    }

}
