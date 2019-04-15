package com.videolib.android.JS_Bridge;

import com.worthcloud.avlib.bean.TFRemoteFile;
import java.util.List;

public class DataListBean {
    private String id;
    private List<TFRemoteFile>  fileList;
    private boolean isloading=false;
    private boolean isfresh=false;
    private long startTimeSpan, endTimeSpan;
}
