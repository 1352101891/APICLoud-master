package com.videolib.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.videolib.android.R;
import com.videolib.android.app.AppContext;
import com.videolib.android.utils.Constant;
import com.worthcloud.avlib.basemedia.MediaControl;
import com.worthcloud.avlib.bean.EventMessage;
import com.worthcloud.avlib.bean.PlaybackProgress;
import com.worthcloud.avlib.bean.TFRemoteFile;
import com.worthcloud.avlib.event.eventcode.EventTypeCode;
import com.worthcloud.avlib.listener.OnVideoViewListener;
import com.worthcloud.avlib.utils.DateUtils;
import com.worthcloud.avlib.utils.LogUtils;
import com.worthcloud.avlib.widget.VideoPlayView;

import org.jetbrains.annotations.Nullable;

/**
 * TF Card recording play
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/9/21
 */
public class TFVideoPlayActivity extends Activity implements OnVideoViewListener, View.OnClickListener {
    private VideoPlayView videoPlayView;
    private long linkHandler = 0;
    private AppContext appContext;
    private TFRemoteFile tfRemoteFile;
    private String deviceUUid;
    private SeekBar seekBar;
    private int progress;
    private boolean isSliding = false;
    private Button playBtn, stopBtn, pauseBtn, continueBtn;
    private String defaultCameraPwd = MediaControl.DEFAULT_DEVICE_PWD;

    public AppContext getAppContext(){
        return AppContext.initApp(getApplication());
    }
    @Override
    protected void onCreate(  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tfvideo_player);
        videoPlayView = findViewById(R.id.TFVideo_VideoPlay);
        seekBar = findViewById(R.id.seekBar);
        videoPlayView.setOnVideoPlayViewListener(this);
        appContext = (AppContext) getAppContext();
        (playBtn = findViewById(R.id.TFVideo_Play)).setOnClickListener(this);
        (stopBtn = findViewById(R.id.TFVideo_Stop)).setOnClickListener(this);
        (pauseBtn = findViewById(R.id.TFVideo_Pause)).setOnClickListener(this);
        (continueBtn = findViewById(R.id.TFVideo_Continue)).setOnClickListener(this);
        initData();
    }

    private void initData() {
        linkHandler = getIntent().getLongExtra(Constant.INTENT_LINK_HANDLER, 0);
        tfRemoteFile = (TFRemoteFile) getIntent().getSerializableExtra(Constant.INTENT_DATABEAN);
        deviceUUid = getIntent().getStringExtra(Constant.INTENT_STRING);
        if (linkHandler == 0) {
            appContext.showToast("Wrong parameter");
            finish();
            return;
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
                TFVideoPlayActivity.this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSliding = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoPlayView.changePlayPosition(TFVideoPlayActivity.this.progress);
                isSliding = false;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoPlayView.playTFVideoByP2P(deviceUUid, defaultCameraPwd, linkHandler, tfRemoteFile.getFileName());
            }
        },300);
        setButtonStatus(1);
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
        appContext.showToast("Played");
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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.TFVideo_Play) {
            videoPlayView.playVideoStop();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    videoPlayView.playTFVideoByP2P(deviceUUid, defaultCameraPwd, linkHandler, tfRemoteFile.getFileName());
                }
            }, 300);
            setButtonStatus(1);

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
