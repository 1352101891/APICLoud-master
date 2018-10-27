package com.lechange.demo.JS_Bridge;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.lechange.demo.R;
import com.lechange.demo.business.Business;
import com.lechange.demo.business.entity.ChannelInfo;
import com.lechange.demo.business.util.PermissionHelper;
import com.lechange.demo.login.SplashActivity;
import com.lechange.demo.mediaplay.fragment.MediaPlayOnlineFragment;

import java.util.ArrayList;
import java.util.List;

import static com.lechange.demo.JS_Bridge.constant.APPID;
import static com.lechange.demo.JS_Bridge.constant.APPSECRET;
import static com.lechange.demo.JS_Bridge.constant.PREKEY;
import static com.lechange.demo.JS_Bridge.constant.SERVERURL;
import static com.lechange.demo.JS_Bridge.constant.TOKEN;
import static com.lechange.demo.JS_Bridge.constant.URL;


/**
 * Created by lvqiu on 2018/9/6.
 */

public class StepProxy {
    private Context mActivity;
    private String appid,appsecret;
    private SharedPreferences sp;
    private UZModule uzModule;
    private View floatview;
    private int x,y,w,h;
    private String deviceCode="";

    public int getY() {
        return y;
    }

    public int getSCREEN_MODE() {
        return SCREEN_MODE;
    }

    private int SCREEN_MODE=Configuration.ORIENTATION_PORTRAIT;

    public StepProxy(Context activity , UZModule uzModule) {
        this.mActivity=activity;
        this.uzModule=uzModule;
    }

    public void InitDevice(String deviceCode){
        this.deviceCode=deviceCode;
    }

    public boolean InitAppID(String appid,String appsecret){
        if (!util.Isvalid(appid,appsecret)){
            return false;
        }
        this.appid=appid;
        this.appsecret=appsecret;
        sp =  mActivity.getSharedPreferences(PREKEY, SplashActivity.MODE_PRIVATE);

        //初始化需要的数据
        boolean flag= Business.getInstance().init(appid, appsecret, SERVERURL);
        if (flag){
            SharedPreferences.Editor editor=sp.edit();
            editor.putString(APPID, appid);
            editor.putString(APPSECRET, appsecret );
            editor.putString(URL, SERVERURL);
            editor.commit();
        }
        return flag;
    }

    public void InitTelnum(String telnum){
        if (!util.IsDeadline(mActivity) &&  !util.getValue(TOKEN,mActivity).equals("")){
            String userToken= util.getValue(TOKEN,mActivity);
            Business.getInstance().setToken(userToken);
            util.saveKeyValue(TOKEN,userToken,mActivity);
            loadChannelList();
        }else {
            Business.getInstance().userlogin(telnum, new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if(0 == msg.what) {
                        String userToken = (String) msg.obj;
                        Business.getInstance().setToken(userToken);
                        util.saveKeyValue(TOKEN,userToken,mActivity);
                        loadChannelList();
//                    util.Toast(constant.LOGIN_SUCCESS,mActivity);
                    } else {
                        if ( 1==msg.what){
                            util.Toast(mActivity.getResources().getString(R.string.user_nobind_err),mActivity);
                        }else {
                            String result = (String) msg.obj;
                            util.Toast(constant.LOGIN_ERROR+result,mActivity);
                        }

                    }
                }

            });
        }
    }


    boolean CFlag=false,SFlag=false,AFlag=false;
    public void loadChannelList() {

        final ArrayList<ChannelInfo>  mChannelInfoList = new ArrayList<ChannelInfo>();
        // 初始化数据
        Business.getInstance().getChannelList(new Handler()	{
            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(Message msg) {
                Business.RetObject retObject = (Business.RetObject) msg.obj;
                CFlag=true;
                if (msg.what == 0) {
                    mChannelInfoList.addAll((List<ChannelInfo>) retObject.resp);
                    //	mChannelInfoList = (List<ChannelInfo>) retObject.resp;
                    if(mChannelInfoList != null && mChannelInfoList.size() > 0){
                        printDevices(mChannelInfoList,"getChannelList");
                    } else{
                        //util.Toast(mActivity.getResources().getString(R.string.toast_device_no_devices),mActivity);
                    }
                }
                else {
                    util.Toast( retObject.mMsg,mActivity);
                }

            }
        });

        //国内支持共享设备和授权设备
        if(!Business.getInstance().isOversea){
            Business.getInstance().getSharedDeviceList(new Handler()	{
                @SuppressWarnings("unchecked")
                @Override
                public void handleMessage(Message msg) {
                    Business.RetObject retObject = (Business.RetObject) msg.obj;
                    SFlag=true;
                    if (msg.what == 0 && retObject.resp != null) {
                        mChannelInfoList.addAll((List<ChannelInfo>) retObject.resp);
                        //	mChannelInfoList = (List<ChannelInfo>) retObject.resp;
                        if(mChannelInfoList != null && mChannelInfoList.size() > 0){
                            printDevices(mChannelInfoList,"getSharedDeviceList");
                        } else{
                          //  util.Toast(mActivity.getResources().getString(R.string.devices_no_shared_device),mActivity);
                        }
                    }
                    else {
                        util.Toast(retObject.mMsg,mActivity);
                     }

                }
            });
            Business.getInstance().getBeAuthDeviceList(new Handler()	{
                @SuppressWarnings("unchecked")
                @Override
                public void handleMessage(Message msg) {
                    Business.RetObject retObject = (Business.RetObject) msg.obj;
                    AFlag=true;
                    if (msg.what == 0 && retObject.resp != null) {
                        mChannelInfoList.addAll((List<ChannelInfo>) retObject.resp);
                        //	mChannelInfoList = (List<ChannelInfo>) retObject.resp;
                        if(mChannelInfoList != null && mChannelInfoList.size() > 0){
                            printDevices(mChannelInfoList,"getBeAuthDeviceList");
                        } else{
                          //  util.Toast(mActivity.getResources().getString(R.string.devices_no_authorized_device),mActivity);
                        }
                    }
                    else {
                        util.Toast(retObject.mMsg,mActivity);
                    }
                }
            });
        }
    }

    public void GoToLiveVideo(final ArrayList<ChannelInfo> mChannelInfoList){
        // 启动实时视频
        final EditText et = new EditText(mActivity);
        if (mChannelInfoList.get(0).getEncryptMode() == 1 && mChannelInfoList.get(0).getEncryptKey() == null) {
            new AlertDialog.Builder(mActivity).setTitle(R.string.alarm_message_keyinput_dialog_title)
                    .setIcon(android.R.drawable.ic_dialog_info).setView(et)
                    .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            String  UUID=FiltDevice(mChannelInfoList);
                            if (!UUID.equals("")){
                                InitFragment(UUID);
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_nagative, null).show();
        } else {
            mChannelInfoList.get(0).setEncryptKey(et.getText().toString());
            InitFragment(mChannelInfoList.get(0).getUuid());
        }
    }

    public String FiltDevice(ArrayList<ChannelInfo>  mChannelInfoList){

        if (mChannelInfoList!=null && mChannelInfoList.size()>0){
            if (deviceCode.equals("")){
                return mChannelInfoList.get(0).getUuid();
            }
            for (ChannelInfo info: mChannelInfoList) {
                if (deviceCode.equals(info.getDeviceCode())){
                    if (info.getState()== ChannelInfo.ChannelState.Offline){
                        ((CloudClassModule)uzModule).offline();
                    }
                    return info.getUuid();
                }
            }
        }
        return "";
    }

    public synchronized void printDevices(ArrayList<ChannelInfo> mChannelInfoList, String methods ){

        if ( (CFlag&&AFlag&&AFlag)){
            if (mChannelInfoList != null && mChannelInfoList.size() > 0 ) {
                if (util.permissionCheck((Activity) mActivity)) {
                    GoToLiveVideo(mChannelInfoList);
                } else {
                    PermissionHelper.requestPermission((Activity) mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO});
                }
                for (int i = 0; i < mChannelInfoList.size(); i++) {
                    Log.e(methods, "DeviceCode:" + mChannelInfoList.get(i).getDeviceCode() + "," +
                            "Name:" + mChannelInfoList.get(i).getName() + "," + "status:" + mChannelInfoList.get(i).getState());
                }
            }else {
                util.Toast("没有绑定的设备！",mActivity);
            }
        }
        Log.e("printDevices", "_____________________________________");

    }
    boolean attach=false;
    MediaPlayOnlineFragment mediaPlayFragment;
    public void InitFragment(String UUID){
        if (!attach) {
            mediaPlayFragment = new MediaPlayOnlineFragment();
            mediaPlayFragment.setCallback(new ScaleCallback() {
                @Override
                public void spread() {
                    ((CloudClassModule)uzModule).spread();
                    FixPosition(0,0, Configuration.ORIENTATION_LANDSCAPE);
                }

                @Override
                public void shrink() {
                    ((CloudClassModule)uzModule).shrink();
                    FixPosition(0, StepProxy.this.y,Configuration.ORIENTATION_PORTRAIT);
                }

                @Override
                public void share() {
                    ((CloudClassModule)uzModule).share();
                }
            });
            attach = true;
            Bundle b = new Bundle();
            b.putString("RESID", UUID);
            mediaPlayFragment.setArguments(b);
            changeFragment(mediaPlayFragment, false);
        }
    }


    public void changeFragment(Fragment targetFragment, boolean isAddToStack) {
        if (isAddToStack) {
            ((Activity)mActivity).getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .addToBackStack(null).commitAllowingStateLoss();
        } else {
            ((Activity)mActivity).getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .commitAllowingStateLoss();
        }
    }

    public void CreateFloatView(int x,int y,int w,int h,String fixon,boolean fixed){
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
        if(floatview==null) {
            floatview = LayoutInflater.from(mActivity).inflate(R.layout.activity_main,null);

        }
        RelativeLayout.LayoutParams containerLP = new RelativeLayout.LayoutParams(w, h);
        containerLP.leftMargin=x;
        containerLP.topMargin=y;
        FrameLayout frameLayout= (FrameLayout) floatview.findViewById(R.id.fragment_container);
        frameLayout.setLayoutParams(containerLP);
        if (null==floatview.getParent()){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
            //UI类模块都应该实现fixedOn和fixed，标识该UI模块是挂在window还是frame上，是跟随网页滚动还是不跟随滚动
            //fixedOn为frame的name值。
            //通常，fixedOn为空或者不传时，UI模块默认挂在window上，如果有值，则挂在名为fixedOn传入的值的frame上
            uzModule.insertViewToCurWindow(floatview, lp, fixon, fixed);
        }
    }


    public void scaleAction(){
        if (mediaPlayFragment!=null){
            mediaPlayFragment.clickScaleButton();
        }
    }

    public void close(){
        if (mediaPlayFragment!=null){
            ((Activity)mActivity).getFragmentManager().beginTransaction().remove(mediaPlayFragment).commit();
        }
        if (floatview!=null) {
            uzModule.removeViewFromCurWindow(floatview);
        }
        floatview=null;
        uzModule=null;
        mActivity=null;
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
        RelativeLayout.LayoutParams containerLP=null;
        if (mode==Configuration.ORIENTATION_LANDSCAPE){
            containerLP = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }else {
            containerLP = new RelativeLayout.LayoutParams( w,h);
            containerLP.leftMargin=x;
            containerLP.topMargin=y;
        }

        FrameLayout frameLayout= (FrameLayout) floatview.findViewById(R.id.fragment_container);
        frameLayout.setLayoutParams(containerLP);
        SCREEN_MODE=mode;
    }

}
