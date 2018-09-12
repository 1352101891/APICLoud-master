package com.yunbao.phonelive.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.yunbao.phonelive.activity.JumpActivity;
import com.yunbao.phonelive.bean.VideoInfo;
import com.yunbao.phonelive.video.videorecord.TCVideoSettingActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/9.
 */

public class VideoRecordModule extends UZModule {
    private AlertDialog.Builder mAlert;
    private Vibrator mVibrator;
    private UZModuleContext mJsCallback;
    private final static int REQUEST_CODE=11112;

    public VideoRecordModule(UZWebView webView) {
        super(webView);
    }



    /**
     * @param moduleContext
     */
    public void jsmethod_openVideoRecord(final UZModuleContext moduleContext){

         // 意图实现activity的跳转
        Intent intent = new Intent(mContext, TCVideoSettingActivity.class);
         // 这种启动方式：startActivity(intent);并不能返回结果
        startActivityForResult(intent,REQUEST_CODE);

        this.mJsCallback=moduleContext;

    }

    static String error= "{info:\'没有选择文件\'}";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JSONObject object= null;
        try {
            object = new JSONObject(error);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (resultCode== Activity.RESULT_OK  && requestCode==REQUEST_CODE){
            VideoInfo videoInfo= (VideoInfo) data.getSerializableExtra("VideoInfo");
            if (videoInfo==null){
                mJsCallback.error(object,object,true);
            }else {
                String json= JSON.toJSONString(videoInfo);
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("VideoRecordModule","path:"+videoInfo.getVIDEPATH());
                mJsCallback.success(jsonObject, true);
            }
        }else {
            mJsCallback.error(object,object,true);
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
    }

}
