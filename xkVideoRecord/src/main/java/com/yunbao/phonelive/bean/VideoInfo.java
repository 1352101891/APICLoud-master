package com.yunbao.phonelive.bean;

import java.io.Serializable;

/**
 * Created by lvqiu on 2018/5/30.
 */

public class VideoInfo  implements Serializable{
    public int TYPE;
    public String VIDEPATH;
    public String COVERPATH;


    public VideoInfo(int TYPE, String VIDEPATH, String COVERPATH) {
        this.TYPE = TYPE;
        this.VIDEPATH = VIDEPATH;
        this.COVERPATH = COVERPATH;
    }

    public int getTYPE() {
        return TYPE;
    }

    public void setTYPE(int TYPE) {
        this.TYPE = TYPE;
    }

    public String getVIDEPATH() {
        return VIDEPATH;
    }

    public void setVIDEPATH(String VIDEPATH) {
        this.VIDEPATH = VIDEPATH;
    }

    public String getCOVERPATH() {
        return COVERPATH;
    }

    public void setCOVERPATH(String COVERPATH) {
        this.COVERPATH = COVERPATH;
    }
}
