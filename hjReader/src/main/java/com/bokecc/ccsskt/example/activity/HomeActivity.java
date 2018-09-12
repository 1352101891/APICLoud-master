package com.bokecc.ccsskt.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.R2;
import com.bokecc.ccsskt.example.base.BaseActivity;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.util.ParseMsgUtil;
import com.bokecc.ccsskt.example.util.RequestPermissionHelper;
import com.bokecc.ccsskt.example.view.ClearEditLayout;
import com.kaola.qrcodescanner.qrcode.QrCodeActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    @BindView(R2.id.id_main_version)
    TextView mVersion;
    @BindView(R2.id.id_zbar)
    CheckBox mZbar;
    @BindView(R2.id.id_link_url)
    ClearEditLayout mClearEditLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onViewCreated() {
        mVersion.setText(CCApplication.getVersion());
    }


    @OnClick(R2.id.id_scan)
    void scan() {
        RequestPermissionHelper.RequestCamera(this);
        RequestPermissionHelper.RequestStore(this);
        if(RequestPermissionHelper.isPermissionCamera( this) && RequestPermissionHelper.isPermissionStore(this)){
            go(QrCodeActivity.class);
        }
    }

    @Override
    public void permissionToDo(int type){
        if(RequestPermissionHelper.isPermissionCamera( this) && RequestPermissionHelper.isPermissionStore(this)){
            go(QrCodeActivity.class);
        }
    }

    @OnClick(R2.id.operation)
    void goOperation() {
        go(OperationActivity.class);
    }

    @OnClick(R2.id.id_link_go)
    void goByLink() {
        String url = mClearEditLayout.getText();
        if (TextUtils.isEmpty(url)) {
            showToast("请输入链接");
            return;
        }
        parseUrl(url);
    }

    private void parseUrl(String url) {
        ParseMsgUtil.parseUrl(HomeActivity.this, url, new ParseMsgUtil.ParseCallBack() {
            @Override
            public void onStart() {
                showProgress();
            }

            @Override
            public void onSuccess() {
                dismissProgress();
            }

            @Override
            public void onFailure(String err) {
                toastOnUiThread(err);
                dismissProgress();
            }
        });
    }

    @Override
    protected void protectApp() {
        go(SplashActivity.class);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String value = intent.getStringExtra(Config.FORCE_KILL_ACTION);
        if (!TextUtils.isEmpty(value) && value.equals(Config.FORCE_KILL_VALUE)) {
            protectApp();
        }
    }
}
