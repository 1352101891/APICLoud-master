package com.videolib.android.JS_Bridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.videolib.android.R;
import com.videolib.android.app.AppContext;
import com.videolib.android.utils.Constant;
import com.worthcloud.avlib.basemedia.MediaControl;
import com.worthcloud.avlib.bean.EventMessage;
import com.worthcloud.avlib.bean.LinkInfo;
import com.worthcloud.avlib.bean.PlaybackProgress;
import com.worthcloud.avlib.bean.TFRemoteFile;
import com.worthcloud.avlib.event.eventcode.EventCode;
import com.worthcloud.avlib.event.eventcode.EventTypeCode;
import com.worthcloud.avlib.listener.OnVideoViewListener;
import com.worthcloud.avlib.listener.ValueCallBack;
import com.worthcloud.avlib.utils.DateUtils;
import com.worthcloud.avlib.utils.LogUtils;
import com.worthcloud.avlib.widget.VideoPlayView;

import static com.worthcloud.avlib.event.eventcode.EventCode.E_EVENT_CODE_LICENSE_TOKEN_ERROR;

/**
 * TF Card recording play
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/9/21
 */
public class BackplayVideoActivity extends Activity implements OnVideoViewListener, View.OnClickListener {
    private VideoPlayView videoPlayView;
    private long linkHandler = 0;
    private AppContext appContext;
    private TFRemoteFile tfRemoteFile;
    private String deviceUUid,userId,defaultAccessKey,defaultSecretKey;
    private SeekBar seekBar;
    private int progress;
    private boolean isSliding = false;
    private Button playBtn, stopBtn, pauseBtn, continueBtn;
    private String defaultCameraPwd = MediaControl.DEFAULT_DEVICE_PWD;
    private ImageView loadingImage;

    public AppContext getAppContext(){
        return AppContext.initApp(getApplication());
    }
    @Override
    protected void onCreate(  Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backplay_player);
        videoPlayView = findViewById(R.id.TFVideo_VideoPlay);
        seekBar = findViewById(R.id.seekBar);
        videoPlayView.setOnVideoPlayViewListener(this);
        appContext = (AppContext) getAppContext();
        startAnima();
        (playBtn = findViewById(R.id.TFVideo_Play)).setOnClickListener(this);
        (stopBtn = findViewById(R.id.TFVideo_Stop)).setOnClickListener(this);
        (pauseBtn = findViewById(R.id.TFVideo_Pause)).setOnClickListener(this);
        (continueBtn = findViewById(R.id.TFVideo_Continue)).setOnClickListener(this);
        initData();
    }

    private BackPlayBean backPlayBean;
    private void initData() {
        if (getIntent()!=null){
            backPlayBean= (BackPlayBean) getIntent().getSerializableExtra(Constant.BACKPLAYBEAN);
        }
        if (backPlayBean!=null){
            userId=backPlayBean.getUserId();
            tfRemoteFile =new TFRemoteFile();
            tfRemoteFile.setFileName(backPlayBean.getFileName());
            deviceUUid = backPlayBean.getDevCode();
            defaultAccessKey=backPlayBean.getAccess_key();
            defaultSecretKey=backPlayBean.getSecret_key();
            MediaControl.getInstance().getLinkHandler(deviceUUid, defaultAccessKey, defaultSecretKey, userId, true,
            new ValueCallBack<LinkInfo>() {
                @Override
                public void success(final LinkInfo linkInfo) {
                    BackplayVideoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linkSuccess(linkInfo);
                        }
                    });
                }

                @Override
                public void fail(final long code,final String msg) {
                    BackplayVideoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linkFail(code, msg);
                        }
                    });
                }
            });
        }
        TextView textView = findViewById(R.id.FileName);
        textView.setText(tfRemoteFile.getFileName());
        TextView timeStart = findViewById(R.id.TimeStart);
        timeStart.setText(DateUtils.getDateToString(tfRemoteFile.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
        TextView timeEdn = findViewById(R.id.TimeEnd);
        timeEdn.setText(DateUtils.getDateToString(tfRemoteFile.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        seekBar.setMax((int) tfRemoteFile.getTimeSpan());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BackplayVideoActivity.this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSliding = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoPlayView.changePlayPosition(BackplayVideoActivity.this.progress);
                isSliding = false;
            }
        });

        setButtonStatus(0);
    }
    private void linkSuccess(LinkInfo linkInfo) {
        linkHandler = linkInfo.getLinkHandler();
        LogUtils.debug(linkHandler);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoPlayView.playTFVideoByP2P(deviceUUid, defaultCameraPwd, linkHandler, tfRemoteFile.getFileName());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playBtn.performClick();
                    }
                },1000);
            }
        },300);
    }

    private void linkFail(long code, String msg) {
        LogUtils.debug("Error code" + code);
        if (code == EventCode.E_EVENT_CODE_DEVICE_LICENSE_CHECK_FAILURE) {
            toast(VideoProxy.MESSAGE,"Network timeout, please try again");
        } else if (code == EventCode.E_EVENT_CODE_DEVICE_LICENSE_EXPIRED) {
            toast(VideoProxy.MESSAGE,"No access to this device");
        } else if (code == E_EVENT_CODE_LICENSE_TOKEN_ERROR) {
            toast(VideoProxy.MESSAGE,"Token is invalid or expired");
        } else {
            toast(VideoProxy.MESSAGE,"Connection failed");
        }
    }

    public void toast(String key,String value){
        String json="{'"+key+"':'"+value+"'}";
        Intent intent=new Intent();
        intent.putExtra("json",json);
        intent.setAction("alertMess");
        this.sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayView.playVideoStop();
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onPlayComplete() {
        toast(VideoProxy.MESSAGE,"Played");
        videoPlayView.playVideoStop();
        seekBar.setProgress(0);
        setButtonStatus(0);
    }

    @Override
    public void onVideoMessage(EventMessage eventMessage) {
        switch (eventMessage.getMessageCode()) {
            case EventTypeCode.REFRESH_REPLAY_PROGRESS:
                PlaybackProgress playbackProgress = (PlaybackProgress) eventMessage.getObject();
                int playTime = (int) playbackProgress.getPlayTime();
                if (!isSliding) {
                    seekBar.setProgress(playTime);
                }
                LogUtils.debug("Current duration" + playTime + "Total duration" + tfRemoteFile.getTimeSpan());
                break;
            default:
                break;

        }
    }
    public void startAnima(){
        (loadingImage=findViewById(R.id.loading_iv)).setVisibility(View.VISIBLE);
        //加载动画资源
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
        //开启动画
        loadingImage.startAnimation(animation);
    }

    public void clearAnima(){
        if (loadingImage.animate()!=null){
            loadingImage.animate().cancel();
        }
        loadingImage.clearAnimation();
        loadingImage.setAnimation(null);
        loadingImage.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.TFVideo_Play) {
            if (linkHandler!=0) {
                videoPlayView.playVideoStop();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        videoPlayView.playTFVideoByP2P(deviceUUid, defaultCameraPwd, linkHandler, tfRemoteFile.getFileName());
                    }
                }, 300);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearAnima();
                    }
                },2000);
                setButtonStatus(1);
            }
        } else if (i == R.id.TFVideo_Stop) {
            videoPlayView.playVideoStop();
            setButtonStatus(0);

        } else if (i == R.id.TFVideo_Pause) {
            if (videoPlayView.playVideoPause()) {
                setButtonStatus(2);
            }

        } else if (i == R.id.TFVideo_Continue) {
            if (videoPlayView.playVideoContinue()) {
                setButtonStatus(1);
            }

        } else {
        }
    }

    /*设置按钮状态 0:未播放 1:播放 2: 暂停*/
    private void setButtonStatus(int status) {
        playBtn.setEnabled(status == 0);
        stopBtn.setEnabled(status != 0);
        pauseBtn.setEnabled(status == 1);
        continueBtn.setEnabled(status == 2);
    }
}
