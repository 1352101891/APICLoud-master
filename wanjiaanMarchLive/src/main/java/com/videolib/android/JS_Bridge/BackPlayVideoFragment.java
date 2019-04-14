package com.videolib.android.JS_Bridge;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.videolib.android.R;
import com.videolib.android.activity.TFVideoPlayActivity;
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

import java.util.ArrayList;

/**
 * Created by lvqiu on 2019/1/30.
 */

@SuppressLint("ValidFragment")
public class BackPlayVideoFragment extends Fragment implements OnVideoViewListener, View.OnClickListener {

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
    private VideoProxy prox;
    public AppContext getAppContext(){
        return AppContext.initApp(null);
    }
    private View rootView;
    private Context mContext;
    private Intent intent;
    /**
     *
     * @param proxy 代理
     */
    @SuppressLint("ValidFragment")
    public BackPlayVideoFragment(VideoProxy proxy,Intent intent) {
        appContext=proxy.appContext;
        this.prox=proxy;
        this.intent=intent;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(99);
        if (rootView==null){
            mContext=inflater.getContext();
            rootView= inflater.inflate(R.layout.fragment_operate_p2p,null);
            initView(rootView);
            initData(intent);
        }
        return rootView;
    }

    private void initView(View rootView) {
        videoPlayView = rootView.findViewById(R.id.TFVideo_VideoPlay);
        seekBar = rootView.findViewById(R.id.seekBar);
        videoPlayView.setOnVideoPlayViewListener(this);
        appContext = (AppContext) getAppContext();
        (playBtn = rootView.findViewById(R.id.TFVideo_Play)).setOnClickListener(this);
        (stopBtn = rootView.findViewById(R.id.TFVideo_Stop)).setOnClickListener(this);
        (pauseBtn = rootView.findViewById(R.id.TFVideo_Pause)).setOnClickListener(this);
        (continueBtn = rootView.findViewById(R.id.TFVideo_Continue)).setOnClickListener(this);
    }

    private void initData(Intent intent) {
        linkHandler = intent.getLongExtra(Constant.INTENT_LINK_HANDLER, 0);
        tfRemoteFile = (TFRemoteFile) intent.getSerializableExtra(Constant.INTENT_DATABEAN);
        deviceUUid = intent.getStringExtra(Constant.INTENT_STRING);
        if (linkHandler == 0) {
            appContext.showToast("Wrong parameter");
            prox.closeBackPlayFragment();
            return;
        }
        TextView textView = rootView.findViewById(R.id.FileName);
        textView.setText(tfRemoteFile.getFileName());
        TextView timeStart = rootView.findViewById(R.id.TimeStart);
        timeStart.setText(DateUtils.getDateToString(tfRemoteFile.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
        TextView timeEdn = rootView.findViewById(R.id.TimeEnd);
        timeEdn.setText(DateUtils.getDateToString(tfRemoteFile.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prox.closeBackPlayFragment();
            }
        });
        seekBar.setMax((int) tfRemoteFile.getTimeSpan());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BackPlayVideoFragment.this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSliding = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoPlayView.changePlayPosition(BackPlayVideoFragment.this.progress);
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
    public void onPause() {
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
    // 请求权限
    public void requestPermissions(int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                ArrayList<String> requestPerssionArr = new ArrayList<>();
                int hasCamrea = getContext().checkSelfPermission(Manifest.permission.CAMERA);
                if (hasCamrea != PackageManager.PERMISSION_GRANTED) {
                    requestPerssionArr.add(Manifest.permission.CAMERA);
                }

                int hasSdcardRead = getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                if (hasSdcardRead != PackageManager.PERMISSION_GRANTED) {
                    requestPerssionArr.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                int hasSdcardWrite = getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasSdcardWrite != PackageManager.PERMISSION_GRANTED) {
                    requestPerssionArr.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                // 是否应该显示权限请求
                if (!requestPerssionArr.isEmpty()) {
                    String[] requestArray = new String[requestPerssionArr.size()];
                    for (int i = 0; i < requestArray.length; i++) {
                        requestArray[i] = requestPerssionArr.get(i);
                    }
                    requestPermissions(requestArray, requestCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                flag = true;
            }
        }
        if (!flag) {
            Log.i("BaseActivity", "权限未申请");
        }
    }

}
