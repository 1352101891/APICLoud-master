package com.ykan.sdk.example.JS_Bridge;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;

import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.yaokan.sdk.api.YkanSDKManager;
import com.yaokan.sdk.giz.net.NetUtils;
import com.yaokan.sdk.ir.InitYkanListener;
import com.yaokan.sdk.model.YKUserAccountType;
import com.yaokan.sdk.utils.Logger;
import com.yaokan.sdk.wifi.DeviceConfig;
import com.yaokan.sdk.wifi.DeviceManager;
import com.yaokan.sdk.wifi.GizWifiCallBack;
import com.yaokan.sdk.wifi.listener.IDeviceConfigListener;
import com.ykan.sdk.example.Constant;
import com.ykan.sdk.example.R;
import com.ykan.sdk.example.YKWifiConfigActivity;

import java.util.ArrayList;
import java.util.List;

import static com.ykan.sdk.example.JS_Bridge.constants.toastError;

/**
 * Created by lvqiu on 2018/12/9.
 */

public class RemoteProxy implements IDeviceConfigListener {
    public Context context;
    public UZModule uzModule;
    private String TAG="RemoteProxy";
    public boolean show=false;
    private RemoteCallback remoteCallback;
    private String fillfull="请输入完整信息";
    List<GizWifiDevice> wifiDevices = new ArrayList<GizWifiDevice>();
    private List<String> deviceNames = new ArrayList<String>();
    private DeviceManager  mDeviceManager;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            constants.HandlerKey key = constants.HandlerKey.values()[msg.what];
            switch (key) {
                case TIMER_TEXT:
                    break;
                case START_TIMER:
                    break;
                case SUCCESSFUL:
                   // Toast.makeText(context, R.string.configuration_successful,  Toast.LENGTH_SHORT).show();
                case FAILED:
                  //  Toast.makeText( context, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };

    public RemoteProxy(UZModule uzModule,RemoteCallback callback) {
        this.uzModule = uzModule;
        this.context=uzModule.getContext();
        this.remoteCallback=callback;
        initListener();
    }

    /**
     * 初始化sdk
     * @param appid
     * @param initYkanListener
     */
    public void initSdk(String appid,InitYkanListener initYkanListener){
        fix_meta_data(appid);
        // 初始化SDK
        YkanSDKManager.init(context, initYkanListener);
    }

    /**
     * 注册用户
     * @param username
     * @param password
     */
    public void register(String username,String password){
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            DeviceManager.instanceDeviceManager(context).register(username, password, null, YKUserAccountType.YKUserNormal);
        } else {
            toast(fillfull);
        }
    }


    /**
     * 登陆
     * @param username
     * @param password
     */
    public void login(String username,String password){
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            DeviceManager.instanceDeviceManager(context).userLogin(username, password, YKUserAccountType.YKUserNormal);
        } else {
            toast(fillfull);
        }
    }

    /**
     * 获取设备列表
     */
    public void getDevices(){
        mDeviceManager = DeviceManager.instanceDeviceManager(context);
        mDeviceManager.setGizWifiCallBack(mGizWifiCallBack);
        update(mDeviceManager.getCanUseGizWifiDevice());
    }

    /**
     * 解绑设备
     */
    public void unbindDevice(GizWifiDevice device){
        if (device==null){
            toast("设备信息为空！");
            return;
        }
        GizWifiDevice mGizWifiDevice =null;
        if (wifiDevices!=null && wifiDevices.size()>0){
            for (GizWifiDevice g: wifiDevices) {
                if (g.getDid().equals(device.getDid())){
                    mGizWifiDevice=g;
                }
            }
        }
        if (mGizWifiDevice==null){
            toast("不存在该设备！");
            return;
        }
        // 已经绑定的设备才去解绑
        if (mGizWifiDevice.isBind()) {
            // 解绑该设备
            mDeviceManager.unbindDevice(mGizWifiDevice.getDid());
            update(mDeviceManager.getCanUseGizWifiDevice());
        } else {
            toast("该设备未绑定");
        }
        if (mGizWifiDevice.isSubscribed()) {
            mDeviceManager.setSubscribe(mGizWifiDevice, false);
        }
    }

    /**
     * 小苹果配网绑定
     */
    public void bindDevice(String name,String pwd){
        //实例化配置对象
        DeviceConfig deviceConfig = new DeviceConfig(context, this);
        String wifinetName = NetUtils.getCurentWifiSSID(context);
        if (TextUtils.isEmpty(pwd))
            pwd=deviceConfig.getPwdBySSID(wifinetName);
        //开始绑定
        deviceConfig.setPwdSSID(wifinetName, pwd);
        deviceConfig.startAirlink(wifinetName, pwd);
        //通知开始绑定
        ((BanRemoteModule)uzModule).progress(constants.HandlerKey.START_TIMER);
        ((BanRemoteModule)uzModule).progress(constants.HandlerKey.TIMER_TEXT);
        mHandler.sendEmptyMessage(YKWifiConfigActivity.HandlerKey.TIMER_TEXT.ordinal());
        mHandler.sendEmptyMessage(YKWifiConfigActivity.HandlerKey.START_TIMER.ordinal());
    }


    /**
     * 设备配网回调
     * @param gizWifiErrorCode
     * @param s
     * @param s1
     * @param s2
     */
    @Override
    public void didSetDeviceOnboarding(GizWifiErrorCode gizWifiErrorCode, String s, String s1, String s2) {
    }
    @Override
    public void didSetDeviceOnboardingX(GizWifiErrorCode result, GizWifiDevice gizWifiDevice) {
        if (GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING == result) {
            return;
        }
        Message message = new Message();
        if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
            message.obj = gizWifiDevice.getMacAddress();
            message.what =  constants.HandlerKey.SUCCESSFUL.ordinal();
            ((BanRemoteModule)uzModule).progress(constants.HandlerKey.SUCCESSFUL);
        } else {
            message.what =  constants.HandlerKey.FAILED.ordinal();
            message.obj = toastError(context, result);
            ((BanRemoteModule)uzModule).progress(constants.HandlerKey.FAILED);
        }
        mHandler.sendMessage(message);
    }


    /**
     * 登录，注册等回调
     */
    private void initListener() {
        DeviceManager.instanceDeviceManager(context).setGizWifiCallBack(new GizWifiCallBack() {

            @Override
            public void didBindDeviceCd(GizWifiErrorCode result, String did) {
                super.didBindDeviceCd(result, did);
            }

            @Override
            public void didTransAnonymousUser(GizWifiErrorCode result) {
                super.didTransAnonymousUser(result);
            }

            /** 用于用户登录的回调 */
            @Override
            public void userLoginCb(GizWifiErrorCode result, String uid, String token) {
                Logger.d(TAG, "didUserLogin result:" + result + " uid:" + uid + " token:" + token);
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {// 登陆成功
                    Constant.UID = uid;
                    Constant.TOKEN = token;
                    if (remoteCallback!=null){
                        remoteCallback.loginSuccess("登陆成功！");
                    }
                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_USER_NOT_EXIST) {// 用户不存在
                    toast(R.string.GIZ_OPENAPI_USER_NOT_EXIST);
                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_USERNAME_PASSWORD_ERROR) {//// 用户名或者密码错误
                    toast(R.string.GIZ_OPENAPI_USERNAME_PASSWORD_ERROR);
                } else {
                    toast("登陆失败，请重新登录");
                }
            }

            /** 用于用户注册的回调 */
            @Override
            public void registerUserCb(GizWifiErrorCode result, String uid, String token) {
                Logger.d(TAG, "registerUserCb result:" + result + " uid:" + uid + " token:" + token);
                /** 用于用户注册 */
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    if (remoteCallback!=null){
                        remoteCallback.registerSuccess("注册成功！");
                    }
                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_USERNAME_UNAVALIABLE) {
                    toast( "userName  unavaliabale");
                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_CODE_INVALID) {
                    toast( "验证码不正确");
                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_EMAIL_UNAVALIABLE) {
                    toast( "该邮箱已️注册或该邮箱无效");
                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_PHONE_UNAVALIABLE) {
                    toast( "该手机已️注册或该手机号码无效");
                } else {
                    toast( "注册失败，请重新注册");
                }

            }

            /** 用于发送验证码的回调 */
            @Override
            public void didRequestSendPhoneSMSCodeCb(GizWifiErrorCode result) {
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 请求成功
                } else {
                    // 请求失败
                }
            }

            /** 用于重置密码的回调 */
            @Override
            public void didChangeUserPasswordCd(GizWifiErrorCode result) {
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    toast("重置密码成功！");
                    // 请求成功
                } else {
                    // 请求失败
                }
            }
        });
    }


    /**
     * 获取设备列表回调
     */
    private GizWifiCallBack mGizWifiCallBack = new GizWifiCallBack() {

        @Override
        public void didUnbindDeviceCd(GizWifiErrorCode result, String did) {
            super.didUnbindDeviceCd(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, "解除绑定成功");
            } else {
                // 解绑失败
                Logger.d(TAG, "解除绑定失败");
            }
        }

        @Override
        public void didBindDeviceCd(GizWifiErrorCode result, String did) {
            super.didBindDeviceCd(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 绑定成功
                Logger.d(TAG, "绑定成功");
            } else {
                // 绑定失败
                Logger.d(TAG, "绑定失败");
            }
        }

        @Override
        public void didSetSubscribeCd(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
            super.didSetSubscribeCd(result, device, isSubscribed);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, (isSubscribed ? "订阅" : "取消订阅") + "成功");
            } else {
                // 解绑失败
                Logger.d(TAG, "订阅失败");
            }
        }

        @Override
        public void discoveredrCb(GizWifiErrorCode result,
                                  List<GizWifiDevice> deviceList) {
            Logger.d(TAG,
                    "discoveredrCb -> deviceList size:" + deviceList.size()
                            + "  result:" + result);
            switch (result) {
                case GIZ_SDK_SUCCESS:
                    Logger.e(TAG, "load device  sucess");
                    update(deviceList);
//                    if(deviceList.get(0).getNetStatus()==GizWifiDeviceNetStatus.GizDeviceOffline)
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 更新设备列表数据
     * @param gizWifiDevices
     */
    void update(List<GizWifiDevice> gizWifiDevices) {
        if (gizWifiDevices != null) {
            Log.e("DeviceListActivity", gizWifiDevices.size() + "");
        }
        if (gizWifiDevices == null) {
            deviceNames.clear();
        } else if (gizWifiDevices != null && gizWifiDevices.size() >= 1) {
            wifiDevices.clear();
            wifiDevices.addAll(gizWifiDevices);
            deviceNames.clear();
            for (int i = 0; i < wifiDevices.size(); i++) {
                deviceNames.add(wifiDevices.get(i).getProductName() + "("
                        + wifiDevices.get(i).getMacAddress() + ") "
                        + getBindInfo(wifiDevices.get(i).isBind()) + "\n"
                        + getLANInfo(wifiDevices.get(i).isLAN()) + "  " + getOnlineInfo(mDeviceManager,wifiDevices.get(i).getNetStatus()));
            }
        }
    }


    /**
     * 修改meta_data属性
     * @param appid
     */
    public void fix_meta_data(String appid){
        if (appid==null || appid.length()==0){
            return;
        }
        ApplicationInfo appi;
        try {
            appi = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            appi.metaData.putString("yk_key", appid);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }finally {
            Log.e(TAG,""+appid);
        }
    }

    public void toast(String s){
        if (remoteCallback!=null){
            remoteCallback.failed(s);
        }
        if (show)
            Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }

    public void toast(int s){
        if (remoteCallback!=null){
            remoteCallback.failed(context.getString(s));
        }
        if (show)
            Toast.makeText(context,context.getString(s),Toast.LENGTH_SHORT).show();
    }


    private String getBindInfo(boolean isBind) {
        String strReturn = "";
        if (isBind == true)
            strReturn = "已绑定";
        else
            strReturn = "未绑定";
        return strReturn;
    }

    private String getLANInfo(boolean isLAN) {
        String strReturn = "";
        if (isLAN == true)
            strReturn = "局域网内设备";
        else
            strReturn = "远程设备";
        return strReturn;
    }


    private String getOnlineInfo(DeviceManager mDeviceManager,GizWifiDeviceNetStatus netStatus) {
        String strReturn = "";
        if (mDeviceManager.isOnline(netStatus) == true)//判断是否在线的方法
            strReturn = "在线";
        else
            strReturn = "离线";
        return strReturn;
    }


    public interface RemoteCallback{
        void loginSuccess(String mss);
        void registerSuccess(String mss);
        void failed(String mss);
    }
}
