package com.example.waImageClip.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

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

    public static final Bitmap getBitmapByPath(Context context , String path)
            throws FileNotFoundException, IOException {

        File newFile = new File(path);
        Uri inputUri = FileProvider.getUriForFile( context, "com.example.waImageClip.activity", newFile);
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


    public static boolean CheckDeadline(Context context){
//        String before =util.getString(context,TIME,"0");
//        if (before.equals("0")){
//            long beforetime= System.currentTimeMillis();
//            before=  String.valueOf(beforetime);
//            util.setString(context,TIME,before);
//        }else {
//            long deadline=new Long(before)+daymills;
//            if (deadline<System.currentTimeMillis()){
//                return false;
//            }
//        }
        return true;
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
