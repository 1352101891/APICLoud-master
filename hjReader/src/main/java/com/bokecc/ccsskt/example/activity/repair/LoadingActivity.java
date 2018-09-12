package com.bokecc.ccsskt.example.activity.repair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.R2;
import com.bokecc.ccsskt.example.activity.StudentActivity;
import com.bokecc.ccsskt.example.activity.TeacherActivity;
import com.bokecc.ccsskt.example.base.BaseActivity;
import com.bokecc.ccsskt.example.entity.CloseEntity;
import com.bokecc.ccsskt.example.entity.MyEBEvent;
import com.bokecc.ccsskt.example.entity.TimingEntity;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.sskt.CCInteractSession;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

import static com.bokecc.ccsskt.example.activity.repair.Constant.DIRECTION;
import static com.bokecc.ccsskt.example.activity.repair.Constant.NICKNAME;
import static com.bokecc.ccsskt.example.activity.repair.Constant.PASSWORD;
import static com.bokecc.ccsskt.example.activity.repair.Constant.ROLE;
import static com.bokecc.ccsskt.example.activity.repair.Constant.URL;
import static com.bokecc.ccsskt.example.activity.repair.Constant.USERID;
import static com.bokecc.ccsskt.example.global.Config.mRole;
import static com.bokecc.ccsskt.example.global.Config.mRoomDes;
import static com.bokecc.ccsskt.example.global.Config.mRoomId;
import static com.bokecc.ccsskt.example.global.Config.mUserId;
import static com.bokecc.sskt.CCInteractSession.TALKER;

/**
 * Created by Administrator on 2018/4/8.
 */

public class LoadingActivity extends BaseActivity {
    private String yunKeChengUrl=null;
    private int  direction= Constant.HORIZON;
    private String password=null;
    public volatile boolean isJoin = false, isJump = false;
    private String mNickName=null;
    private int role=1;

    public static Intent newIntent(Context context, String url,String nickname,String password ) {
        Intent intent = new Intent(context, LoadingActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(PASSWORD, password);
        intent.putExtra(NICKNAME,nickname);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
    public static void startSelf(Context context, String url ) {
        context.startActivity(newIntent(  context,   url,    null, null ));
    }
    public static void startSelf(Context context, String url,  String nickname, String password ) {
        context.startActivity(newIntent(  context,   url,   nickname,  password ));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.loading_layout;
    }



    // 课堂模式（方向）0竖屏 1横屏
    @Override
    protected void onViewCreated() {

        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }

        Intent intent= getIntent();
        if (intent!=null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Log.e("LoadingActivity","onViewCreated->接受来bundle的信息");
                yunKeChengUrl = bundle.getString(Constant.URL);

                password = bundle.getString(Constant.PASSWORD);
                mNickName = bundle.getString(NICKNAME);
            }
        }
        goByLink(yunKeChengUrl);
    }


    void goByLink(String URL) {
        Log.e("LoadingActivity","goByLink->"+URL);
        String url = URL;
        if (TextUtils.isEmpty(url)) {
            showToast("无效链接");
            this.finish();
            return;
        }
        parseUrl(url);
    }


    //第一步解析url
    private void parseUrl(String url) {
        ParseMsgUtil.parseUrl(LoadingActivity.this, url, new ParseMsgUtil.ParseCallBack() {
            @Override
            public void onStart() {
                showProgress();
            }

            @Override
            public void onSuccess() {
                Log.e("LoadingActivity","parseUrl->success");
                fillForm();
            }

            @Override
            public void onFailure(String err) {
                toastOnUiThread(err);
                dismissProgress();
                toastOnUiThread("链接格式错误！");
                LoadingActivity.this.finish();
            }
        });
    }

    //第二步开始填入用户账号密码以及屏幕方向选择,请求房间链接
    public void fillForm(){
        if (mRole.equals("presenter")){
            role= Constant.PRESENTER;
        }else if(mRole.equals("talker")){
            role=TALKER;
        }

        if (direction== Constant.HORIZON){
            CCApplication.sClassDirection= Constant.HORIZON;
        }else {
            CCApplication.sClassDirection= Constant.VERTICAL;
        }
        goRoom();
    }

    //  role   presenter为0，talker为1
    private boolean IsNoPwd(){
        boolean flag= role != CCInteractSession.PRESENTER && mRoomDes.getAuthtype() == 2;
        return flag;
    }

    /**
     *      if(index==1){
     var password='123456'
     var url_ping='/presenter/'
     }
     if(index==2){
     var password='654321'
     var url_ping='/talker/'
     }
     if(index==3){
     var password='123456789'
     var url_ping=''
     }
     */

    void goRoom() {
        Log.e("LoadingActivity","goRoom");
        String mPwd = password;
        if (role == TALKER && IsNoPwd()) {
            if (TextUtils.isEmpty(mNickName)) {
                toastOnUiThread("昵称为空！");
                this.finish();

            }
        } else {
            if (TextUtils.isEmpty(mNickName) || TextUtils.isEmpty(mPwd)) {
                toastOnUiThread("昵称或者密码为空！");
                this.finish();

            }
        }
        /**
         * DirectionActivity.this, mRoomDes.getName(), mRoomDes.getDesc(), mRoomId, mUserId, mRole,
         */
        Log.e("LoadingActivity","login");
        isJoin = false;
        isJump = false;
        mInteractSession.login(mRoomId,
                mUserId, role, mNickName, mPwd, new CCInteractSession.OnLoginStatusListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("LoadingActivity","login->success");
                        saveUserMsg();
                        join();
                    }

                    @Override
                    public void onFailed(String err) {
                        dismissLoadingOnUiThread();
                        toastOnUiThread(err);
                    }
                });
    }

    private void saveUserMsg() {
        mSPUtil.put(mRoomId + "&" + mUserId + "&" + role + "-name", mNickName);
        if (!IsNoPwd())
            mSPUtil.put(mRoomId + "&" + mUserId + "&" + role + "-password", password);
    }

    private void join() {
        Log.e("LoadingActivity","joinRoom");
        mInteractSession.joinRoom(new CCInteractSession.AtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                synchronized (LoadingActivity.this) {
                    isJoin = true;
                    Log.e("LoadingActivity","joinRoom->success->isConnect："+CCApplication.isConnect );
                    //只有处在连接状态才跳转
                    if (CCApplication.isConnect && !isJump) {
                        isJump = true;
                        dismissLoadingOnUiThread();
                        if (role == CCInteractSession.PRESENTER) {
                            go(TeacherActivity.class);
                        } else {
                            go(StudentActivity.class);
                        }
                    }
                }
            }

            @Override
            public void onFailure(String err) {
                dismissLoadingOnUiThread();
                toastOnUiThread(err);
            }
        });
    }


    /**接受订阅消息，跳转到直播页面
        当发送了链接成功消息之后，CCApplication.isConnect会被置为true,
        详情见InteractSessionManager类的connect方法
     */
     @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        switch (event.what) {
            case Config.INTERACT_EVENT_WHAT_SERVER_CONNECT:
                synchronized (LoadingActivity.this) {

                    if (isJoin && !isJump) {
                        Log.e("LoadingActivity","onInteractEvent->isConnect:"+CCApplication.isConnect);
                        isJump = true;
                        dismissLoadingOnUiThread();
                        if (role == CCInteractSession.PRESENTER) {
                            go(TeacherActivity.class);
                        } else {
                            go(StudentActivity.class);
                        }
                    }
                }
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void InceptEvent(CloseEntity event) {
        switch (event.getObJ()) {
            case  "LoadingActivity":
                synchronized (LoadingActivity.this) {
                    TimingEntity entity=new TimingEntity(mUserId,mRoomId,event.getClasslength(),System.currentTimeMillis()/1000);
                    Bundle bundle= new Bundle();
                    bundle.putSerializable("TimingEntity",entity);
                    Intent intent=new Intent();
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    this.finish();
                }
                break;
        }
    }

    private void dismissLoadingOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }

        super.onDestroy();
    }

}
