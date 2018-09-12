package com.yunbao.phonelive.bean;

/**
 * Created by lvqiu on 2018/5/30.
 */

public class CallbackBean {
    private boolean finish;
    private String key;
    private VideoInfo videoInfo;

    public CallbackBean(boolean finish, String key) {
        this.finish = finish;
        this.key = key;
    }

    public CallbackBean(boolean finish, String key, VideoInfo videoInfo) {
        this.finish = finish;
        this.key = key;
        this.videoInfo = videoInfo;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
