package com.example.waImageClip.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;



/**
 * Created by lvqiu on 2017/11/11.
 */

public class RequestPermissionHelper {

    public final static int  UPDATE_APP=111;
    public final static int REQUEST_COARSE_Permission=134;
    public final static int  REQUEST_GPS_Permission=132;

    public final static int GETPERMISSION=102;

    public final static int REQUEST_camera_Permission=113;
    public final static int REQUEST_audio_Permission=114;

    public final static int REQUEST_Read_Permission=151;
    public final static int  REQUEST_write_Permission=152;
    /**
     * 请求定位
     * @param context
     * @return
     */
    public static boolean RequestLocation(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int check_COARSE_Permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            int check_GPS_Permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            Log.e("RequestPermission", "请求权限之前");
            if (check_COARSE_Permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_Permission);
                Log.e("RequestPermission", "正在请求网络定位权限！");
            }
            if (check_GPS_Permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_Permission);
                Log.e("RequestPermission", "正在请GPS定位权限！");
            }
            if (check_COARSE_Permission == PackageManager.PERMISSION_GRANTED && check_GPS_Permission == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
    public static boolean isPermissionLocation(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int check_COARSE_Permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            int check_GPS_Permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            if (check_COARSE_Permission == PackageManager.PERMISSION_GRANTED && check_GPS_Permission == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }


    /**
     * 请求存储
     * @param context
     * @return
     */
    public static boolean RequestStore(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int  Permission_w = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int  Permission_r = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.e("RequestPermission", "请求权限之前");
            if (Permission_w != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_write_Permission);
                Log.e("RequestPermission", "正在请求权限！");
            }
            if (Permission_r != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_Read_Permission);
                Log.e("RequestPermission", "正在请权限！");
            }
            if (Permission_w == PackageManager.PERMISSION_GRANTED && Permission_r == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
    public static boolean isPermissionStore(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int  Permission_w = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int  Permission_r = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.e("RequestPermission", "请求权限之前");
            if (Permission_w == PackageManager.PERMISSION_GRANTED && Permission_r == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }



    /**
     * 请求摄像头
     * @param context
     * @return
     */
    public static boolean RequestCamera(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int  Permission_camare = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            int  Permission_audio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);

            Log.e("RequestPermission", "请求权限之前");
            if (Permission_camare != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, REQUEST_camera_Permission);
                Log.e("RequestPermission", "正在请求 摄像头权限！");
            }
            if ( Permission_audio!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_audio_Permission);
                Log.e("RequestPermission", "正在请求录音权限！");
            }

            if (Permission_camare == PackageManager.PERMISSION_GRANTED &&Permission_audio == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
    public static boolean isPermissionCamera(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int  Permission_camare = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            int  Permission_audio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
            Log.e("RequestPermission", "请求权限之前");
            if (Permission_camare == PackageManager.PERMISSION_GRANTED &&Permission_audio == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
}
