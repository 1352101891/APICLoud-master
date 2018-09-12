package com.bokecc.ccsskt.example.activity.repair;

import android.content.Context;
import android.content.Intent;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.BaseActivity;


/**
 * Created by Administrator on 2018/4/17.
 */

public class percentActivity extends BaseActivity {
    private static Intent newIntent(Context context ) {
        Intent intent = new Intent(context, percentActivity.class);
        return intent;
    }

    public static void startSelf(Context context ) {
        context.startActivity(newIntent(  context));
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_percenttest;
    }

    @Override
    protected void onViewCreated() {

    }
}
