package com.videolib.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.videolib.android.R;
import com.videolib.android.utils.Constant;
import com.worthcloud.avlib.widget.VideoPushView;


/**
 * Video streaming interface
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/4/24
 */
public class PushActivity extends Activity implements View.OnClickListener {
    private VideoPushView videoPushView;
    private Button buttonStart, buttonStop;
    private EditText editText;
    private boolean isCallPush = false;

    @Override
    protected void onCreate(  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        videoPushView = findViewById(R.id.VideoPushView);
        editText = findViewById(R.id.editText);
        editText.setText(Constant.PUSHURL);
        videoPushView.showPortraitFullScreen();
        buttonStart = findViewById(R.id.button5);
        buttonStart.setOnClickListener(this);
        buttonStop = findViewById(R.id.button6);
        buttonStop.setOnClickListener(this);
        buttonStart.setVisibility(View.VISIBLE);
        buttonStop.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button5) {
            if (TextUtils.isEmpty(editText.getText())) return;
            isCallPush = true;
            videoPushView.startPush(editText.getText().toString(), "0");
            buttonStop.setVisibility(View.VISIBLE);
            buttonStart.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
            videoPushView.setKeepScreenOn(true);

        } else if (i == R.id.button6) {
            isCallPush = false;
            videoPushView.setKeepScreenOn(false);
            videoPushView.stopPush("0");
            finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isCallPush) {
            videoPushView.setKeepScreenOn(false);
            videoPushView.stopPush("0");
        }
    }
}
