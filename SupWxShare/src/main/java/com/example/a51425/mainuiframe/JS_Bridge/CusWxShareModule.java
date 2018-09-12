package com.example.a51425.mainuiframe.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import com.example.a51425.mainuiframe.APP;
import com.example.a51425.mainuiframe.ui.activity.ShareProxy;
import com.example.a51425.mainuiframe.utils.ShareUtils;
import com.example.a51425.mainuiframe.utils.util;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import org.json.JSONException;
import org.json.JSONObject;
import static com.example.a51425.mainuiframe.utils.Constant.APPID;
import static com.example.a51425.mainuiframe.utils.Constant.PACKAGENAME;
import static com.example.a51425.mainuiframe.utils.Constant.SHAREDESC;
import static com.example.a51425.mainuiframe.utils.Constant.SHARELINK;
import static com.example.a51425.mainuiframe.utils.Constant.SHARE_IMG;
import static com.example.a51425.mainuiframe.utils.Constant.THUMB;
import static com.example.a51425.mainuiframe.utils.Constant.TITLE;
import static com.example.a51425.mainuiframe.utils.Constant.shareImgToSession;
import static com.example.a51425.mainuiframe.utils.Constant.shareImgToTimeline;
import static com.example.a51425.mainuiframe.utils.Constant.shareToSession;
import static com.example.a51425.mainuiframe.utils.Constant.shareToTimeline;
import static com.example.a51425.mainuiframe.utils.util.CheckDeadline;

/**
 * Created by Administrator on 2018/4/9.
 */

public class CusWxShareModule extends UZModule {
    private AlertDialog.Builder mAlert;
    private UZModuleContext mJsCallback;
    private final static int REQUEST_CODE=11112;

    private String shareTitle = "空";
    private String shareImageUrl = "null";
    private String shareContent = "空";
    private String jumpUrl = "null";
    private ShareProxy shareProxy;
    private String Imageurl = "http://bo.5173cdn.com/5173_2/data/201705/02/36/RQKowFknx1cAAAAAAACtQXcf_5g23.jpg";
    private final static String TAG="CusWxShareModule";

    public CusWxShareModule(UZWebView webView) {
        super(webView);
        APP.get_Instance(getContext().getApplication());
    }

    //检查是否存在分享的软件
    public void jsmethod_checkApkExist(final UZModuleContext moduleContext){
        String[] strs=ShareUtils.shareWXReadyRx(null);
        String error3=null;
        if (strs==null){
            error3= "{info:\'不存在可以分享的软件！\',key:\'false\'}";
        }else {
            error3= "{info:\'存在可以分享的软件！\',key:\'true\'}";
        }
        JSONObject object= getJSON(error3);
        moduleContext.error(object,object,true);
        Log.e(TAG,"jsmethod_checkApkExist");
    }


    //分享图片，到好友
    public void jsmethod_shareImgToSession(final UZModuleContext moduleContext){
        mJsCallback=moduleContext;
        NewProxy(moduleContext);
        NewIntent(shareImgToSession,moduleContext);
    }
    //分享图片到朋友圈
    public void jsmethod_shareImgToTimeline(final UZModuleContext moduleContext){
        mJsCallback=moduleContext;
        NewProxy(moduleContext);
        NewIntent(shareImgToTimeline,moduleContext);
    }


    /**
    //分享内容到好友
    */
    public void jsmethod_shareToSession(final UZModuleContext moduleContext){
        mJsCallback=moduleContext;
        NewProxy(moduleContext);
        NewIntent(shareToSession,moduleContext);
    }
    //分享内容到朋友圈
    public void jsmethod_shareToTimeline(final UZModuleContext moduleContext){
        mJsCallback=moduleContext;
        NewProxy(moduleContext);
        NewIntent(shareToTimeline,moduleContext);
    }


    public void NewProxy(UZModuleContext moduleContext){
        String appid= util.Str(moduleContext.optString(APPID));
        String packagename=util.Str(moduleContext.optString(PACKAGENAME));

        if (appid.equals("")&&packagename.equals("")){
            //使用默认的app进行微信分享
            shareProxy =new ShareProxy(getContext());
            Log.e(TAG,"使用默认app进行微信分享！");
        }else   if (!appid.equals("")&&!packagename.equals("")){
            //使用外部传入的appid进行分享
            shareProxy =new ShareProxy(getContext(),appid,packagename);
            Log.e(TAG,"使用外部传入的appid进行微信分享！");
        }else {
            JSONObject object= getJSON(error2);
            mJsCallback.error(object,object,true);
            Log.e(TAG,"传入参数有误！");
        }
    }


    public void NewIntent(int type,UZModuleContext moduleContext){
//        JSONObject object= getJSON(error1);
//        if (!CheckDeadline(getContext())){
//            mJsCallback.error(object,object,true);
//            return;
//        }
        // 意图实现activity的跳转
        if (type==shareToSession || type==shareToTimeline) {
            shareTitle = moduleContext.optString(TITLE);
            shareImageUrl = moduleContext.optString(THUMB);
            shareContent = moduleContext.optString(SHAREDESC);
            jumpUrl = moduleContext.optString(SHARELINK);
            shareProxy.initContent(type,shareTitle,shareImageUrl,shareContent,jumpUrl);
        }else {
            Imageurl= moduleContext.optString(SHARE_IMG);
            shareProxy.initImage(type,Imageurl);
        }
    }

    private static String error1= "{info:\'测试超时！\'}";
    private static String error= "{info:\'没有分享成功\'}";
    private static String error2= "{info:\'appid或者packagenam为空！\'}";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JSONObject object=getJSON(error);
        if (resultCode== Activity.RESULT_OK  && requestCode==REQUEST_CODE){

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
        if (shareProxy!=null){
            shareProxy=null;
        }
    }


    public JSONObject getJSON(String str){
        JSONObject object= null;
        try {
            object = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
