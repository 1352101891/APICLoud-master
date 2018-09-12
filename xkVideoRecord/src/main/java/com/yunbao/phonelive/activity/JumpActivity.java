package com.yunbao.phonelive.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.video.videorecord.TCVideoSettingActivity;

/**
 * Created by lvqiu on 2018/5/26.
 */

public class JumpActivity extends AbsActivity implements View.OnClickListener{
    ImageView imageView;
    private static final int REQUEST_LOCATION_PERMISSION = 100;//请求定位权限的请求码
    private final int REQUEST_VIDEO_PERMISSION = 101;

    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,JumpActivity.class);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void main() {
        imageView=(ImageView) findViewById(R.id.btn_live_choose);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_live_choose){
            startVideoRecord();
        }
    }

    public void startVideoRecord(){
        checkVideoPermission();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_VIDEO_PERMISSION:
                if (isAllGranted(permissions, grantResults)) {
                    startVideo();
                }
                break;
        }
    }

    /**
     * 检查并申请录制视频的权限
     */
    public void checkVideoPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.RECORD_AUDIO,
                                android.Manifest.permission.CAMERA,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        REQUEST_VIDEO_PERMISSION);
            } else {
                startVideo();
            }
        } else {
            startVideo();

        }
    }

    /**
     * 开启短视频
     */
    public void startVideo() {
        startActivity(new Intent(mContext, TCVideoSettingActivity.class));
    }



    //判断申请的权限有没有被允许
    private boolean isAllGranted(String[] permissions, int[] grantResults) {
        boolean isAllGranted = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                showTip(permissions[i]);
            }
        }
        return isAllGranted;
    }

    //拒绝某项权限时候的提示
    private void showTip(String permission) {
        switch (permission) {
            case android.Manifest.permission.READ_EXTERNAL_STORAGE:
                ToastUtil.show(getString(R.string.storage_permission_refused));
                break;
            case android.Manifest.permission.CAMERA:
                ToastUtil.show(getString(R.string.camera_permission_refused));
                break;
            case android.Manifest.permission.RECORD_AUDIO:
                ToastUtil.show(getString(R.string.record_audio_permission_refused));
                break;
        }
    }

}
