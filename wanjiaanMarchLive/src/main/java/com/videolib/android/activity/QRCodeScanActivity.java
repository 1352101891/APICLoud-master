package com.videolib.android.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.google.zxing.Result;
import com.videolib.android.utils.Constant;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * QR Code Scanning Interface
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/3/28
 */
public class QRCodeScanActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView zXingScannerView;
    private final static String TAG = QRCodeScanActivity.class.getName();

    @Override
    protected void onCreate(  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zXingScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        zXingScannerView.setLaserEnabled(true);
        setContentView(zXingScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        if (!TextUtils.isEmpty(result.getText())) {
            setResult(RESULT_OK, getIntent().putExtra(Constant.KEY_QRSCAN, result.getText()));
            finish();
        } else {
            resumeCameraPreview();
        }
    }

    /*从新扫描*/
    private void resumeCameraPreview() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                zXingScannerView.resumeCameraPreview(QRCodeScanActivity.this);
            }
        },1000);
    }

    private class CustomViewFinderView extends ViewFinderView {
        public CustomViewFinderView(Context context) {
            super(context);
            setSquareViewFinder(true);
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setSquareViewFinder(true);
        }
    }
}
