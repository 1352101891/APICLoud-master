package com.qingke.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.qingke.view.CalendarActivity;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2018/4/9.
 */

public class CloudClassModule extends UZModule {
    static String success= "{key:成功}";
    static String error= "{key:内部错误}";
    static String error1= "{key:测试超时，请重新安装！}";
    int REQUEST_ACTIVITY=111;
    private AlertDialog.Builder mAlert;
    private UZModuleContext mJsCallback;
    private static final String RESULT="RESULT";
    private int[] size;
    public CloudClassModule(UZWebView webView) {
        super(webView);

    }


    /**打开日历页面
     * @param moduleContext
     */
    public void jsmethod_qingkeCalendarCreatView(final UZModuleContext moduleContext){

        this.mJsCallback=moduleContext;
        JumpTo(new Bundle(), CalendarActivity.class);
    }


    public void JumpTo(Bundle bundle,Class<?> clazz){
        Intent intent=new Intent(getContext(),clazz);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null || data.getExtras()==null){
            return;
        }

        if (requestCode==REQUEST_ACTIVITY && resultCode== Activity.RESULT_OK){
            mJsCallback.success(getJO(success), true);
            Log.e("CloudClassModule",""+success);
        }else {
            returnError(error);
        }
    }

    public void returnError(String es){
        JSONObject object=null;
        try {
            object=new JSONObject(es);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsCallback.error(object,object,true);
    }

    public JSONObject getJO(String es){
        JSONObject object=null;
        try {
            object=new JSONObject(es);
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
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
