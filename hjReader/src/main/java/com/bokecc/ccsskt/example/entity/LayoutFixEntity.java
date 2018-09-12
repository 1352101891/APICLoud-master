package com.bokecc.ccsskt.example.entity;

/**
 * Created by Administrator on 2018/4/17.
 */

public class LayoutFixEntity {
    private int destID;
    private int margin_value;
    private Dir dir;
    public enum Dir {
        BELOW,LEFT,RIGHT,ABOVE
    }
    public LayoutFixEntity(int margin_value,  Dir dir) {
        this.margin_value = margin_value;
        this.dir = dir;
    }


    public LayoutFixEntity(int destID,int margin_value,  Dir dir) {
        this.destID = destID;
        this.dir = dir;
    }

    public int getDestID() {
        return destID;
    }

    public int getMargin_value() {
        return margin_value;
    }

    public void setMargin_value(int margin_value) {
        this.margin_value = margin_value;
    }

    public void setDestID(int destID) {
        this.destID = destID;
    }


    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }
}
