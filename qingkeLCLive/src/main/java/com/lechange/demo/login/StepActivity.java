package com.lechange.demo.login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.lechange.demo.JS_Bridge.util;
import com.lechange.demo.R;
import com.lechange.demo.business.Business;
import com.lechange.demo.business.entity.ChannelInfo;
import com.lechange.demo.business.util.PermissionHelper;
import com.lechange.demo.mediaplay.fragment.MediaPlayFragment;
import com.lechange.demo.mediaplay.fragment.MediaPlayOnlineFragment;

import java.util.ArrayList;
import java.util.List;

public class StepActivity extends FragmentActivity implements MediaPlayFragment.BackHandlerInterface{
    private String appid="lcf04b49d5c84d4c1a",appsecret="66e9671628ac4906b18721ae3842d4";
    private String telnum="15170935990";
    private String serverUrl="openapi.lechange.cn:443";

    private final ArrayList<ChannelInfo> mChannelInfoList = new ArrayList<ChannelInfo>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

    }

    public void Init(){
        //初始化需要的数据
        boolean flag= Business.getInstance().init(appid,appsecret, serverUrl);
        if (flag){
            InitTelnum();
        }
    }

    public void InitTelnum(){
        Business.getInstance().userlogin(telnum, new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(0 == msg.what) {
                    String userToken = (String) msg.obj;
                    Business.getInstance().setToken(userToken);
                    getDevices();
                } else  {
                    if(1 == msg.what) {
                        Toast.makeText(getApplicationContext(),"当前未绑定手机号！需要绑定手机号",Toast.LENGTH_SHORT).show();
                    }else{
                        String result = (String)msg.obj;
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    public void getDevices(){

        // 初始化数据
        Business.getInstance().getChannelList(new Handler()	{
            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(Message msg) {

                Business.RetObject retObject = (Business.RetObject) msg.obj;
                if (msg.what == 0) {
                    mChannelInfoList.addAll((List<ChannelInfo>) retObject.resp);
                    if(mChannelInfoList != null && mChannelInfoList.size() > 0){
                        printDevices(mChannelInfoList,"getChannelList");
                    } else{
                        Log.e("getChannelList","没有设备");
                        Toast.makeText(StepActivity.this, R.string.toast_device_no_devices, Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Log.e("getChannelList","错误"+retObject.mMsg);
                    Toast.makeText(StepActivity.this,retObject.mMsg, Toast.LENGTH_LONG).show();
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
                    if (msg.what == 0 && retObject.resp != null) {
                        mChannelInfoList.addAll((List<ChannelInfo>) retObject.resp);
                        //	mChannelInfoList = (List<ChannelInfo>) retObject.resp;
                        if(mChannelInfoList != null && mChannelInfoList.size() > 0){
                            printDevices(mChannelInfoList,"getSharedDeviceList");
                        } else{
                            Log.e("getSharedDeviceList","没有分享设备");
                            Toast.makeText(StepActivity.this, R.string.devices_no_shared_device, Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Log.e("getSharedDeviceList","错误"+retObject.mMsg);
                        Toast.makeText(StepActivity.this, retObject.mMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Business.getInstance().getBeAuthDeviceList(new Handler()	{
                @SuppressWarnings("unchecked")
                @Override
                public void handleMessage(Message msg) {

                    Business.RetObject retObject = (Business.RetObject) msg.obj;
                    if (msg.what == 0 && retObject.resp != null) {
                        mChannelInfoList.addAll((List<ChannelInfo>) retObject.resp);
                        //	mChannelInfoList = (List<ChannelInfo>) retObject.resp;
                        if(mChannelInfoList != null && mChannelInfoList.size() > 0){
                            printDevices(mChannelInfoList,"getBeAuthDeviceList");
                        } else{
                            Log.e("getBeAuthDeviceList","没有授权设备");
                            Toast.makeText(StepActivity.this, R.string.devices_no_authorized_device, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Log.e("getBeAuthDeviceList","错误"+retObject.mMsg);
                        Toast.makeText(StepActivity.this, retObject.mMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public void GoToLiveVideo(final ArrayList<ChannelInfo> mChannelInfoList){
        // 启动实时视频
        final EditText et = new EditText(StepActivity.this);
        if (mChannelInfoList.get(0).getEncryptMode() == 1 && mChannelInfoList.get(0).getEncryptKey() == null) {
            new AlertDialog.Builder(StepActivity.this).setTitle(R.string.alarm_message_keyinput_dialog_title)
                    .setIcon(android.R.drawable.ic_dialog_info).setView(et)
                    .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            mChannelInfoList.get(0).setEncryptKey(et.getText().toString());
                            InitFragment(mChannelInfoList.get(0).getUuid());
                        }
                    })
                    .setNegativeButton(R.string.dialog_nagative, null).show();
        } else {
            mChannelInfoList.get(0).setEncryptKey(et.getText().toString());
            InitFragment(mChannelInfoList.get(0).getUuid());
        }
    }

    public synchronized void printDevices(ArrayList<ChannelInfo> mChannelInfoList, String methods ){

        if (util.permissionCheck(this)) {
            GoToLiveVideo(mChannelInfoList);
        } else {
            PermissionHelper.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO});
        }
        if (mChannelInfoList != null && mChannelInfoList.size() > 0) {
            for (int i = 0; i < mChannelInfoList.size(); i++) {
                Log.e(methods, "DeviceCode:" + mChannelInfoList.get(i).getDeviceCode() + "," +
                        "Name:" + mChannelInfoList.get(i).getName() + "," + "status:" + mChannelInfoList.get(i).getState());
            }
        }
        Log.e("printDevices", "_____________________________________");

    }
    boolean attach=false;
    MediaPlayOnlineFragment mediaPlayFragment;
    public void InitFragment(String UUID){
        if (!attach) {
            mediaPlayFragment = new MediaPlayOnlineFragment();
            attach = true;
            Bundle b = new Bundle();
            b.putString("RESID", UUID);
            mediaPlayFragment.setArguments(b);
            changeFragment(mediaPlayFragment, false);
        }
    }


    public void changeFragment( Fragment targetFragment, boolean isAddToStack) {
        if (isAddToStack) {
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .addToBackStack(null).commitAllowingStateLoss();
        } else {
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .commitAllowingStateLoss();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for ( int re: grantResults) {
            if (re==PackageManager.PERMISSION_GRANTED){
                continue;
            }else {
                Toast.makeText(getApplication(),"权限不足！",Toast.LENGTH_SHORT).show();
                PermissionHelper.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO});
                return;
            }
        }
        GoToLiveVideo(mChannelInfoList);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void setSelectedFragment(MediaPlayFragment backHandledFragment) {
        Log.e("StepActivity","MediaPlayFragment已经初始化！");
    }
}
