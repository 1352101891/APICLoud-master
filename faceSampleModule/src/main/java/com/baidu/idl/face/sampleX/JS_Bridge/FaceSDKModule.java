package com.baidu.idl.face.sampleX.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Vibrator;

import com.alibaba.fastjson.JSON;
import com.baidu.crabsdk.CrabSDK;
import com.baidu.idl.face.sampleX.ApiActivity;
import com.baidu.idl.facesdk.utils.PreferencesUtil;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.i;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/9.
 */

public class FaceSDKModule extends UZModule {
    private AlertDialog.Builder mAlert;
    private Vibrator mVibrator;
    private UZModuleContext mJsCallback;
    private final static int REQUEST_CODE=112;
    private static String error= "{info:\'没有回调任何信息！\'}";
    private static String error1= "{info:\'测试过期，请重新安装应用！\'}";
    private JSONObject object= null;
    private FaceProxy faceProxy;
    public FaceSDKModule(UZWebView webView) {
        super(webView);
        Loggger.init(this.getContext());
        PreferencesUtil.initPrefs(this.getContext());
        object=getObject(error);
        faceProxy=new FaceProxy(this);
        CrabSDK.init(this.getContext().getApplication(), "8bc935ee31c9b769");
    }



    /**
     * 打开原生activity
     * @param moduleContext
     */
    public void jsmethod_openFaceSDK(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,true);
            return;
        }
        String path=moduleContext.optString("name");
         // 意图实现activity的跳转
        Intent intent = new Intent(mContext, ApiActivity.class);
        intent.putExtra("path",path);
         // 这种启动方式：startActivity(intent);并不能返回结果
        startActivityForResult(intent,REQUEST_CODE);

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
        String code=moduleContext.optString("code");
        code=util.getNull(code);
        faceProxy.initSDK(code);
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
        path=util.getNull(path);
        name=util.getNull(name);
        faceProxy.registerFace(name,path);
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
        String framename=moduleContext.optString("framename");
        framename=util.getNull(framename);
        int[] a=new int[4];
        a[0]= moduleContext.optInt("x");//JS传入的x
        a[1]= moduleContext.optInt("y");//JS传入的y
        a[2] = moduleContext.optInt("w");//JS传入的w
        a[3]= moduleContext.optInt("h");//JS传入的h

        faceProxy.openFaceRecognition(a[0],a[1],a[2],a[3],framename,true);
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
        faceProxy.closeFace();
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
        if (null!=faceProxy){
            faceProxy.destroy();
        }
    }


}
