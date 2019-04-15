package com.videolib.android.JS_Bridge;

import java.io.Serializable;

public class BackPlayBean implements Serializable {
    private String access_key;
    private String secret_key;
    private String devCode;
    private String userId;
    private String fileName;

    public BackPlayBean(String access_key, String secret_key, String devCode, String userId, String fileName) {
        this.access_key = access_key;
        this.secret_key = secret_key;
        this.devCode = devCode;
        this.userId = userId;
        this.fileName = fileName;
    }


    public String getAccess_key() {
        return access_key;
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

    public String getDevCode() {
        return devCode;
    }

    public void setDevCode(String devCode) {
        this.devCode = devCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
