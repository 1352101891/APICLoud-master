package com.ykan.sdk.example.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.waImageClip.activity.GuideActivity;
import com.example.waImageClip.activity.util;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/9.
 */

public class BanRemoteModule extends UZModule {
    private AlertDialog.Builder mAlert;
    private Vibrator mVibrator;
    private UZModuleContext mJsCallback;
    private final static int REQUEST_CODE=112;
    private static String error= "{info:\'没有选择图片\'}";
    private static String error1= "{info:\'测试过期，请重新安装应用！\'}";
    private JSONObject object= null;
    public BanRemoteModule(UZWebView webView) {
        super(webView);
        object=getObject(error);
    }



    /**
     * @param moduleContext
     */
    public void jsmethod_openImageClip(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,true);
            return;
        }
        String path=moduleContext.optString("imagePath");
         // 意图实现activity的跳转
        Intent intent = new Intent(mContext, GuideActivity.class);
        intent.putExtra("path",path);
         // 这种启动方式：startActivity(intent);并不能返回结果
        startActivityForResult(intent,REQUEST_CODE);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode== Activity.RESULT_OK  && requestCode==REQUEST_CODE){
            String path=  data.getStringExtra("path");
            if (path==null){
                mJsCallback.error(object,object,true);
            }else {
                Map<String,String> map=new HashMap<>();
                map.put("path",path);
                String json= JSON.toJSONString(map);
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("ImageClip","path:"+map.get("map"));
                mJsCallback.success(jsonObject, true);
            }
        }else {
            mJsCallback.error(object,object,true);
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
    }

}
