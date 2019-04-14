package com.videolib.android.JS_Bridge;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.videolib.android.R;
import com.videolib.android.activity.TFVideoPlayActivity;
import com.videolib.android.app.AppContext;
import com.videolib.android.utils.Constant;
import com.worthcloud.avlib.basemedia.MediaControl;
import com.worthcloud.avlib.bean.EventMessage;
import com.worthcloud.avlib.bean.TFRemoteFile;
import com.worthcloud.avlib.bean.TFSearchPrm;
import com.worthcloud.avlib.event.eventcallback.BaseEventCallback;
import com.worthcloud.avlib.event.eventcallback.EventCallBack;
import com.worthcloud.avlib.event.eventcode.EventCode;
import com.worthcloud.avlib.utils.LogUtils;
import com.worthcloud.sdlib.SoundWaveUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import voice.Util;
import voice.encoder.VoicePlayer;
import voice.encoder.VoicePlayerListener;

/**
 * Created by lvqiu on 2019/1/30.
 */

public class VideoProxy  implements ScaleCallback,VoicePlayerListener,DirectionCallback,BaseEventCallback.OnEventListener {
    public AppContext appContext;
    private UZModule uzModule;
    private Context context;
    private Fragment fragment,backplayFragment;
    private FragmentManager fragmentManager;
    private String keyCode="";
    private int x,y,w,h;
    private ViewGroup floatview,backplayWindow;
    private int status;
    private List<TFRemoteFile> tfList=new ArrayList<>();
    private int page=0;
    boolean isRefresh=false;
    private int startPage=2;
    private int perPageNum=15;
    private int fileType=0;

    public VideoProxy(UZModule uzModule) {
        this.uzModule = uzModule;
        this.context=uzModule.getContext();
        appContext=AppContext.initApp(uzModule.getContext().getApplication());
        this.status=-1;
        EventCallBack.getInstance().addCallbackListener(this);
    }

    public void initSDK(String token,String accessKey,String secretKey,String devCode,String userid,String password){
        //appsecret初始化
        if ( TextUtils.isEmpty(accessKey) || TextUtils.isEmpty(secretKey)) {
            alert(MESSAGE,"TOKEN/ACCESSKEY/SECRETTKEY can not be empty");
            return;
        }
        appContext.sharedPreferUtils.putString(Constant.SP_TOKEN, token);
        appContext.sharedPreferUtils.putString(Constant.SP_ACCESSKEY, accessKey);
        appContext.sharedPreferUtils.putString(Constant.SP_SECRETKEY, secretKey);
        appContext.sharedPreferUtils.putString(Constant.SP_DEV_CODE, devCode);
        appContext.sharedPreferUtils.putString(Constant.SP_USERID, userid);
        appContext.sharedPreferUtils.putString(Constant.SP_DEV_PASSWORD, password);
        //自定义端口
    //    serverNum == 2 ? "Custom" : serverNum == 1 ? "Debug" : "Release")
//        if (checkedId == R.id.Config_Custom) {
//            serverStatusNum = 2;  //自定义服务器
//        } else if (checkedId == R.id.Config_Debug) {
//            serverStatusNum = 1; //debug服务器
//        } else if (checkedId == R.id.Config_Release) {
//            serverStatusNum = 0; //relase服务器
//        }

//        if (checkedId == R.id.Config_Token) {
//            linkStatusNum = 1;  //1是token验证
//        } else if (checkedId == R.id.Config_Key) {
//            linkStatusNum = 0; //0是key验证
//        }
        appContext.sharedPreferUtils.putInt(Constant.SP_LINK_STATUS, 0);
        appContext.sharedPreferUtils.putInt(Constant.SP_SERVER_STATUS, 0);

       // alert(MESSAGE,"Config has been saved");
        status=1;
    }


    public VideoFragment getArgsFragment(){
        VideoFragment videoFragment=new VideoFragment(this);
        return videoFragment;
    }

    /**
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param fixon 依附的 h5 页面，为空就是独立页面
     * @param fixed 是否依附页面，依附的话会随着页面滑动而滑动
     */
    public void openFragment(int x,int y,int w,int h,String fixon,boolean fixed){
        if (status!=1){
            ((VideoLiveSDKModule)uzModule).alertMess("还没初始化！");
            return;
        }else {
            this.x=x;
            this.y=y;
            this.w=w;
            this.h=h;
            if(floatview==null) {
                floatview = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.null_layout,null);
            }
            if (null==floatview.getParent()){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp.setMargins(0,0,0,0);
                if (fragment==null){
                    fragment=getArgsFragment();
                }
                if (fragmentManager==null){
                    fragmentManager= uzModule.getContext().getFragmentManager();
                }
                FrameLayout.LayoutParams containerLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                FrameLayout frameLayout= floatview.findViewById(R.id.rootview_fragment);
                containerLP.setMargins(x,y,0,0);
                frameLayout.setLayoutParams(containerLP);

                //通过回调设置fragment，在floatview没有添加到窗口之前是不能添加fragment的
                floatview.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        for (int i=0;i<floatview.getChildCount();i++){
                            if (floatview.getChildAt(i).getId()==R.id.rootview_fragment){
                                FragmentTransaction transaction= fragmentManager.beginTransaction();
                                transaction.add(R.id.rootview_fragment, fragment);
                                transaction.commit();
                            }
                        }
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {

                    }
                });

                //UI类模块都应该实现fixedOn和fixed，标识该UI模块是挂在window还是frame上，是跟随网页滚动还是不跟随滚动
                //fixedOn为frame的name值。
                //通常，fixedOn为空或者不传时，UI模块默认挂在window上，如果有值，则挂在名为fixedOn传入的值的frame上
                uzModule.insertViewToCurWindow(floatview, lp, null, fixed);
            }
        }
    }

    /**
     * 关闭视图
     */
    public void closeFragment(){
        if ( fragment!=null && floatview!=null){
            for (int i=0;i<floatview.getChildCount();i++){
                if (floatview.getChildAt(i).getId()==R.id.rootview_fragment){
                    FragmentTransaction transaction= fragmentManager.beginTransaction();
                    transaction.remove(fragment);
                    transaction.commit();
                }
            }
            uzModule.removeViewFromCurWindow(floatview);
        }
        fragment=null;
        floatview.removeAllViews();
        floatview=null;
    }


    public void destroy(){
        uzModule=null;
        context=null;
        SoundWaveUtils.destroyPlay();
        EventCallBack.getInstance().removeListener(this);
    }


    @Override
    public void spread() {
        FixPosition(0,0, Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    public void shrink() {
        FixPosition(0, this.y,Configuration.ORIENTATION_PORTRAIT);
    }

    @Override
    public void share() {

    }

    public void FixPosition(int x,int y,int mode){
        if (floatview==null){
            Log.e("StepProxy","窗口为空，需要重新初始化！");
            return;
        }
        if (null == floatview.getParent()) {
            Log.e("StepProxy","窗口丢失，需要重新初始化！");
            return;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,0,0,0);
        floatview.setLayoutParams(lp);

        FrameLayout.LayoutParams containerLP=null;
        if (mode==Configuration.ORIENTATION_LANDSCAPE){
            containerLP = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            containerLP.leftMargin=0;
            containerLP.topMargin=0;
        }else if (mode==Configuration.ORIENTATION_PORTRAIT){
            containerLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            containerLP.leftMargin=x;
            containerLP.topMargin=y;
        }

        FrameLayout frameLayout= (FrameLayout) floatview.findViewById(R.id.rootview_fragment);
        frameLayout.setLayoutParams(containerLP);

    }


    /**
     * Send sound wave
     */
    public void changedevice(String deviceid,int direction ) {
        if (TextUtils.isEmpty(deviceid) ){
            ((VideoLiveSDKModule)uzModule).alertMess("控制云台参数为空！");
        }
    }


    /**
     * Send sound wave
     */
    public void sendVoice(String wifiNameTF,String wifipasswordTF,String userId,String appId) {
        if (TextUtils.isEmpty(wifiNameTF) ||
                TextUtils.isEmpty(wifipasswordTF) ||
                TextUtils.isEmpty(userId) ||
                TextUtils.isEmpty(appId)){
            ((VideoLiveSDKModule)uzModule).alertMess("发送声浪，填写信息为空！");
        }
        try {
            SoundWaveUtils.sendSoundWave(wifiNameTF, wifipasswordTF, userId, appId, this);
        } catch (UnsupportedEncodingException e) {
           // alert(MESSAGE,"Convert abnormally");
            e.printStackTrace();
        } catch (IllegalArgumentException f) {
          //  alert(MESSAGE,"WIFI network/Password is too long");
            f.printStackTrace();
        }
    }

    @Override
    public void onPlayStart(VoicePlayer voicePlayer) {
        alert(OPERATION,"shengboBegin");
    }

    @Override
    public void onPlayEnd(VoicePlayer voicePlayer) {
        alert(OPERATION,"shengboEnd");
    }


    /**
     *
     * @param dir ,1上  2下 3左  4右
     */
    @Override
    public void changeDirection(int dir) {
        alert(OPERATION,dir);
    }



    public final static String OPERATION="operate";
    public final static String MESSAGE="message";
    public final static String ALERT="alert";
    public  void alert(String key,String value){
        String json="{'"+key+"':'"+value+"'}";
        JSONObject jsonObject=util.getObject(json);
        ((VideoLiveSDKModule)uzModule).alertMess(jsonObject);
    }
    public  void alert(String key,int value){
        String json="{'"+key+"':"+value+"}";
        JSONObject jsonObject=util.getObject(json);
        ((VideoLiveSDKModule)uzModule).alertMess(jsonObject);
    }

    private String deviceId,cameraPwd;
    private long startTimeSpan,endTimeSpan;
    /** 刷新列表
     * @param deviceId
     * @param cameraPwd
     * @param startTimeSpan
     * @param endTimeSpan
     */
    public void getFirstData(String deviceId,
                        String cameraPwd,
                        long startTimeSpan,
                        long endTimeSpan) {
        this.deviceId=deviceId;
        this.cameraPwd=cameraPwd;
        this.startTimeSpan=startTimeSpan;
        this.endTimeSpan=endTimeSpan;
        isRefresh = true;
        //Files generated from 0:00-24:00 are set to obtained. It is recommended to use 4 hours as the time point in the actual project (too long time will lead to incomplete documents)。
        //MediaControl.getInstance().p2pSearchTFRemoteFile(new TFSearchPrm(deviceID, 0, getTodayTime(0), getTodayTime(24)));
        MediaControl.getInstance().p2pGetTFRemoteFile(new TFSearchPrm(deviceId, cameraPwd, fileType, startTimeSpan,
                endTimeSpan, startPage, perPageNum));
    }

    /**
     * 获取更多列表
     * @param deviceId
     * @param cameraPwd
     * @param startTimeSpan
     * @param endTimeSpan
     */
    public void getMoreDate(String deviceId,
                         String cameraPwd,
                         long startTimeSpan,
                         long endTimeSpan) {
        MediaControl.getInstance().p2pGetTFRemoteFile(new TFSearchPrm(deviceId, cameraPwd, fileType,
                startTimeSpan, endTimeSpan, page, perPageNum));
    }

    public void openBackPlayFragment(long linkHandle,TFRemoteFile tfRemoteFile,String deviceID){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_LINK_HANDLER, linkHandle);
        intent.putExtra(Constant.INTENT_DATABEAN,tfRemoteFile);
        intent.putExtra(Constant.INTENT_STRING, deviceID);

        if (status!=1){
            ((VideoLiveSDKModule)uzModule).alertMess("还没初始化！");
            return;
        }else {
            if(backplayWindow==null) {
                backplayWindow = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.null_layout,null);
            }
            if (null==backplayWindow.getParent()){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                if (backplayFragment==null){
                    backplayFragment=new BackPlayVideoFragment(this,intent);
                }
                if (fragmentManager==null){
                    fragmentManager= uzModule.getContext().getFragmentManager();
                }
                FrameLayout.LayoutParams containerLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                FrameLayout frameLayout= backplayWindow.findViewById(R.id.rootview_fragment);
                frameLayout.setLayoutParams(containerLP);

                //通过回调设置fragment，在floatview没有添加到窗口之前是不能添加fragment的
                backplayWindow.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        for (int i=0;i<backplayWindow.getChildCount();i++){
                            if (backplayWindow.getChildAt(i).getId()==R.id.rootview_fragment){
                                FragmentTransaction transaction= fragmentManager.beginTransaction();
                                transaction.add(R.id.rootview_fragment, backplayFragment);
                                transaction.commit();
                            }
                        }
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {

                    }
                });

                //UI类模块都应该实现fixedOn和fixed，标识该UI模块是挂在window还是frame上，是跟随网页滚动还是不跟随滚动
                //fixedOn为frame的name值。
                //通常，fixedOn为空或者不传时，UI模块默认挂在window上，如果有值，则挂在名为fixedOn传入的值的frame上
                uzModule.insertViewToCurWindow(backplayWindow, lp, null, true);
            }
        }
    }

    public void closeBackPlayFragment(){
        if ( backplayFragment!=null && backplayWindow!=null){
            for (int i=0;i<backplayWindow.getChildCount();i++){
                if (backplayWindow.getChildAt(i).getId()==R.id.rootview_fragment){
                    FragmentTransaction transaction= fragmentManager.beginTransaction();
                    transaction.remove(backplayFragment);
                    transaction.commit();
                }
            }
            uzModule.removeViewFromCurWindow(backplayWindow);
        }
        backplayFragment=null;
        backplayWindow.removeAllViews();
        backplayWindow=null;
    }


    @Override
    public void onEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getMessageCode()) {
            //case EventCode.E_EVENT_CODE_MSG_REMOTE_FILE_SEARCH_SUCCESS:
            case EventCode.E_EVENT_CODE_MSG_REMOTEFILE_SEARCH_SUCCESS:
                List<TFRemoteFile> list = (List<TFRemoteFile>) eventMessage.getObject();
                if (list != null && !list.isEmpty()) {
                    for (TFRemoteFile t : list) {
                        LogUtils.debug("开始时间：" + t.getStartTime() + "----结束时间：" + t.getEndTime());
                    }
                }
                if (list == null || list.size() <= 0) {
                    alert(MESSAGE,"Blank list");
                } else {
                    if (isRefresh) {
                        isRefresh = false;
                        tfList.clear();
                        page=startPage;
                    }else {
                        page++;
                        getMoreDate(deviceId,cameraPwd,startTimeSpan,endTimeSpan);
                    }
                    if (list.size()>0) {
                        tfList.addAll(list);
                    }else {
                        String huifangdata=JSON.toJSONString(tfList);
                        alert("huifangdata",huifangdata);
                    }
                }
                break;
            case EventCode.E_EVENT_CODE_MSG_REMOTE_FILE_SEARCH_FAILURE:
                alert(MESSAGE,"Failed");
                break;
            case EventCode.E_EVENT_CODE_MSG_P2P_ERROR://P2P-发生异常
                alert(MESSAGE,"P2P Error");
                break;
            case EventCode.E_EVENT_CODE_CAMERAPWD_CHECK_FAILED:
                alert(MESSAGE,"Wrong password");
                break;
            default:
                break;
        }
    }


    /*Play*/
    private void openBackPlay(long linkHandle,TFRemoteFile tfRemoteFile,String deviceID ) {
        Intent intent = new Intent(uzModule.getContext(), BackplayVideoActivity.class);
        intent.putExtra(Constant.INTENT_LINK_HANDLER, linkHandle);
        intent.putExtra(Constant.INTENT_DATABEAN, tfRemoteFile);
        intent.putExtra(Constant.INTENT_STRING, deviceID);
        uzModule.getContext().startActivity(intent);
    }
}
