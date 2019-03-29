package com.videolib.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.videolib.android.R;
import com.videolib.android.app.AppContext;
import com.videolib.android.utils.Constant;
import com.worthcloud.avlib.basemedia.MediaControl;
import com.worthcloud.avlib.utils.AudioAcquisition;
import com.worthcloud.avlib.widget.VideoPlayView;

import org.jetbrains.annotations.Nullable;

/**
 * RTMP操作
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/9/20
 */
public class RTMPOperateActivity extends Activity implements View.OnClickListener {
    private VideoPlayView videoPlayView;
    private Button playBtn, stopBtn;
    private Button pauseBtn, continueBtn, intercomBtn;
    private CheckBox isLive;
    private EditText editText;
    private AppContext appContext;
    private String deviceUUID;
    private long clickTime;
    private AudioAcquisition audioAcquisition = new AudioAcquisition(AudioAcquisition.AudioSamplingFrequencyType.Intercom, MediaControl.AgreementType.RTMP);
    private Thread audioThread;

    @Override
    protected void onCreate(  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_rtmp);
        initView();
    }
    public AppContext getAppContext(){
        return AppContext.initApp(getApplication());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        appContext = (AppContext) getAppContext();
        videoPlayView = findViewById(R.id.RTMPOperate_VideoPlay);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.RTMPOperate_Scan).setOnClickListener(this);
        (playBtn = findViewById(R.id.RTMPOperate_Play)).setOnClickListener(this);
        (stopBtn = findViewById(R.id.RTMPOperate_Stop)).setOnClickListener(this);
        (pauseBtn = findViewById(R.id.RTMPOperate_Pause)).setOnClickListener(this);
        (continueBtn = findViewById(R.id.RTMPOperate_Continue)).setOnClickListener(this);
        findViewById(R.id.RTMPOperate_Push).setOnClickListener(this);
        isLive = findViewById(R.id.RTMPOperate_isLive);
        editText = findViewById(R.id.RTMPOperate_PlayAddress);
        editText.setText(Constant.VIDEOURL);
        setButtonStatus(0);
        (intercomBtn = findViewById(R.id.RTMPOperate_Intercom)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (System.currentTimeMillis() - clickTime < 500) return false;
                            clickTime = System.currentTimeMillis();
                            startAudio();
                            break;
                        case MotionEvent.ACTION_UP:
                            closeAudio();
                            break;
                    }
                    return false;
                }
            }
        } );
        initData();
    }

    private void initData() {
        deviceUUID = getIntent().getStringExtra(Constant.INTENT_STRING);
        intercomBtn.setEnabled(!TextUtils.isEmpty(deviceUUID));
        MediaControl.getInstance().initHeart("329", "heart.zhiboyun.net", 4759, 1500);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.back) {
            finish();

        } else if (i == R.id.RTMPOperate_Scan) {
            startActivityForResult(new Intent(this, QRCodeScanActivity.class), 100);

        } else if (i == R.id.RTMPOperate_Play) {
            if (TextUtils.isEmpty(editText.getText())) {
                appContext.showToast("Please enter the play address");
                return;
            }
            setButtonStatus(1);
            videoPlayView.playVideoByRTMP(editText.getText().toString(), 329, isLive.isChecked() ? MediaControl.PlayType.LIVE_TYPE : MediaControl.PlayType.REPLAY_TYPE, "");

        } else if (i == R.id.RTMPOperate_Stop) {
            stopPlay();

        } else if (i == R.id.RTMPOperate_Pause) {
            if (videoPlayView.playVideoPause()) setButtonStatus(2);

        } else if (i == R.id.RTMPOperate_Continue) {
            if (videoPlayView.playVideoContinue()) setButtonStatus(1);

        } else if (i == R.id.RTMPOperate_Push) {
            startActivity(new Intent(this, PushActivity.class));

        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && RESULT_OK == resultCode && requestCode == 100) {
            editText.setText(data.getStringExtra(Constant.KEY_QRSCAN));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlay();
    }

    private void stopPlay() {
        setButtonStatus(0);
        videoPlayView.playVideoStop();
    }

    /**
     * Set button state
     *
     * @param status 0：Stop 1：Play 2：Pause
     */
    private void setButtonStatus(int status) {
        playBtn.setEnabled(status == 0);
        stopBtn.setEnabled(status != 0);
        pauseBtn.setEnabled(status == 1);
        continueBtn.setEnabled(status == 2);
    }

    /**
     * Turn on audio thread
     */
    private void startAudio() {
        MediaControl.getInstance().setIntercom(deviceUUID, true);
        if (audioThread != null) audioThread.interrupt();
        audioThread = new Thread(audioAcquisition);
        audioThread.start();
    }

    /**
     * Turn off audio thread
     */
    private void closeAudio() {
        if (audioThread != null) {
            audioThread.interrupt();
            audioThread = null;
        }
        MediaControl.getInstance().setIntercom(deviceUUID, false);
        audioAcquisition.stopAcquisition();
    }
}
