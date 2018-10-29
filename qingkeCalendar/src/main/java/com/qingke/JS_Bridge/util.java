package com.qingke.JS_Bridge;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by lvqiu on 2018/6/2.
 */

public class util {


    private static final String TIME="time";
    private static   String SD_PATH  ;
    private static   String IN_PATH  ;
    private static long daymills=86400000;
    private static long mills=2000;


    public static JSONObject getJO(String json){
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * dp转换成px
     */
    public static int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    /**
     * px转换成dp
     */
    public static int px2dp(Context context,float pxValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale+0.5f);
    }
    /**
     * sp转换成px
     */
    public static int sp2px(Context context,float spValue){
        float fontScale=context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue*fontScale+0.5f);
    }
    /**
     * px转换成sp
     */
    public static int px2sp(Context context,float pxValue){
        float fontScale=context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue/fontScale+0.5f);
    }


    public static int[] getScreenSize(Context context){
        int[] size=new int[2];
        //3、获取屏幕的默认分辨率
        DisplayMetrics dm= context.getResources().getDisplayMetrics();
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }

    public static int ToInt(String s){
        String temp=Str(s);
        if (temp.length()==0){
            return 0;
        }else {
            return Integer.valueOf(temp);
        }
    }


    public static String Str(String s){
        if (s==null){
            return "";
        }
        return s;
    }

    public static boolean permissionCheck(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static void Toast(final String msg, final Context activity){
        ((Activity)activity).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean Isvalid(String appId,String appSecret)
    {
        appId=Str(appId);
        appSecret=Str(appSecret);
        if(appId.length()==0 || appSecret.length() == 0)
        {
            return false;
        }
        return true;
    }

    public static final Bitmap getBitmapByPath(Context context , String path)
            throws FileNotFoundException, IOException {

        File newFile = new File(path);
        Uri inputUri = FileProvider.getUriForFile( context, "com.lechange.demo.example.waImageClip.activity", newFile);
        InputStream input = context.getContentResolver().openInputStream(inputUri);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }


    public static final Bitmap getBitmap(ContentResolver cr, Uri url)
            throws FileNotFoundException, IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }


    public static boolean IsDeadline(Context context){
        String before = util.getString(context,TIME,"0");
        if (before.equals("0")){
            long beforetime= System.currentTimeMillis();
            before=  String.valueOf(beforetime);
            util.setString(context,TIME,before);
        }else {
            long deadline=new Long(before)+daymills;
            if (deadline<System.currentTimeMillis()){
                return true;
            }
        }
        return false;
    }

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }
    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        if (SD_PATH==null || IN_PATH==null){
            SD_PATH=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
            IN_PATH=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        }
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    // 自定义保存数据方法，将使用Sharepreference保存数据封装成方法
    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("timefile", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        // 和Map<key, value>一样保存数据，取数据也是一样简单
        edit.putString(key, value);
        edit.commit();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences("timefile", Context.MODE_PRIVATE);
        // 和Map<key, value>一样获取数据，存数据也是一样简单
        String value = sp.getString(key, defValue);
        return value;
    }
}
