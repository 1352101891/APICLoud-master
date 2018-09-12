package com.exing.scale_test.JS_Bridge;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.exing.scale_test.activity.BLT_service;
import com.exing.scale_test.activity.util;
import com.exing.sdk.WeightData;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import org.json.JSONException;
import org.json.JSONObject;
import static com.exing.scale_test.activity.util.WEIGHTDATA;

/**
 * Created by Administrator on 2018/4/9.
 */

public class EleBLTModule extends UZModule {
    private AlertDialog.Builder mAlert;
    private Vibrator mVibrator;
    private UZModuleContext mJsCallback;
    private Intent intent ;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private int REQUEST_BLUETOOTH_CODE=1112;

    private static String error= "{info:\'没有获取到体重信息\'}";
    private static String error1= "{info:\'测试过期，请重新安装应用！\'}";
    private JSONObject object= null;
    public EleBLTModule(UZWebView webView) {
        super(webView);
        object=getObject(error);
        IntentFilter filter = new IntentFilter();
        filter.addAction(util.BLT_ERROR);
        filter.addAction(util.BLT_DATA);
        filter.addAction(util.BLT_NULL);
        mContext.registerReceiver( mDATAReceiver, filter );

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(util.COONECTED);
        filter1.addAction(util.GETBLTCONNECT);
        filter1.addAction(util.GETDATA);
        filter1.addAction(util.STAUS);
        mContext.registerReceiver(statusReceiver,filter1);
    }

    private final BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (util.COONECTED.equals(action)) {
                Toast.makeText(mContext,"已连接上服务！",Toast.LENGTH_SHORT).show();
            }else if (util.GETBLTCONNECT.equals(action)){
                Toast.makeText(mContext,"体重秤已经连接上！",Toast.LENGTH_SHORT).show();
            }else if (util.GETDATA.equals(action)){
                Toast.makeText(mContext,"已经获取了数据！",Toast.LENGTH_SHORT).show();
            }else if (util.STAUS.equals(action)){
                Toast.makeText(mContext,intent.getStringExtra(util.STAUS),Toast.LENGTH_SHORT).show();
            }
        }
    };


    private final BroadcastReceiver mDATAReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (util.BLT_ERROR.equals(action)) {
                BroadcastResult(Activity.RESULT_CANCELED,intent);
                Toast.makeText(mContext,"体重秤数据出错！",Toast.LENGTH_SHORT).show();
            }else if (util.BLT_DATA.equals(action)){
                BroadcastResult(Activity.RESULT_OK,intent);
                Toast.makeText(mContext,"体重秤数据正确！",Toast.LENGTH_SHORT).show();
            }else {
                BroadcastResult(Activity.RESULT_CANCELED,null);
                Toast.makeText(mContext,"没有获取到数据！",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String appId;
    private String appSecret;
    private String deviceName;
    private String age;
    private String gender;
    private String height;

    /**
     * @param moduleContext
     */
    public void jsmethod_setInitialInfoAndScan(final UZModuleContext moduleContext){
        this.mJsCallback=moduleContext;
        if (!util.CheckDeadline(getContext())){
            JSONObject err=getObject(error1);
            mJsCallback.error(err,err,true);
            return;
        }

        appId=moduleContext.optString(util.APPID);
        deviceName=moduleContext.optString(util.DEVICENAME);
        appSecret=moduleContext.optString(util.APPSECRET);
        age=moduleContext.optString(util.AGE);
        gender=moduleContext.optString(util.GENDER);
        height=moduleContext.optString(util.HEIGHT);

        requestBLT();
    }

    public void requestBLT(){
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BLUETOOTH_CODE);
    }


    //请求蓝牙权限
    public void RequestBLTPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int flag=mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if ( flag!= PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("蓝牙权限请求");
                builder.setMessage("请授予地理位置权限，否则功能无法使用！");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mContext.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RequestBLTPermission();
                            }
                        }, 2000);
                    }
                });
                builder.show();
                return;
            }else {
                startBLTService();
                return;
            }
        }else {
            startBLTService();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };


    public void startBLTService(){
        intent = new Intent(mContext, BLT_service.class);
        intent.putExtra(util.APPID,appId);
        intent.putExtra(util.DEVICENAME,deviceName);
        intent.putExtra(util.APPSECRET,appSecret);
        intent.putExtra(util.AGE,age);
        intent.putExtra(util.GENDER,gender);
        intent.putExtra(util.HEIGHT,height);
        mContext.startService(intent);

    }


    public void BroadcastResult(int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            WeightData weightData = (WeightData) data.getSerializableExtra(WEIGHTDATA);

            String json = JSON.toJSONString(weightData);
            JSONObject jsonObject = getObject(json);
            Log.e("onActivityResult", "WeightData:" + weightData.getDeviceName());
            mJsCallback.success(jsonObject, true);


        }else if (resultCode == Activity.RESULT_CANCELED && data != null) {
            WeightData weightData = (WeightData) data.getSerializableExtra(WEIGHTDATA);

            String err = "{info:'" + weightData.getErrorCode() + "-->" + weightData.getErrorMessage() + "}";
            JSONObject jsonObject = getObject(err);
            mJsCallback.error(jsonObject, jsonObject, true);

        }else {
            mJsCallback.error(object, object, true);
        }
    }

    public JSONObject getObject(String er){
        JSONObject object=null;
        try {
            object = new JSONObject(er);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    @Override
    protected void onClean() {
        if(null != mAlert){
            mAlert = null;
        }
        if(null != mJsCallback){
            mJsCallback = null;
        }
        if (mDATAReceiver!=null){
            mContext.unregisterReceiver(mDATAReceiver);
        }
        if (statusReceiver!=null){
            mContext.unregisterReceiver(statusReceiver);
        }
        if (intent!=null){
            mContext.stopService(intent);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_BLUETOOTH_CODE){
            //data始终是null,
            if (resultCode== Activity.RESULT_OK){
                RequestBLTPermission();
            }else {
                Toast.makeText(mContext,"你拒绝了打开蓝牙请求，将无法获取连接体重秤！",Toast.LENGTH_SHORT).show();
                requestBLT();
            }
            Log.e("onActivityResult",""+resultCode+";");
        }
    }



}
