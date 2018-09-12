package com.bokecc.ccsskt.example.entity;

/**
 * Created by Administrator on 2018/4/10.
 */

public class CloseEntity {

    public static String CLOSE="close";
    public static String OPEN="open";

    private String ObJ;
    private String Action;
    private long classlength;

    public CloseEntity(String obJ, String action) {
        ObJ = obJ;
        Action = action;
    }

    public CloseEntity(String obJ, String action, long classlength) {
        ObJ = obJ;
        Action = action;
        this.classlength = classlength;
    }

    public String getObJ() {
        return ObJ;
    }

    public void setObJ(String obJ) {
        ObJ = obJ;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public long getClasslength() {
        return classlength;
    }

    public void setClasslength(long classlength) {
        this.classlength = classlength;
    }
}
