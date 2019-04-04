package com.videolib.android.activity;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.videolib.android.R;
import com.videolib.android.app.AppContext;
import com.videolib.android.dialog.WiFiListDialog;
import com.videolib.android.utils.Constant;
import com.worthcloud.sdlib.SoundWaveUtils;

import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;

import voice.encoder.VoicePlayer;
import voice.encoder.VoicePlayerListener;

/**
 * WIFI Setting
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/7/20
 */
public class WIFIConfigActivity extends Activity implements View.OnClickListener, VoicePlayerListener {
    private EditText wifiEt, pwdEt, appIDEt;
    private AppContext appContext;
    private WiFiListDialog dialog;
    private String userId, appId;

    public AppContext getAppContext(){
        return AppContext.initApp(getApplication());
    }
    @Override
    protected void onCreate(  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        wifiEt = findViewById(R.id.WIFI_SSID);
        pwdEt = findViewById(R.id.WIFI_PWD);
        findViewById(R.id.WIFI_Scan).setOnClickListener(this);
        findViewById(R.id.sendVoice).setOnClickListener(this);
        appIDEt = findViewById(R.id.WIFI_ET_APPID);
        appContext = (AppContext)  getAppContext();
        dialog = new WiFiListDialog(this);
        dialog.setItemClickBackListener(new ValueCallback<ScanResult>() {
            @Override
            public void onReceiveValue(ScanResult value) {
                wifiEt.setText(value.SSID);
            }
        });
        findViewById(R.id.back).setOnClickListener(this);
        userId = getIntent().getStringExtra(Constant.INTENT_USERID);
        appIDEt.setText(Constant.APPID);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.back) {
            finish();

        } else if (i == R.id.WIFI_Scan) {
            dialog.show();

        } else if (i == R.id.sendVoice) {
            if (!TextUtils.isEmpty(wifiEt.getText()) && !TextUtils.isEmpty(appIDEt.getText())) {
                sendVoice();
            } else if (TextUtils.isEmpty(wifiEt.getText())) {
                appContext.showToast("Enter WIFI password");
            } else {
                appContext.showToast("Enter APPId");
            }

        }
    }

    /**
     * Send sound wave
     */
    private void sendVoice() {
        appId = appIDEt.getText().toString().trim();
        try {
            SoundWaveUtils.sendSoundWave(wifiEt.getText().toString(), pwdEt.getText().toString(), userId, appId, this);
        } catch (UnsupportedEncodingException e) {
            appContext.showToast("Convert abnormally");
            e.printStackTrace();
        } catch (IllegalArgumentException f) {
            appContext.showToast("WIFI network/Password is too long");
            f.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundWaveUtils.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundWaveUtils.destroyPlay();
    }

    @Override
    public void onPlayStart(VoicePlayer voicePlayer) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.sendVoice).setEnabled(false);
            }
        });
    }

    @Override
    public void onPlayEnd(VoicePlayer voicePlayer) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.sendVoice).setEnabled(true);
            }
        });
    }
}
