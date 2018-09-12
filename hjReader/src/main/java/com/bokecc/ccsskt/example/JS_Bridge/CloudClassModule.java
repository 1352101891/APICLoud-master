package com.bokecc.ccsskt.example.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.bokecc.ccsskt.example.activity.repair.LoadingActivity;
import com.bokecc.ccsskt.example.activity.repair.percentActivity;
import com.bokecc.ccsskt.example.entity.TimingEntity;
import com.google.gson.Gson;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import static com.bokecc.ccsskt.example.activity.repair.Constant.HORIZON;
import static com.bokecc.ccsskt.example.activity.repair.Constant.REQUEST_ACTIVITY;

/**
 * Created by Administrator on 2018/4/9.
 */

public class CloudClassModule extends UZModule {
    static final int ACTIVITY_REQUEST_CODE_A = 100;
    static int count=1;
    static String error= "{nickname:\'学生\',password:\'654321\'}";
    private AlertDialog.Builder mAlert;
    private Vibrator mVibrator;
    private UZModuleContext mJsCallback;
    private int direction= HORIZON;

    public CloudClassModule(UZWebView webView) {
        super(webView);
    }



    /**
     * {nickname:'嘿嘿',password:password,token:'url'
     * @param moduleContext
     */
    public void jsmethod_open(final UZModuleContext moduleContext){
        String url = moduleContext.optString("token");
        String id= moduleContext.optString("id");
        String password=moduleContext.optString("password");
        String nickname= moduleContext.optString("nickname");

        // 意图实现activity的跳转
        Intent intent = LoadingActivity.newIntent(getContext(),url,nickname,password);
        // 这种启动方式：startActivity(intent);并不能返回结果
        startActivityForResult(intent, REQUEST_ACTIVITY);

        this.mJsCallback=moduleContext;

    }

    public void jsmethod_jump(final UZModuleContext moduleContext){
        percentActivity.startSelf(getContext());
        this.mJsCallback=moduleContext;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null || data.getExtras()==null){
            return;
        }
        Bundle bundle=data.getExtras();
        TimingEntity entity= (TimingEntity) bundle.get("TimingEntity");
        Gson gson=new Gson();
        String json= gson.toJson(entity);
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (requestCode==REQUEST_ACTIVITY && resultCode== Activity.RESULT_OK){
            mJsCallback.success(jsonObject, true);
            Log.e("CloudClassModule",""+json);
        }else {
            JSONObject object=null;
            try {
                object=new JSONObject(error);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
