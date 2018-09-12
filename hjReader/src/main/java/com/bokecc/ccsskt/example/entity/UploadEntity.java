package com.bokecc.ccsskt.example.entity;

/**
 * Created by Administrator on 2018/4/14.
 */

public class UploadEntity {
    public  final static String SUCCESS="SUCCESS";
    public  final static String ERROR="ERROR";
    String path;
    String key;
    int type;


    public UploadEntity(String path, String key) {
        this.path = path;
        this.key = key;
    }

    public UploadEntity(String path, String key, int type) {
        this.path = path;
        this.key = key;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
