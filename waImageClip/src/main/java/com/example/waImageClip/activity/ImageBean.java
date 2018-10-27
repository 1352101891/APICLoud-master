package com.lechange.demo.example.waImageClip.activity;

/**
 * Created by lvqiu on 2018/6/2.
 */

public class ImageBean {
    public String path;
    public String type;
    public String key;

    public ImageBean(String path, String type, String key) {
        this.path = path;
        this.type = type;
        this.key = key;
    }

    public ImageBean(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
