package com.bokecc.dwlivedemo_new.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import com.bokecc.dwlivedemo_new.activity.LoginActivity;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

import static com.bokecc.dwlivedemo_new.util.constant.LiveName;
import static com.bokecc.dwlivedemo_new.util.constant.LivePassword;
import static com.bokecc.dwlivedemo_new.util.constant.LiveRoomid;
import static com.bokecc.dwlivedemo_new.util.constant.LiveUid;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayLiveid;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayName;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayPassword;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayRecordid;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayRoomid;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayUid;


/**
 * Created by Administrator on 2018/4/9.
 */

public class ReplayVideoModule extends UZModule {
    static final int ACTIVITY_REQUEST_CODE_A = 100;
    static int count=1;

    private AlertDialog.Builder mAlert;
    private Vibrator mVibrator;
    private UZModuleContext mJsCallback;


    public ReplayVideoModule(UZWebView webView) {
        super(webView);
    }



    /**云回放
     * @param moduleContext
     */
    public void jsmethod_open(final UZModuleContext moduleContext){

        String Uid=moduleContext.optString(ReplayUid );
        String Roomid=moduleContext.optString(ReplayRoomid );
        String Liveid=moduleContext.optString(ReplayLiveid );
        String Recordid=moduleContext.optString(ReplayRecordid );
        String Name=moduleContext.optString(ReplayName );
        String Password=moduleContext.optString(ReplayPassword );
        LoginActivity.startSelf(getContext(),Uid,Roomid,Liveid,Recordid,Name,Password);
    }


    /**
     *   LiveUid: "4CFFF48C38F9C58B",
         LiveRoomid: "0F410A3248E60DE49C33DC5901307461",
         LiveName: "直播观众昵称",
         LivePassword: " 123456789"
     * @param moduleContext
     */
    public void jsmethod_openLive(final UZModuleContext moduleContext){

        String Uid=moduleContext.optString(LiveUid );
        String Roomid=moduleContext.optString(LiveRoomid );
        String name=moduleContext.optString(LiveName );
        String password=moduleContext.optString(LivePassword );

        LoginActivity.startSelf(getContext(),Uid,Roomid,name,password);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == ACTIVITY_REQUEST_CODE_A){
            String result = data.getStringExtra("result");
            if(null != result && null != mJsCallback){
                try {
                    JSONObject ret = new JSONObject(result);
                    mJsCallback.success(ret, true);
                    mJsCallback = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
