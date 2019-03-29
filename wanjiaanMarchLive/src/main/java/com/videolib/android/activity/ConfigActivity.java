package com.videolib.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.videolib.android.R;
import com.videolib.android.app.AppContext;
import com.videolib.android.utils.Constant;

/**
 * configActivity
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/9/20
 */
public class ConfigActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private EditText token, accessKey, secretKey, passServer, passServerPort, turnServer, turnPort, authURL;
    private AppContext appContext;
    private RadioButton tokenRB, keyRB, releaseRB, debugRB, customRB;
    private RadioGroup linkStatus, serverStatus;
    public boolean NoPermissions;
    private ConstraintLayout layout;
    private int serverStatusNum = 0;
    private int linkStatusNum = 0;

    @Override
    protected void onCreate(  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        initView();
    }
    public AppContext getAppContext(){
        return AppContext.initApp(getApplication());
    }
    private void initView() {
        appContext = (AppContext)  getAppContext();
        accessKey = findViewById(R.id.Config_ET_ACCESSKEY);
        secretKey = findViewById(R.id.Config_ET_SECRETKEY);
        token = findViewById(R.id.Config_ET_TOKEN);
        findViewById(R.id.Config_Save).setOnClickListener(this);
        layout = findViewById(R.id.constraintLayout);
        tokenRB = findViewById(R.id.Config_Token);
        keyRB = findViewById(R.id.Config_Key);
        releaseRB = findViewById(R.id.Config_Release);
        debugRB = findViewById(R.id.Config_Debug);
        customRB = findViewById(R.id.Config_Custom);
        passServer = findViewById(R.id.Config_Custom_PassServer);
        passServerPort = findViewById(R.id.Config_Custom_Passport);
        turnServer = findViewById(R.id.Config_Custom_TurnServer);
        turnPort = findViewById(R.id.Config_Custom_TurnPort);
        authURL = findViewById(R.id.Config_Custom_AuthURL);
        findViewById(R.id.Config_Scan_token).setOnClickListener(this);
        findViewById(R.id.Config_Scan_ACCESSKEY).setOnClickListener(this);
        findViewById(R.id.Config_Scan_SECRETKEY).setOnClickListener(this);
        findViewById(R.id.Config_Save).setOnClickListener(this);
        findViewById(R.id.Config_Back).setOnClickListener(this);
        findViewById(R.id.Config_Scan_AuthURL).setOnClickListener(this);
        linkStatus = findViewById(R.id.RG_Auth);
        linkStatus.setOnCheckedChangeListener(this);
        serverStatus = findViewById(R.id.radioGroup);
        serverStatus.setOnCheckedChangeListener(this);
        initDate();
    }

    private void initDate() {
        serverStatusNum = appContext.sharedPreferUtils.getInt(Constant.SP_SERVER_STATUS);
        if (serverStatusNum == 2) {
            layout.setVisibility(View.VISIBLE);
            customRB.setChecked(true);
            String passServerT = appContext.sharedPreferUtils.getString(Constant.SP_PASS_SERVER);
            int passServerPortT = appContext.sharedPreferUtils.getInt(Constant.SP_PASS_SERVER_PROT);
            String turnServerT = appContext.sharedPreferUtils.getString(Constant.SP_TURN_SERVER);
            int turnServerPortT = appContext.sharedPreferUtils.getInt(Constant.SP_TURN_SERVER_PROT);
            String authUrl = appContext.sharedPreferUtils.getString(Constant.SP_AUTH_URL);
            if (!TextUtils.isEmpty(passServerT) && !TextUtils.isEmpty(turnServerT) && !TextUtils.isEmpty(authUrl) && passServerPortT > 0 && turnServerPortT > 0) {
                passServer.setText(passServerT);
                passServerPort.setText(passServerPortT);
                turnServer.setText(turnServerPortT);
                authURL.setText(authUrl);
            }

        } else if (serverStatusNum == 1) {
            layout.setVisibility(View.GONE);
            debugRB.setChecked(true);
        } else {
            layout.setVisibility(View.GONE);
            releaseRB.setChecked(true);
        }
        linkStatusNum = appContext.sharedPreferUtils.getInt(Constant.SP_LINK_STATUS);
        if (linkStatusNum == 1) {
            tokenRB.setChecked(true);
        } else {
            keyRB.setChecked(true);
        }
        String defaultAccessKey = appContext.sharedPreferUtils.getString(Constant.SP_ACCESSKEY);
        String defaultSecretKey = appContext.sharedPreferUtils.getString(Constant.SP_SECRETKEY);
        String defaultToken = appContext.sharedPreferUtils.getString(Constant.SP_TOKEN);
        if (!TextUtils.isEmpty(defaultAccessKey)) {
            accessKey.setText(defaultAccessKey);
        }
        if (!TextUtils.isEmpty(defaultSecretKey)) {
            secretKey.setText(defaultSecretKey);
        }
        if (!TextUtils.isEmpty(defaultToken)) {
            token.setText(defaultToken);
        }
    }

    @Override
    public void onClick(View v) {
        if (!appContext.checkInitStatus(this)) {
            return;
        }
        int i = v.getId();
        if (i == R.id.Config_Scan_token) {
            startActivityForResult(new Intent(this, QRCodeScanActivity.class), 100);

        } else if (i == R.id.Config_Scan_ACCESSKEY) {
            startActivityForResult(new Intent(this, QRCodeScanActivity.class), 200);

        } else if (i == R.id.Config_Scan_SECRETKEY) {
            startActivityForResult(new Intent(this, QRCodeScanActivity.class), 300);

        } else if (i == R.id.Config_Scan_AuthURL) {
            startActivityForResult(new Intent(this, QRCodeScanActivity.class), 400);

        } else if (i == R.id.Config_Back) {
            finish();

        } else if (i == R.id.Config_Save) {
            save();

        } else {
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == linkStatus) {
            if (checkedId == R.id.Config_Token) {
                linkStatusNum = 1;
            } else if (checkedId == R.id.Config_Key) {
                linkStatusNum = 0;
            }
        } else if (group == serverStatus) {
            if (checkedId == R.id.Config_Custom) {
                layout.setVisibility(View.VISIBLE);
                serverStatusNum = 2;
            } else if (checkedId == R.id.Config_Debug) {
                layout.setVisibility(View.GONE);
                serverStatusNum = 1;
            } else if (checkedId == R.id.Config_Release) {
                layout.setVisibility(View.GONE);
                serverStatusNum = 0;
            }
        }
    }

    /*Save date*/
    private void save() {
        if (TextUtils.isEmpty(token.getText()) || TextUtils.isEmpty(accessKey.getText()) || TextUtils.isEmpty(secretKey.getText())) {
            appContext.showToast("TOKEN/ACCESSKEY/SECRETTKEY can not be empty");
            return;
        }
        appContext.sharedPreferUtils.putString(Constant.SP_TOKEN, token.getText().toString());
        appContext.sharedPreferUtils.putString(Constant.SP_ACCESSKEY, accessKey.getText().toString());
        appContext.sharedPreferUtils.putString(Constant.SP_SECRETKEY, secretKey.getText().toString());
        appContext.sharedPreferUtils.putInt(Constant.SP_LINK_STATUS, linkStatusNum);
        if (serverStatusNum == 2) {
            if (!TextUtils.isEmpty(passServer.getText()) && !TextUtils.isEmpty(passServerPort.getText()) && !TextUtils.isEmpty(turnServer.getText()) && !TextUtils.isEmpty(turnPort.getText()) && !TextUtils.isEmpty(authURL.getText())) {
                appContext.sharedPreferUtils.putString(Constant.SP_PASS_SERVER, passServer.getText().toString());
                appContext.sharedPreferUtils.putString(Constant.SP_TURN_SERVER, turnServer.getText().toString());
                appContext.sharedPreferUtils.putString(Constant.SP_AUTH_URL, authURL.getText().toString());
                appContext.sharedPreferUtils.putInt(Constant.SP_PASS_SERVER_PROT, Integer.parseInt(passServerPort.getText().toString()));
                appContext.sharedPreferUtils.putInt(Constant.SP_TURN_SERVER_PROT, Integer.parseInt(turnPort.getText().toString()));
            } else {
                appContext.showToast("Server config can not be empty");
                return;
            }
        }
        appContext.sharedPreferUtils.putInt(Constant.SP_SERVER_STATUS, serverStatusNum);
        appContext.showToast("Config has been saved");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && RESULT_OK == resultCode) {
            switch (requestCode) {
                case 100:
                    token.setText(data.getStringExtra(Constant.KEY_QRSCAN));
                    break;
                case 200:
                    accessKey.setText(data.getStringExtra(Constant.KEY_QRSCAN));
                    break;
                case 300:
                    secretKey.setText(data.getStringExtra(Constant.KEY_QRSCAN));
                    break;
                case 400:
                    authURL.setText(data.getStringExtra(Constant.KEY_QRSCAN));
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
}
