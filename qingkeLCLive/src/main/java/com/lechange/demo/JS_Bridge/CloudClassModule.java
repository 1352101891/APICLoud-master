package com.lechange.demo.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.lechange.demo.business.Business;
import com.lechange.demo.login.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.lechange.demo.JS_Bridge.constant.APPID;
import static com.lechange.demo.JS_Bridge.constant.APPSECRET;
import static com.lechange.demo.JS_Bridge.constant.REQUEST_ACTIVITY;
import static com.lechange.demo.JS_Bridge.constant.TELNUM;
import static com.lechange.demo.JS_Bridge.util.getJO;


/**
 * Created by Administrator on 2018/4/9.
 */

public class CloudClassModule extends UZModule {

    static String error= "{key:内部错误}";
    static String error1= "{key:测试超时，请重新安装！}";
    static String offlinError="{operate:参数error}";
    static String shareResult="{operate:share}";
    static String openResult="{operate:open}";
    static String spreadResult="{operate:spread}";
    static String shrinkResult="{operate:shrink}";
    private AlertDialog.Builder mAlert;
    private UZModuleContext mJsCallback;
    private static final String RESULT="RESULT";
    private int[] size;
    public CloudClassModule(UZWebView webView) {
        super(webView);
        size= util.getScreenSize(mContext);
        proxy=new StepProxy(mContext,this);
        Log.d("CloudClassModule", constant.SERVERURL);
    }
    private StepProxy proxy;


    /**
     * {nickname:'嘿嘿',password:password,token:'url'
     * @param moduleContext
     */
    public void jsmethod_open(final UZModuleContext moduleContext){
        if (util.IsDeadline(getContext())){
            returnError(error1);
        }

        String appid= moduleContext.optString(APPID);
        String appsecret=moduleContext.optString(APPSECRET);
        String telnum= moduleContext.optString(TELNUM);

        // 意图实现activity的跳转
        SplashActivity.newIntent(getContext(),appid,appsecret,telnum);

        this.mJsCallback=moduleContext;

    }


    /**
     * 正式接口
     * @param moduleContext
     */
    public void jsmethod_openqingKeLCLive(final UZModuleContext moduleContext){
        if (util.IsDeadline(getContext())){
            returnError(error1);
        }
        int topHeight = util.ToInt(moduleContext.optString("topHeight"));//JS传入的h

        String token =moduleContext.optString("token");
        String devCode=moduleContext.optString("devCode");

        proxy.InitDevice(devCode);
        proxy.CreateFloatView(0,topHeight,size[0],size[1],null,false);
        Business.getInstance().setToken(token);
        proxy.loadChannelList();

    }


    /**
     * 返回缩屏
     */
    public void jsmethod_back(final UZModuleContext moduleContext){
        if (proxy!=null){
            if (proxy.getSCREEN_MODE()== Configuration.ORIENTATION_LANDSCAPE) {
                proxy.scaleAction();
            }
            if (proxy.getSCREEN_MODE()== Configuration.ORIENTATION_PORTRAIT) {
                return;
            }
        }
    }


    /**
     * 测试接口
     * @param moduleContext
     */
    public void jsmethod_openLcLive(final UZModuleContext moduleContext){
        if (util.IsDeadline(getContext())){
            returnError(error1);
        }


        String fixedOn = moduleContext.optString("fixedOn");
        //fixed参数标识UI模块是否跟随网页滚动
        boolean fixed = moduleContext.optBoolean("fixed", true);
        int x = moduleContext.optInt("x");//JS传入的x
        int y = moduleContext.optInt("y");//JS传入的y
        int w = moduleContext.optInt("w");//JS传入的w
        int h = moduleContext.optInt("h");//JS传入的h
        x= util.dp2px(mContext,x);
        y= util.dp2px(mContext,y);
        w= util.dp2px(mContext,w);
        h= util.dp2px(mContext,h);

        String appid = util.Str(moduleContext.optString("appid"));
        String appsecret= util.Str(moduleContext.optString("appsecret"));
        String telnum = util.Str(moduleContext.optString("telnum"));
        String devCode= util.Str(moduleContext.optString("devCode"));

        proxy.CreateFloatView(x,y,w,h,fixedOn,fixed);
        proxy.InitDevice(util.Str(devCode));

        appid= appid.length()==0?"lcf04b49d5c84d4c1a":appid;
        appsecret= appsecret.length()==0? "66e9671628ac4906b18721ae3842d4":appsecret;
        telnum= telnum.length()==0?"18852983257":telnum;

        if (proxy.InitAppID(appid,appsecret)){
            proxy.InitTelnum(telnum);
        }

        this.mJsCallback=moduleContext;
        mJsCallback.success(getJO(openResult),false);
    }



    /**
     * 关闭窗口
     * @param moduleContext
     */
    public void jsmethod_closeLcLive(final UZModuleContext moduleContext){
        if (proxy!=null) {
            proxy.close();
        }
        onClean();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null || data.getExtras()==null){
            return;
        }
        Bundle bundle=data.getExtras();
        Object entity=  bundle.get(RESULT);
        Gson gson=new Gson();
        String json= gson.toJson(entity);
        if (requestCode==REQUEST_ACTIVITY && resultCode== Activity.RESULT_OK){
            mJsCallback.success(getJO(json), true);
            Log.e("CloudClassModule",""+json);
        }else {
            returnError(error);
        }
    }


    public void shrink(){
        mJsCallback.success(getJO(shrinkResult),false);
    }
    public void spread(){
        mJsCallback.success(getJO(spreadResult),false);
    }
    public void offline(){
        mJsCallback.success(getJO(offlinError),false);
    }
    public void share(){
        mJsCallback.success(getJO(shareResult),false);
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
