package com.yunbao.phonelive.video.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.DialogUitl;


/**
 * Created by cxf on 2017/7/12.
 */

public class ProgressPopWinFactory {

    private static ProgressPopWinFactory sInstance;
    private TextView mTextView;
    private Dialog mDialog;
    private boolean isShowing;

    public static ProgressPopWinFactory getInstance() {
        if (sInstance == null) {
            synchronized (ProgressPopWinFactory.class) {
                if (sInstance == null) {
                    sInstance = new ProgressPopWinFactory();
                }
            }
        }
        return sInstance;
    }

    private ProgressPopWinFactory() {
    }


    public void show(Context context,String text) {
        if(mDialog!=null){
            mDialog=null;
        }
        mDialog = DialogUitl.loadingDialog(context, text);
        mTextView = (TextView) mDialog.findViewById(R.id.text);
//        mDialog.setCanceledOnTouchOutside(false);
//        mDialog.setCancelable(false);
        if (!isShowing) {
            isShowing = true;
            mDialog.show();
        }
    }

    public void hide() {
        if (isShowing) {
            isShowing = false;
            mDialog.dismiss();
            mDialog=null;
        }
    }

    public void setText(String text) {
        mTextView.setText(text);
    }


}
