package com.videolib.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.videolib.android.R;
import com.videolib.android.app.AppContext;
import com.videolib.android.utils.Constant;
import com.worthcloud.avlib.basemedia.MediaControl;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText deviceID, userId;
    private AppContext appContext;
    public boolean NoPermissions;

    protected void onCreate(  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppContext.initApp(getApplication());

        initView();
        deviceID = (EditText) findViewById(R.id.Main_DeviceUUID);
        userId = (EditText) findViewById(R.id.Main_ET_UserId);
        deviceID.setText(Constant.TEST_UUID);
        userId.setText(Constant.USERID);
    }


    public AppContext getAppContext(){
        return AppContext.initApp(getApplication());
    }

    private void initView() {
        appContext = getAppContext();
        TextView serverTV = (TextView) findViewById(R.id.Main_Server);
        findViewById(R.id.main_Scan).setOnClickListener(this);

        findViewById(R.id.Main_P2POperate).setOnClickListener(this);
        findViewById(R.id.Main_RTMPOperate).setOnClickListener(this);
        findViewById(R.id.Main_WIFI).setOnClickListener(this);
        findViewById(R.id.Main_LibInfo).setOnClickListener(this);
        findViewById(R.id.Main_Config).setOnClickListener(this);
        int serverNum = appContext.sharedPreferUtils.getInt(Constant.SP_SERVER_STATUS);
        serverTV.setText(serverNum == 2 ? "Custom" : serverNum == 1 ? "Debug" : "Release");
    }

    @Override
    public void onClick(View v) {
        if (!appContext.checkInitStatus(this)) {
            return;
        }
        Intent intent = null;
        String UserId = null;
        int i = v.getId();
        if (i == R.id.main_Scan) {
            startActivityForResult(new Intent(this, QRCodeScanActivity.class), 100);

        } else if (i == R.id.Main_P2POperate) {
            String deviceUID = deviceID.getText().toString().trim();
            UserId = userId.getText().toString().trim();
            if (TextUtils.isEmpty(deviceUID) || deviceUID.length() != 16) {
                Toast.makeText(this, "Enter valid device ID", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(UserId)) {
                Toast.makeText(this, "Enter valid user ID", Toast.LENGTH_SHORT).show();
                return;
            }
            intent = new Intent(this, P2POperateActivity.class);
            intent.putExtra(Constant.INTENT_STRING, deviceUID);
            intent.putExtra(Constant.INTENT_USERID, UserId);
            startActivity(intent);

        } else if (i == R.id.Main_RTMPOperate) {
            String deviceUID2 = deviceID.getText().toString().trim();
            if (TextUtils.isEmpty(deviceUID2) || deviceUID2.length() != 16) {
                Toast.makeText(this, "Enter valid device ID", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent2 = new Intent(this, RTMPOperateActivity.class);
            intent2.putExtra(Constant.INTENT_STRING, deviceUID2);
            startActivity(intent2);

        } else if (i == R.id.Main_WIFI) {
            UserId = userId.getText().toString().trim();
            if (TextUtils.isEmpty(UserId)) {
                Toast.makeText(this, "Enter valid user ID", Toast.LENGTH_SHORT).show();
                return;
            }
            intent = new Intent(this, WIFIConfigActivity.class);
            intent.putExtra(Constant.INTENT_USERID, UserId);
            startActivity(intent);

        } else if (i == R.id.Main_LibInfo) {
            new AlertDialog.Builder(this).setMessage(MediaControl.getInstance().getLibInfo()).show();

        } else if (i == R.id.Main_Config) {
            startActivity(new Intent(this, ConfigActivity.class));

        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && RESULT_OK == resultCode) {
            switch (requestCode) {
                case 100:
                    deviceID.setText(data.getStringExtra(Constant.KEY_QRSCAN));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppContext.PERMISSIONS_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    NoPermissions = true;
                }
                if (!NoPermissions) {
                    appContext.initAVLib();
                } else {
                    appContext.showToast("Request READ_PHONE_STATE authorization failed");
                }
            }

        }
    }

    private void checkPartamer() {

    }

    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setType("application/vnd.android.package-archive");
            intent.setData(Uri.fromFile(file));
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
