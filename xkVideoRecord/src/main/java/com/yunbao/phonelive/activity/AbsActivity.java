package com.yunbao.phonelive.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/8/3.
 */

public abstract class AbsActivity extends AppCompatActivity {

    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        //初始化application
        AppContext.getInstance(this.getApplication());

        setContentView(getLayoutId());
        mContext = this;
        main();
    }

    protected abstract int getLayoutId();

    protected abstract void main();

    protected void setTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //FixFocusedViewLeakUtil.fixFocusedViewLeak();
       // AppContext.sRefWatcher.watch(this);
        EventBus.getDefault().unregister(this);
    }
}
