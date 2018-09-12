package com.bokecc.dwlivedemo_new.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.dwlivedemo_new.R;
import com.bokecc.dwlivedemo_new.R2;
import com.bokecc.dwlivedemo_new.adapter.LoginNavLvDownloadAdapter;
import com.bokecc.dwlivedemo_new.base.BaseActivity;
import com.bokecc.dwlivedemo_new.fragment.BaseFragment;
import com.bokecc.dwlivedemo_new.fragment.LiveFragment;

import com.bokecc.dwlivedemo_new.fragment.ReplayFragment;
import com.bokecc.dwlivedemo_new.module.CloseEntity;
import com.bokecc.dwlivedemo_new.third.scan.qr_codescan.MipcaActivityCapture;
import com.bokecc.dwlivedemo_new.view.LoginPopupWIndow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bokecc.dwlivedemo_new.util.constant.LiveName;
import static com.bokecc.dwlivedemo_new.util.constant.LivePassword;
import static com.bokecc.dwlivedemo_new.util.constant.LiveRoomid;
import static com.bokecc.dwlivedemo_new.util.constant.LiveUid;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayLiveid;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayName;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayPassword;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayRecordid;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayRoomid;
import static com.bokecc.dwlivedemo_new.util.constant.ReplayUid;

public class LoginActivity extends BaseActivity {

    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.tv_nav_title)
    TextView tvNavTitle;
    @BindView(R2.id.iv_nav_cbb)
    ImageView ivNavCbb;
    @BindView(R2.id.rl_na_title)
    RelativeLayout rlNaTitle;
    @BindView(R2.id.iv_scan)
    ImageView ivScan;
    @BindView(R2.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R2.id.fl)
    FrameLayout fl;

    private String[] listArray = new String[]{"观看直播", "观看回放"};

    /**
     * 云直播
     */
    public static void startSelf(Context context ,String a1,String a2,String a3,String a4 ) {
        context.startActivity(newIntent(  context, a1,  a2,  a3,  a4 ));
        isLive=true;
    }
    private static Intent newIntent(Context context,String a1,String a2,String a3,String a4){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LiveUid,a1);
        intent.putExtra(LiveRoomid,a2);
        intent.putExtra(LiveName,a3);
        intent.putExtra(LivePassword,a4);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }


    /**
       云回放
     */
    public static void startSelf(Context context ,String a1,String a2,String a3,String a4,String a5,String a6) {
        context.startActivity(newIntent(  context, a1,  a2,  a3,  a4,  a5,  a6 ));
        isLive=false;
    }
    private static Intent newIntent(Context context,String a1,String a2,String a3,String a4,String a5,String a6){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(ReplayUid,a1);
        intent.putExtra(ReplayRoomid,a2);
        intent.putExtra(ReplayLiveid,a3);
        intent.putExtra(ReplayRecordid,a4);
        intent.putExtra(ReplayName,a5);
        intent.putExtra(ReplayPassword,a6);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public   String  Uid ;
    public   String  Roomid ;
    public   String  Liveid ;
    public   String  Recordid ;
    public   String   Name ;
    public   String   Password ;
    public static boolean isLive=false;

    @Override
    protected void onViewCreated() {
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null && !isLive){
            Uid=bundle.getString(ReplayUid );
            Roomid=bundle.getString(ReplayRoomid );
            Liveid=bundle.getString(ReplayLiveid );
            Recordid=bundle.getString(ReplayRecordid );
            Name=bundle.getString(ReplayName );
            Password=bundle.getString(ReplayPassword );
        }else {
            Uid=bundle.getString(LiveUid );
            Roomid=bundle.getString(LiveRoomid );
            Name=bundle.getString(LiveName );
            Password=bundle.getString(LivePassword );
        }

        initTitleMenu();
        initFragments();
    }

    List<BaseFragment> fragmentList = new ArrayList<>();
    ReplayFragment replayFragment;
    LiveFragment liveFragment;
    private void initFragments() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        liveFragment = new LiveFragment();
        replayFragment=new ReplayFragment();
        fragmentList.add(replayFragment);
        fragmentList.add(liveFragment);

        if (isLive){ //打开直播间窗口
            transaction.add(R.id.fl, liveFragment );
            transaction.commit();
            liveFragment.performlogin(
                    Uid,
                    Roomid,
                    Name ,
                    Password );
        }else { //打开云回放窗口
            transaction.add(R.id.fl, replayFragment );
            transaction.commit();
            replayFragment.performlogin(
                    Uid,
                    Roomid,
                    Liveid ,
                    Recordid,
                    Name ,
                    Password );
        }
    }

    private void changFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fl, fragmentList.get(index));
        transaction.commit();
        tvNavTitle.setText(listArray[index]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R2.id.iv_back, R2.id.rl_na_title, R2.id.iv_scan})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back) {
            finish();

        } else if (i == R.id.rl_na_title) {
            showTitleMenu();

        } else if (i == R.id.iv_scan) {
            showScan();

        }
    }

    final int qrRequestCode = 111;

    private void showScan() {
        Intent intent = new Intent(this, MipcaActivityCapture.class);
        startActivityForResult(intent, qrRequestCode);
    }

    void showTitleMenu() {
        RotateAnimation animation = new RotateAnimation(360f, 180f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        animation.setFillAfter(true);
        ivNavCbb.startAnimation(animation);

        popupWindow.showAsDropDown(tvNavTitle,  -1 * tvNavTitle.getWidth() / 2, 0);
    }
    ListView listView;
    LoginPopupWIndow loginPopupWIndow = new LoginPopupWIndow();
    LoginNavLvDownloadAdapter loginNavLvDownloadAdapter;
    PopupWindow popupWindow;
    void initTitleMenu() {
        final View popupWindowVIew = LayoutInflater.from(this).inflate(R.layout.login_popupwindow, null);

        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(popupWindowVIew);

        popupWindow.setWidth(450);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.nav_bg_cbb));
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);

        listView = ButterKnife.findById(popupWindowVIew, R.id.lv_login_popupwindow);
        loginNavLvDownloadAdapter = new LoginNavLvDownloadAdapter(this, listArray);
        loginNavLvDownloadAdapter.setSelectItem(getIntent().getIntExtra("fragmentIndex", 0));
        listView.setAdapter(loginNavLvDownloadAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                loginNavLvDownloadAdapter.setSelectItem(i);
                changFragment(i);
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                RotateAnimation animation = new RotateAnimation(180f, 360f, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(300);
                animation.setFillAfter(true);
                ivNavCbb.startAnimation(animation);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case qrRequestCode:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    Log.e(LoginActivity.class.getSimpleName(), result);

                    if(!result.contains("userid=")) {
                        Toast.makeText(getApplicationContext(), "扫描失败，请扫描正确的播放二维码", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, String> map = parseUrl(result);
                    if (map == null) {
                        return;
                    }

                    for (int i = 0; i < fragmentList.size(); i++) {
                        BaseFragment fragment = fragmentList.get(i);
                        fragment.setLoginInfo(map);
                    }
                }
                break;
            default:
                break;
        }
    }

    private Map<String, String> parseUrl(String url) {
        Map<String, String> map = new HashMap<String, String>();
        String param = url.substring(url.indexOf("?") + 1, url.length());
        String[] params = param.split("&");

        if (params.length < 2) {
            return null;
        }
        for (String p : params) {
            String[] en = p.split("=");
            map.put(en[0], en[1]);
        }

        return map;
    }

}
