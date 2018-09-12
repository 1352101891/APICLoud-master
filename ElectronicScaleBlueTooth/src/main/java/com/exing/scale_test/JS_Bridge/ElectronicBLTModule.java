package com.exing.scale_test.JS_Bridge;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.exing.scale_test.activity.util;
import com.exing.sdk.BluetoothLeService;
import com.exing.sdk.DeviceType;
import com.exing.sdk.WeightData;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guan on 2018/6/10.
 */
public class ElectronicBLTModule extends UZModule {

    private BluetoothLeService mBluetoothLeService;
    private String appSecret;
    private String deviceName;
    private String appId;
	private int gender;
	private int age;
	private int height;
	private WeightData weightData;
    private static String error1= "{info:\'测试过期，请重新安装应用！\'}";
	//绑定蓝牙服务状态回调
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            if (!mBluetoothLeService.initialize()) {
            }
            // Automatically connects to the device upon successful start-up initialization.
            // Tim 自动连接，在绑定后，立即进行一次了
            configDevice();
            scanAndConnectDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private UZModuleContext mModuleContext;

    private void scanAndConnectDevice() {
        if (mBluetoothLeService == null) {
            // 没有正确初始化蓝牙
            return;
        }
        //搜索蓝牙
        mBluetoothLeService.scan();
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                // 数据通信
            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                // 蓝牙连接
                Log.i(TAG,"蓝牙电子秤已连接");
            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                // 蓝牙发送数据
                Log.i(TAG,"蓝牙发送数据");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // 蓝牙服务发现
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                // 蓝牙断开连接
                Log.i(TAG,"蓝牙断开连接");
                scanAndConnectDevice();
            } else if (BluetoothLeService.ACTION_DATA.equals(action)) {
                // 获取数据
                weightData = (WeightData) intent.getSerializableExtra(BluetoothLeService.EXTRA_DATA);
                Log.i(TAG,"蓝牙获取数据");
                parseWeightData();
            }
        }
    };

    private void parseWeightData(){

        if (weightData.getErrorCode() > 0){
            Log.i(TAG,"数据获取错误 : "+weightData.getErrorMessage());

            JSONObject ret = new JSONObject();
            try {
                ret.put("errorCode", weightData.getErrorCode());
                ret.put("errorMessage", weightData.getErrorMessage());
                ret.put("weight", "--");
                ret.put("fatRate", "--");
                ret.put("water", "--");
                ret.put("bmi", "--");
                ret.put("bmr", "--");
                ret.put("bone", "--");
                ret.put("bodyAge", "--");
                ret.put("muscle", "--");
                ret.put("vFat", "--");
                ret.put("protein", "--");
                ret.put("pxFatRate", "--");
                ret.put("proposalWeight", "--");
                ret.put("fatWeight", "--");
                ret.put("boneMuscleWeight", "--");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mModuleContext.success(ret, true);
            return;
        }

        //体重
        String weight = String.valueOf(weightData.getWeight());
        String fatRate = String.valueOf( weightData.getFatRate());
        String water = String.valueOf( weightData.getWaterRate());
        String bmi = String.valueOf(weightData.getBmi());
        String bmr = String.valueOf( weightData.getBmr());
        String bone = String.valueOf( weightData.getBoneWeight());
        String bodyAge = String.valueOf(weightData.getBodyAge());
        String muscle = String.valueOf( weightData.getMuscleWeight());
        String vFat = String.valueOf( weightData.getViscerafat());
        String protein = String.valueOf( weightData.getProtein());
        // 皮下脂肪
        String pxFatRate = String.valueOf(weightData.getPxFatRate());
        // 理想体重
        String proposalWeight = String.valueOf(weightData.getProposalWeight());
        // 脂肪量
        String fatWeight = String.valueOf(weightData.getFatWeight());
        // 骨骼肌
        String boneMuscleWeight = String.valueOf(weightData.getBoneMuscleWeight());

        Log.i(TAG,"数据获取成功");
        StringBuilder sb = new StringBuilder();
        sb .append("体重 = ").append(weight).append("\n")
                .append("体脂率 = ").append(fatRate).append("\n")
                .append("身体年龄 = ").append(bodyAge).append("\n")
                .append("肌肉量 = ").append(muscle).append("\n")
                .append("内脏脂肪等级 = ").append(vFat).append("\n");
        Log.i(TAG,sb.toString());

        JSONObject ret = new JSONObject();
        try {
            ret.put("errorCode", weightData.getErrorCode());
            ret.put("errorMessage", weightData.getErrorMessage());
            ret.put("weight", weight);
            ret.put("fatRate", fatRate);
            ret.put("water", water);
            ret.put("bmi", bmi);
            ret.put("bmr", bmr);
            ret.put("bone", bone);
            ret.put("bodyAge", bodyAge);
            ret.put("muscle", muscle);
            ret.put("vFat", vFat);
            ret.put("protein", protein);
            ret.put("pxFatRate", pxFatRate);
            ret.put("proposalWeight", proposalWeight);
            ret.put("fatWeight", fatWeight);
            ret.put("boneMuscleWeight", boneMuscleWeight);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mModuleContext.success(ret, true);
    }

    public ElectronicBLTModule(UZWebView webView) {
        super(webView);
    }

    public static final String TAG = "--------test-------";
    public void jsmethod_setInitialInfoAndScan(final UZModuleContext moduleContext){


        appId = moduleContext.optString("appId");
        appSecret = moduleContext.optString("appSecret");
        deviceName = moduleContext.optString("deviceName");
        age = Integer.valueOf(moduleContext.optString("age"));
        gender =  Integer.valueOf(moduleContext.optString("gender"));
        height =  Integer.valueOf(moduleContext.optString("height"));

        mModuleContext = moduleContext;
        Log.i(TAG,"setInitialInfoAndScan  "+ age);
        bind();

    }

	private void bind(){
		Log.i(TAG,"bind "+ Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			if (mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			    AlertDialog.Builder builder= new  AlertDialog.Builder(mContext);
				builder.setTitle("This app needs location access");
				builder.setMessage("Please grant location access so this app can detect beacons.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@TargetApi(Build.VERSION_CODES.M)
					@Override
					public void onDismiss(DialogInterface dialog) {
                        //requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
					}
				});
				builder.show();
			} else {
				Log.i(TAG,"bindService");
				bindService();
			}
		}else{
			Log.i(TAG,"6.0以下bindService");
			bindService();
		}
	}

    private void bindService() {
        if (mBluetoothLeService == null) {
            Log.i(TAG,"init bleServer");
            Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
            mContext.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mContext.registerReceiver(mGattUpdateReceiver, BluetoothLeService.makeGattUpdateIntentFilter());
        } else {
            Log.i(TAG,"init configDevice");
            configDevice();
            scanAndConnectDevice();
        }
    }

    private void configDevice(){
        if (mBluetoothLeService == null){
            // 没有正确初始化蓝牙
            return;
        }
        // 设置appId和appSecret
//        mBluetoothLeService.config("appId", "appSecret");
        mBluetoothLeService.config(appId, appSecret);
        // 设置设备类型和设备蓝牙名称
        mBluetoothLeService.setDeviceType(DeviceType.HTDevice, deviceName);
        // 设置用户信息，年龄、性别、身高， 性别男为1，女为0，身高单位为cm
        // 用户变化时需要重新设置用户信息
        mBluetoothLeService.setUserInfo(age,gender,height);
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
    protected void onClean() {

        if(null != mModuleContext){
            mModuleContext = null;
        }
        if (mGattUpdateReceiver!=null){
            mContext.unregisterReceiver(mGattUpdateReceiver);
        }

    }
}
