package com.yunbao.phonelive.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.phonelive.R;


/**
 * Created by cxf on 2017/8/8.
 */

public class DialogUitl {
    //第三方登录的时候用显示的dialog
    public static Dialog loginAuthDialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_login_auth);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static Dialog confirmDialog(Context context, String title, String message, final Callback callback) {
        return confirmDialog(context, title, message, "", "", true, callback);
    }

    public static Dialog confirmDialog(Context context, String title, String message, boolean cancel, final Callback callback) {
        return confirmDialog(context, title, message, "", "", cancel, callback);
    }

    public static Dialog confirmDialog(Context context, String title, String message, String confirmText, String cancelText, boolean cancel, final Callback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setCancelable(cancel);
        dialog.setCanceledOnTouchOutside(cancel);
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setText(title);
        TextView content = (TextView) dialog.findViewById(R.id.content);
        content.setText(message);
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.cancel_btn);
        if (!"".equals(cancelText)) {
            cancelBtn.setText(cancelText);
        }
        TextView confirmBtn = (TextView) dialog.findViewById(R.id.confirm_btn);
        if (!"".equals(confirmText)) {
            confirmBtn.setText(confirmText);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.cancel_btn) {
                    if (callback != null) {
                        callback.cancel(dialog);
                    }

                } else if (i == R.id.confirm_btn) {
                    if (callback != null) {
                        callback.confirm(dialog);
                    }

                }
            }
        };
        cancelBtn.setOnClickListener(listener);
        confirmBtn.setOnClickListener(listener);
        return dialog;
    }

    public static Dialog inputDialog(Context context, String title, final Callback3 callback) {
        return inputDialog(context, title, "", "", callback);
    }

    public static Dialog inputDialog(Context context, String title, String confirmText, String cancelText, final Callback3 callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_input);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setText(title);
        final EditText input = (EditText) dialog.findViewById(R.id.input);
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.cancel_btn);
        if (!"".equals(cancelText)) {
            cancelBtn.setText(cancelText);
        }
        TextView confirmBtn = (TextView) dialog.findViewById(R.id.confirm_btn);
        if (!"".equals(confirmText)) {
            confirmBtn.setText(confirmText);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.cancel_btn) {
                    dialog.dismiss();

                } else if (i == R.id.confirm_btn) {
                    if (callback != null) {
                        String text = input.getText().toString();
                        callback.confirm(dialog, text);
                    }

                }
            }
        };
        cancelBtn.setOnClickListener(listener);
        confirmBtn.setOnClickListener(listener);
        return dialog;
    }

    public static Dialog messageDialog(Context context, String title, String message, String confirmText, final Callback2 callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_message);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setText(title);
        TextView content = (TextView) dialog.findViewById(R.id.content);
        content.setText(message);
        TextView confirmBtn = (TextView) dialog.findViewById(R.id.confirm_btn);
        if (!"".equals(confirmText)) {
            confirmBtn.setText(confirmText);
        }
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.confirm(dialog);
                }
            }
        });
        return dialog;
    }

    public static Dialog messageDialog(Context context, String title, String message, final Callback2 callback) {
        return messageDialog(context, title, message, "", callback);
    }


    /**
     * 用于网络请求等耗时操作的LoadingDialog
     */
    public static Dialog loadingDialog(Context context, String text) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_system_loading);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (!"".equals(text)) {
            TextView titleView = (TextView) dialog.findViewById(R.id.text);
            titleView.setText(text);
        }
        return dialog;
    }

    public static Dialog chooseImageDialog(Context context, final Callback2 onCameraClick, final Callback2 onAlbumClick) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_choose_img);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.btn_camera) {
                    if (onCameraClick != null) {
                        onCameraClick.confirm(dialog);
                    }

                } else if (i == R.id.btn_album) {
                    if (onAlbumClick != null) {
                        onAlbumClick.confirm(dialog);
                    }

                } else if (i == R.id.btn_cancel) {
                    dialog.dismiss();

                }
            }
        };
        dialog.findViewById(R.id.btn_camera).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_album).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        return dialog;
    }



    public static Dialog loadingDialog(Context context) {
        return loadingDialog(context, "");
    }

    public interface Callback {
        void confirm(Dialog dialog);

        void cancel(Dialog dialog);
    }

    public interface Callback2 {
        void confirm(Dialog dialog);
    }

    public interface Callback3 {
        void confirm(Dialog dialog, String text);
    }

}
