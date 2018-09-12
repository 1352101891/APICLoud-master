package com.exing.scale_test.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.exing.sdk.BluetoothLeService;
import com.exing.sdk.DeviceType;
import com.exing.sdk.WeightData;

/**
 * Created by lvqiu on 2018/6/8.
 */

public class BLT_service extends Service {
    private static final String TAG = "BLT_service";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private int REQUEST_BLUETOOTH_CODE=1112;

    public static final int SHOW_DATA = 100;
    public static final int UNCONNECT = 110;
    public static final int CONNECTED = 120;

    private BluetoothLeService mBluetoothLeService;
    private WeightData weightData;

    private int gender;
    private int age;
    private int height;

    private String appId;
    private String appSecret;
    private String deviceName;

    @Override
    public void onCreate() {
        Log.i(TAG, "BLT_service-onCreate");
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //执行文件的下载或者播放等操作
        Log.i(TAG, "BLT_service-onStartCommand");
        /*
         * 这里返回状态有三个值，分别是:
         * 1、START_STICKY：当服务进程在运行时被杀死，系统将会把它置为started状态，但是不保存其传递的Intent对象，之后，系统会尝试重新创建服务;
         * 2、START_NOT_STICKY：当服务进程在运行时被杀死，并且没有新的Intent对象传递过来的话，系统将会把它置为started状态，
         *   但是系统不会重新创建服务，直到startService(Intent intent)方法再次被调用;
         * 3、START_REDELIVER_INTENT：当服务进程在运行时被杀死，它将会在隔一段时间后自动创建，并且最后一个传递的Intent对象将会再次传递过来。
         */
        //获取参数
        if (intent!=null){
            age= intent.getIntExtra(util.AGE,18);
            gender= intent.getIntExtra(util.GENDER,1);
            height=intent.getIntExtra(util.HEIGHT,170);
            appId= intent.getStringExtra(util.APPID);
            appSecret=intent.getStringExtra(util.APPSECRET);
            deviceName=intent.getStringExtra(util.DEVICENAME);
        }
        bindService();
        return super.onStartCommand(intent, flags, startId);
    }
    private void bindService() {
        if (mBluetoothLeService == null) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            registerReceiver(mGattUpdateReceiver, BluetoothLeService.makeGattUpdateIntentFilter());
        } else {
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
        mBluetoothLeService.config(appId, appSecret);
        // 设置设备类型和设备蓝牙名称
        mBluetoothLeService.setDeviceType(DeviceType.HTDevice, deviceName);
        // 设置用户信息，年龄、性别、身高， 性别男为1，女为0，身高单位为cm
        // 用户变化时需要重新设置用户信息
        mBluetoothLeService.setUserInfo(age,gender,height);
    }

    private void scanAndConnectDevice() {
        if (mBluetoothLeService == null) {
            // 没有正确初始化蓝牙
            return;
        }
        mBluetoothLeService.scan();
    }


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
            isregister=true;
            _SendBroadCastString("服务已经连接成功！");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    boolean isregister=false;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                // 数据通信
                _SendBroadCastString("进行数据通信！");
            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                _SendBroadCastString("体重秤连接成功！");
                // 蓝牙连接
                mHandler.sendMessage(mHandler.obtainMessage(CONNECTED));
            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                // 蓝牙发送数据
                _SendBroadCastString("蓝牙发送数据！");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // 蓝牙服务发现
                _SendBroadCastString("蓝牙服务发现！");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                // 蓝牙断开连接
                _SendBroadCastString("蓝牙断开连接！");
                mHandler.sendMessage(mHandler.obtainMessage(UNCONNECT));
                scanAndConnectDevice();
            } else if (BluetoothLeService.ACTION_DATA.equals(action)) {
                // 获取数据
                _SendBroadCastString("获取蓝牙数据！");
                weightData = (WeightData) intent.getSerializableExtra(BluetoothLeService.EXTRA_DATA);
                mHandler.sendMessage(mHandler.obtainMessage(SHOW_DATA));
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_DATA: {
                    showWeightData();
                    break;
                }
                case UNCONNECT: {
                    weightData=null;
                    showWeightData();
                    break;
                }
                case CONNECTED:{
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void showWeightData(){
        if (weightData==null){
            Intent intent = new Intent(util.BLT_NULL);
            sendBroadcast(intent);
            return;
        }
        if (weightData.getErrorCode() > 0){
            Intent intent = new Intent(util.BLT_ERROR);
            intent.putExtra(util.WEIGHTDATA,weightData);
            sendBroadcast(intent);
            return;
        }else {
            Intent intent = new Intent(util.BLT_DATA);
            intent.putExtra(util.WEIGHTDATA,weightData);
            sendBroadcast(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "BLT_service-onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "BLT_service-onDestroy");
        super.onDestroy();
        if (mBluetoothLeService!=null){
            unbindService(mServiceConnection);
        }
        if (mGattUpdateReceiver!=null && isregister){
            this.unregisterReceiver(mGattUpdateReceiver);
        }
    }


    public void _SendBroadCastString(String str){
        Intent intent=new Intent(util.STAUS);
        intent.putExtra(util.STAUS,str);
        sendBroadcast(intent);
    }
}
