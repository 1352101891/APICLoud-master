package com.bokecc.dwlivedemo_new.module;

/**
 * Created by Administrator on 2018/4/10.
 */

public class CloseEntity {

    public static String CLOSE="close";
    public static String OPEN="open";

    private String ObJ;
    private String Action;

    public CloseEntity(String obJ, String action) {
        ObJ = obJ;
        Action = action;
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
}
