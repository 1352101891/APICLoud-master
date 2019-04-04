package com.videolib.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler; 
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.videolib.android.R;
import com.videolib.android.app.AppContext;
import com.videolib.android.utils.Constant;
import com.videolib.android.utils.PDZCtrl;
import com.worthcloud.avlib.basemedia.MediaControl;
import com.worthcloud.avlib.bean.EventMessage;
import com.worthcloud.avlib.bean.LinkInfo;
import com.worthcloud.avlib.bean.TFCardInfo;
import com.worthcloud.avlib.event.eventcallback.BaseEventCallback;
import com.worthcloud.avlib.event.eventcallback.EventCallBack;
import com.worthcloud.avlib.event.eventcode.EventCode;
import com.worthcloud.avlib.event.eventcode.EventTypeCode;
import com.worthcloud.avlib.listener.OnVideoViewListener;
import com.worthcloud.avlib.listener.ValueCallBack;
import com.worthcloud.avlib.utils.AudioAcquisition;
import com.worthcloud.avlib.utils.LogUtils;
import com.worthcloud.avlib.widget.VideoPlayView;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import static com.videolib.android.utils.Constant.SCREENSHOTFOLDER;
import static com.worthcloud.avlib.event.eventcode.EventCode.E_EVENT_CODE_LICENSE_TOKEN_ERROR;

/**
 * p2p operation
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/9/20
 */
public class P2POperateActivity extends Activity implements View.OnClickListener, OnVideoViewListener
        , BaseEventCallback.OnEventListener, View.OnTouchListener {
    private long linkHandler;
    private VideoPlayView videoPlayView;
    private String deviceUUID, userId;
    private AppContext appContext;
    private TextView linkStatus, playStatus;
    private Button reconBtn, playBtn, stopBtn;
    private Button tfInfoBtn, tfFileBtn, shBtn, intercomBtn;
    private Button btSD, btHD, btSHD;
    private Button btLEFT, btRIGHT, btUP, btDOWN;
    private AudioAcquisition audioAcquisition = new AudioAcquisition(AudioAcquisition.AudioSamplingFrequencyType.Intercom_16, MediaControl.AgreementType.P2P);
    private Thread audioThread;
    private long clickTime;
    private ProgressBar progressBar;

    private CPCtrl CPCtrlRunnable;
    private long curtDownTime = 0;//点击计时
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_p2p);
        initView();
        intDate();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        videoPlayView = findViewById(R.id.P2POperate_VideoPlay);
        videoPlayView.setOnVideoPlayViewListener(this);
        linkStatus = findViewById(R.id.P2POperate_LinkStatus);
        playStatus = findViewById(R.id.P2POperate_playStatus);
        (reconBtn = findViewById(R.id.P2POperate_ReconnectBtn)).setOnClickListener(this);
        (playBtn = findViewById(R.id.P2POperate_playBtn)).setOnClickListener(this);
        (stopBtn = findViewById(R.id.P2POperate_StopBtn)).setOnClickListener(this);
        (tfInfoBtn = findViewById(R.id.P2POperate_TFInfoBtn)).setOnClickListener(this);
        (tfFileBtn = findViewById(R.id.P2POperate_TFFileBtn)).setOnClickListener(this);
        (shBtn = findViewById(R.id.P2POperate_ScreenShot)).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        (btSD = findViewById(R.id.P2POperate_SD)).setOnClickListener(this);
        (btHD = findViewById(R.id.P2POperate_HD)).setOnClickListener(this);
        (btSHD = findViewById(R.id.P2POperate_SHD)).setOnClickListener(this);

        (btLEFT = findViewById(R.id.P2POperate_LEFT)).setOnTouchListener(this);
        (btRIGHT = findViewById(R.id.P2POperate_RIGHT)).setOnTouchListener(this);
        (btUP = findViewById(R.id.P2POperate_UP)).setOnTouchListener(this);
        (btDOWN = findViewById(R.id.P2POperate_DOWN)).setOnTouchListener(this);

        progressBar = findViewById(R.id.P2POperate_Progress);
        //对讲
        audioAcquisition.setAudioErrorCallBack(new ValueCallback<Integer>() {
            @Override
            public void onReceiveValue(Integer value) {
                P2POperateActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appContext.showToast("Recording permission is forbidden");
                    }
                });
            }
        });
        audioAcquisition.setDBValueCallBack(new ValueCallback<Double>() {
            @Override
            public void onReceiveValue(Double value) {
                progressBar.setProgress((int) value.doubleValue());
            }
        });
        (intercomBtn = findViewById(R.id.P2POperate_Intercom)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
        });
    }
    public AppContext getAppContext(){
        return AppContext.initApp(getApplication());
    }
    private void intDate() {
        appContext = (AppContext) P2POperateActivity.this.getAppContext();
        deviceUUID = getIntent().getStringExtra(Constant.INTENT_STRING);
        userId = getIntent().getStringExtra(Constant.INTENT_USERID);
        getLinkHandler();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.back) {
            finish();

        } else if (i == R.id.P2POperate_ReconnectBtn) {
            getLinkHandler();

        } else if (i == R.id.P2POperate_playBtn) {
            videoPlayView.playVideoStop();
            videoPlayView.playVideoByP2P(deviceUUID, MediaControl.DEFAULT_DEVICE_PWD, linkHandler);

        } else if (i == R.id.P2POperate_StopBtn) {
            stopVideo();

        } else if (i == R.id.P2POperate_TFInfoBtn) {
            MediaControl.getInstance().p2pGetTFInfo(deviceUUID, MediaControl.DEFAULT_DEVICE_PWD);

        } else if (i == R.id.P2POperate_TFFileBtn) {
            Intent intent = new Intent(this, TFVideoListActivity.class);
            intent.putExtra(Constant.INTENT_STRING, deviceUUID);
            intent.putExtra(Constant.INTENT_LINK_HANDLER, linkHandler);
            startActivity(intent);

        } else if (i == R.id.P2POperate_ScreenShot) {
            screenShot();

        } else if (i == R.id.P2POperate_SD) {
            videoPlayView.p2pChangeBitRate(2);

        } else if (i == R.id.P2POperate_HD) {
            videoPlayView.p2pChangeBitRate(1);

        } else if (i == R.id.P2POperate_SHD) {
            videoPlayView.p2pChangeBitRate(0);

        } else {
        }
    }

    /*设置link时候按钮状态 type=0 连接中 type=1连接失败 type=2连接成功 type=3播放*/
    private void setLinkStatus(int type) {
        reconBtn.setEnabled(type == 1);
        playBtn.setEnabled(type == 2);
        tfInfoBtn.setEnabled(type == 2 || type == 3);
        tfFileBtn.setEnabled(type == 2 || type == 3);
        stopBtn.setEnabled(type == 3);
        shBtn.setEnabled(type == 3);
        intercomBtn.setEnabled(type == 3);
        btSD.setEnabled(type == 3);
        btHD.setEnabled(type == 3);
        btSHD.setEnabled(type == 3);
    }

    /*获取Handler*/
    @SuppressLint("SetTextI18n")
    private void getLinkHandler() {
        setLinkStatus(0);
        if (linkHandler != 0) {
            MediaControl.getInstance().p2pUnLinkDevice(linkHandler);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    linkStatus.setText("Connecting...");
                    int linkStatusNum = appContext.sharedPreferUtils.getInt(Constant.SP_LINK_STATUS);
                    String defaultAccessKey = appContext.sharedPreferUtils.getString(Constant.SP_ACCESSKEY);
                    String defaultSecretKey = appContext.sharedPreferUtils.getString(Constant.SP_SECRETKEY);
                    String defaultToken = appContext.sharedPreferUtils.getString(Constant.SP_TOKEN);
                    if (linkStatusNum == 1) {
                        MediaControl.getInstance().getLinkHandler(deviceUUID, defaultToken, userId, true, new ValueCallBack<LinkInfo>() {
                            @Override
                            public void success(final LinkInfo linkInfo) {
                                P2POperateActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        linkSuccess(linkInfo);
                                    }
                                });
                            }

                            @Override
                            public void fail(final long code,final String msg) {
                                P2POperateActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        linkFail(code, msg);
                                    }
                                });
                            }
                        });
                    } else {
                        MediaControl.getInstance().getLinkHandler(deviceUUID, defaultAccessKey, defaultSecretKey, userId, true,
                                new ValueCallBack<LinkInfo>() {
                                    @Override
                                    public void success(final LinkInfo linkInfo) {
                                        P2POperateActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                linkSuccess(linkInfo);
                                            }
                                        });
                                    }

                                    @Override
                                    public void fail(final long code,final String msg) {
                                        P2POperateActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                linkFail(code, msg);
                                            }
                                        });
                                    }
                                });
                    }
                }}, 200);
    }


    private void linkSuccess(LinkInfo linkInfo) {
        linkHandler = linkInfo.getLinkHandler();
        LogUtils.debug(linkHandler);
        setLinkStatus(2);
        linkStatus.setText("Connected");
    }

    private void linkFail(long code, String msg) {
        LogUtils.debug("Error code" + code);
        if (code == EventCode.E_EVENT_CODE_DEVICE_LICENSE_CHECK_FAILURE) {
            linkStatus.setText("Network timeout, please try again");
            appContext.showToast("Network timeout, please try again");
        } else if (code == EventCode.E_EVENT_CODE_DEVICE_LICENSE_EXPIRED) {
            linkStatus.setText("No access to this device");
            appContext.showToast("No access to this device");
        } else if (code == E_EVENT_CODE_LICENSE_TOKEN_ERROR) {
            linkStatus.setText("Token is invalid or expired");
            appContext.showToast("Token is invalid or expired");
        } else {
            linkStatus.setText("Connection failed");
            appContext.showToast("Connection failed");
        }
        setLinkStatus(1);
    }

    /*创建TF卡信息*/
    private void createTFCardDialog(TFCardInfo tfCardInfo) {
        new AlertDialog.Builder(this)
                .setTitle("TF card information")
                .setMessage(tfCardInfo.getSdExist() == 1 ? "Available capacity" + tfCardInfo.getSdFreeSize() + "MB\n" + "Total capacity" + tfCardInfo.getSdTotalSize() + "MB" : "No SD Card")
                .show();
    }

    /*截图*/
    private void screenShot() {
        if (!videoPlayView.isPlaying()) {
            appContext.showToast("Video is not playing，screenshot failed");
            return;
        }
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appContext.showToast("No SD card, screenshot failed");
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + SCREENSHOTFOLDER + File.separator + "LIVE_" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.mkdirs();
        MediaControl.getInstance().p2pPlayCapture(videoPlayView.getPlayerId(), file.getAbsolutePath());
    }

    @Override
    public void onLoading() {
        playStatus.setText("Loading");
    }

    @Override
    public void onHideLoading() {
        setLinkStatus(3);
        playStatus.setText("Playing");
    }

    @Override
    public void onPlayComplete() {
        playStatus.setText("Played");
    }

    @Override
    public void onVideoMessage(EventMessage eventMessage) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventCallBack.getInstance().addCallbackListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeAudio();
        stopVideo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventCallBack.getInstance().removeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (linkHandler != 0) MediaControl.getInstance().p2pUnLinkDevice(linkHandler);
    }

    private void getHaveFile() {
        MediaControl.getInstance().p2pGetHaveFileList(deviceUUID, 0, 1549012852, 1551345652, MediaControl.DEFAULT_DEVICE_PWD);
    }

    @Override
    public void onEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getMessageCode()) {
            case EventCode.E_EVENT_CODE_MSG_SEARCH_FILE_TIME_LIST_RESP:
                List<String> list1 = (List<String>) eventMessage.getObject();
                LogUtils.debug(list1.toString());
                new AlertDialog.Builder(this)
                        .setMessage(list1.toString())
                        .show();
                break;
            case EventTypeCode.TF_INFO:
                TFCardInfo tfCardInfo = (TFCardInfo) eventMessage.getObject();
                if (tfCardInfo != null) {
                    createTFCardDialog(tfCardInfo);
                } else {
                    appContext.showToast("Failed to obtain TF card information");
                }
                break;
            case EventCode.E_EVENT_CODE_MSG_HEARTBEAT_ERROR:
                linkStatus.setText("Connection failed");
                if (videoPlayView.isCallPlay()) {
                    playStatus.setText("Not play");
                    videoPlayView.playVideoStop();
                    appContext.showToast("Error");
                }
                MediaControl.getInstance().p2pUnLinkDevice(linkHandler);
                setLinkStatus(1);
                break;
            //截屏成功
            case EventCode.E_EVENT_CODE_PLAY_CAPTURE_SUCCEED:
                appContext.showToast("Screenshot successfully");
                break;
            //截屏失败
            case EventCode.E_EVENT_CODE_PLAY_CAPTURE_FAILED:
                appContext.showToast("Screenshot failed");
                break;
            case EventCode.E_EVENT_CODE_CAMERAPWD_CHECK_FAILED:
                appContext.showToast("Wrong password");
                break;
            default:
                break;
        }
    }

    /**
     * Stap playing
     */
    private void stopVideo() {
        playStatus.setText("Not play");
        videoPlayView.playVideoStop();
        setLinkStatus(2);
    }

    /**
     * Turn on audio thread
     */
    private void startAudio() {
        MediaControl.getInstance().p2pSetIntercom(deviceUUID, true);
        if (audioThread != null) audioThread.interrupt();
        audioThread = new Thread(audioAcquisition);
        audioThread.start();
    }

    /**
     * Turn off audio thread
     */
    private void closeAudio() {
        MediaControl.getInstance().p2pSetIntercom(deviceUUID, false);
        audioAcquisition.stopAcquisition();
        if (audioThread != null) {
            audioThread.interrupt();
            audioThread = null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int vid = v.getId();
        int direct = 0;
        if (vid == R.id.P2POperate_LEFT) {
            direct = 3;
        } else if (vid == R.id.P2POperate_UP) {
            direct = 1;
        } else if (vid == R.id.P2POperate_RIGHT) {
            direct = 4;
        } else if (vid == R.id.P2POperate_DOWN) {
            direct = 2;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setCtrlBtnEnable(direct);
                curtDownTime = System.currentTimeMillis();
                CPCtrlRunnable = new CPCtrl(direct);
                handler.postDelayed(CPCtrlRunnable, 1000);
                break;
            case MotionEvent.ACTION_UP:
                setCtrlBtnEnable(0);
                if (System.currentTimeMillis() - curtDownTime < 1000) {
                    if (CPCtrlRunnable != null) handler.removeCallbacks(CPCtrlRunnable);
                    CPCtrl(direct, 0);
                } else {
                    //取消长按
                    CPCtrlStop();
                }
                break;
        }
        return true;
    }

    public class CPCtrl implements Runnable {
        int direct;

        public CPCtrl(int direct) {
            this.direct = direct;
        }

        @Override
        public void run() {
            CPCtrl(direct, 1);
        }
    }

    /*云台控制 :type 0短按 1长按*/
    public void CPCtrl(int direct, int type) {
        if (direct > 0) {
            String token = appContext.sharedPreferUtils.getString(Constant.SP_TOKEN);
            int type_URL = appContext.sharedPreferUtils.getInt(Constant.SP_SERVER_STATUS);
            LogUtils.debug(token);
            String url = type_URL == 1 ? PDZCtrl.URL_DEBUG : PDZCtrl.URL_RELEASE;
            PDZCtrl.ctrl(url, deviceUUID, direct, type, 1, token, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                }
            });
        }
    }

    /*云台控制-停止*/
    private void CPCtrlStop() {
        String token = appContext.sharedPreferUtils.getString(Constant.SP_TOKEN);
        int type_URL = appContext.sharedPreferUtils.getInt(Constant.SP_SERVER_STATUS);
        String url = type_URL == 1 ? PDZCtrl.URL_DEBUG : PDZCtrl.URL_RELEASE;
        PDZCtrl.ctrl(url, deviceUUID, 1, 0, 0, token, null);
    }

    /*设置按钮状态*/
    public void setCtrlBtnEnable(int typeId) {
        btLEFT.setEnabled(typeId == 3 || typeId == 0);
        btUP.setEnabled(typeId == 1 || typeId == 0);
        btRIGHT.setEnabled(typeId == 4 || typeId == 0);
        btDOWN.setEnabled(typeId == 2 || typeId == 0);
    }
}
