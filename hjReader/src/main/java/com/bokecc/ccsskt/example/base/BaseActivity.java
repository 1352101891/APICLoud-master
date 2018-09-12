package com.bokecc.ccsskt.example.base;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Toast;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.activity.HomeActivity;
import com.bokecc.ccsskt.example.entity.UploadEntity;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.interact.InteractSessionManager;
import com.bokecc.ccsskt.example.popup.BottomCancelPopup;
import com.bokecc.ccsskt.example.popup.CommonPopup;
import com.bokecc.ccsskt.example.popup.LoadingPopup;
import com.bokecc.ccsskt.example.popup.NamedPopup;
import com.bokecc.ccsskt.example.popup.NotifyPopup;
import com.bokecc.ccsskt.example.popup.VotePopup;
import com.bokecc.ccsskt.example.popup.VoteResultPopup;
import com.bokecc.ccsskt.example.util.FileUtil;
import com.bokecc.ccsskt.example.util.RequestPermissionHelper;
import com.bokecc.ccsskt.example.util.SPUtil;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.bean.User;
import com.bokecc.sskt.bean.Vote;
import com.bokecc.sskt.bean.VoteResult;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 作者 ${郭鹏飞}.<br/>
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected View mRoot;

    protected Handler mHandler;
    protected InteractSessionManager mInteractSessionManager;
    protected CCInteractSession mInteractSession;
    protected EventBus mEventBus;
    private Unbinder mUnbinder;
    protected SPUtil mSPUtil;

    private Dialog mProgressDialog;
    private LoadingPopup mLoadingPopup;

    protected NotifyPopup mNotifyPopup;
    private CommonPopup mInvitePopup;
    private NamedPopup mNamedPopup;
    private VotePopup mVotePopup;
    private VoteResultPopup mVoteResultPopup;
    protected boolean isExit = false; // 是否退出

    protected BottomCancelPopup mUserPopup;
    // 用户相关操作
    private static final int FLAG_KICK_OUT_LIANMAI = 0x1000;
    private static final int FLAG_GAG = 0x1001;
    private static final int FLAG_CANCEL_GAG = 0x1002;
    private static final int FLAG_KICK_OUT = 0x1003;
    private static final int FLAG_ALLOW_MAI = 0x1004;
    private static final int FLAG_INVITE_MAI = 0x1005;
    private static final int FLAG_INVITE_CANCEL = 0x1006;
    private static final int FLAG_TOGGLE_MIC = 0x1007;
    private static final int FLAG_DRAW_DOC = 0x1008;
    private static final int FLAG_PULL_MAI = 0x1009;
    private SparseIntArray mActions = new SparseIntArray();
    protected User mCurUser;


    protected boolean isStop = false;
    private Timer mUserCountTimer;
    protected boolean isKick = false;

    protected boolean isPause = false;
    public boolean isGo = false;
    private CCApplication ccApplication_delegate;
    public boolean isToast = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ccApplication_delegate=  CCApplication.getInstance(this.getApplication());

        CCApplication.mAppStatus = 0;
        if (CCApplication.mAppStatus == -1) { // 如果被强杀不执行初始化操作
            protectApp();
        } else {
            beforeSetContentView();
            setContentView(getLayoutId());
            isKick = false;
            mUnbinder = ButterKnife.bind(this);
            mRoot = getWindow().getDecorView().findViewById(android.R.id.content);
            mSPUtil = SPUtil.getIntsance();
            mHandler = new Handler();
            mEventBus = EventBus.getDefault();
            mInteractSessionManager = InteractSessionManager.getInstance();
            mInteractSessionManager.setEventBus(mEventBus);
            mInteractSession = CCInteractSession.getInstance();
            initProgressDialog();
            initNotifyPopup();
            initLoadingPopup();
            initUserPopup();
            initNamedPopup();
            initInvitePopup();
            initVotePopup();
            initVoteResultPopup();
            onViewCreated();
        }

    }
    public final static int  UPLOADFILE=111111;

    public final static int write_read=1123;
    public final static int camera_audio=1134;
    public final static int gps_coarse=1145;

    public final static int  UPDATE_APP=111;
    public final static int REQUEST_COARSE_Permission=134;
    public final static int  REQUEST_GPS_Permission=132;

    public final static int GETPERMISSION=102;

    public final static int REQUEST_camera_Permission=113;
    public final static int REQUEST_audio_Permission=114;

    public final static int REQUEST_Read_Permission=151;
    public final static int  REQUEST_write_Permission=152;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_Permission:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage(REQUEST_COARSE_Permission,GETPERMISSION);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "没有打开权限，请打开网络定位权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_GPS_Permission:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage(REQUEST_GPS_Permission,GETPERMISSION);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "没有打开权限，请打开GPS定位权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_camera_Permission:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage(REQUEST_camera_Permission,GETPERMISSION);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "没有打开权限，请打开摄像头权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_audio_Permission:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage(REQUEST_audio_Permission,GETPERMISSION);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "没有打开权限，请打开麦克风权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_write_Permission:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage(REQUEST_write_Permission,GETPERMISSION);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "没有打开权限，请打开读文件权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_Read_Permission:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage(REQUEST_Read_Permission,GETPERMISSION);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "没有打开权限，请打开写文件权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public  void permissionToDo(int type){

    }

    public   Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if ( msg.what == GETPERMISSION) {
                if ((Integer) msg.obj== REQUEST_Read_Permission || (Integer) msg.obj== REQUEST_write_Permission)
                    if (RequestPermissionHelper.isPermissionStore(BaseActivity.this)){
                        permissionToDo(write_read);
                    }
                if ((Integer) msg.obj== REQUEST_camera_Permission || (Integer) msg.obj== REQUEST_audio_Permission){
                    if (RequestPermissionHelper.isPermissionCamera(BaseActivity.this)){
                        permissionToDo(camera_audio);
                    }
                }
                if ((Integer) msg.obj== REQUEST_GPS_Permission || (Integer) msg.obj== REQUEST_COARSE_Permission){
                    if (RequestPermissionHelper.isPermissionLocation(BaseActivity.this)){
                        permissionToDo(gps_coarse);
                    }
                }
            }
        }
    };

    protected View getContentView(){
        return this.findViewById(android.R.id.content);
    }

    public void sendMessage(Object object,int flag){
        Message message = handler.obtainMessage();
        message.obj=object;
        message.what = flag;
        handler.sendMessage(message);
    }


    public  void copyCache(final FileDescriptor fileDescriptor, final String desname,final int type ){
        new Thread(new Runnable() {
            @Override
            public void run() {
                long timeMillis=System.currentTimeMillis();
                String cachePath=CCApplication.getCacheDir()+timeMillis+"."+desname;
                try {
                    FileUtil.copyFileByDescriptor(fileDescriptor,new File(cachePath));
                    EventBus.getDefault().post(new UploadEntity(cachePath,UploadEntity.SUCCESS,type));
                } catch (IOException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new UploadEntity(e.getMessage(),UploadEntity.ERROR,type));
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStop = false;
        isPause = false;
        isGo = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop = true;
    }

    @Override
    protected void onDestroy() {
        cancelUserCount();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        ccApplication_delegate.clear();
        if(handler!=null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }

    /**
     * 在SetContentView之前进行操作，父类空实现，子类根据需要进行实现
     */
    protected void beforeSetContentView() {
    }

    /**
     * 获取布局id
     */
    protected abstract int getLayoutId();

    /**
     * 界面创建完成
     */
    protected abstract void onViewCreated();

    protected void loopUserCount() {
        mUserCountTimer = new Timer();
        mUserCountTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mUserCountTimer == null) {
                    return;
                }
                if (!isKick) {
                    mInteractSession.getUserCount();
                } else {
                    cancelUserCount();
                }
            }
        }, 500, 3000);
    }

    protected void cancelUserCount() {
        if (mUserCountTimer != null) {
            mUserCountTimer.cancel();
            mUserCountTimer = null;
        }
    }

    private void initProgressDialog() {
        mProgressDialog = new Dialog(this, R.style.ProgressDialog);
        mProgressDialog.setContentView(R.layout.progress_layout);
        mProgressDialog.setCancelable(false);
        mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void initLoadingPopup() {
        mLoadingPopup = new LoadingPopup(this);
        mLoadingPopup.setOutsideCancel(false);
        mLoadingPopup.setKeyBackCancel(false);
    }

    protected void showProgress() {
        if (mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.show();
    }

    private void initNamedPopup() {
        mNamedPopup = new NamedPopup(this);
        mNamedPopup.setKeyBackCancel(true);
        mNamedPopup.setOutsideCancel(false);
        mNamedPopup.setOnAnswerClickListener(new NamedPopup.OnAnswerClickListener() {
            @Override
            public void onAnswer() {
                mInteractSession.studentNamed();
            }
        });
    }

    private void initInvitePopup() {
        mInvitePopup = new CommonPopup(this);
        mInvitePopup.setTip("老师正邀请你连麦");
        mInvitePopup.setOKValue("接受");
        mInvitePopup.setCancelValue("拒绝");
        mInvitePopup.setOKClickListener(new CommonPopup.OnOKClickListener() {
            @Override
            public void onClick() {
                showLoading();
                mInteractSession.acceptTeacherInvite(new CCInteractSession.AtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(String err) {
                        dismissLoading();
                        toastOnUiThread(err);
                    }
                });
            }
        });
        mInvitePopup.setCancelClickListener(new CommonPopup.OnCancelClickListener() {
            @Override
            public void onClick() {
                showLoading();
                mInteractSession.refuseTeacherInvite(new CCInteractSession.AtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(String err) {
                        dismissLoading();
                        toastOnUiThread(err);
                    }
                });
            }
        });
    }

    private void initVotePopup() {
        mVotePopup = new VotePopup(this);
        mVotePopup.setKeyBackCancel(true);
        mVotePopup.setOutsideCancel(false); // TODO: 2017/8/15  不可点击消失
        mVotePopup.setOnCommitClickListener(new VotePopup.OnCommitClickListener() {
            @Override
            public void onCommit() {
                mVotePopup.sendVoteSelected(mInteractSession);
            }
        });
    }

    private void initVoteResultPopup() {
        mVoteResultPopup = new VoteResultPopup(this);
        mVoteResultPopup.setKeyBackCancel(true);
        mVoteResultPopup.setOutsideCancel(false);
    }

    protected void showVote(Vote vote) {
        if (mVoteResultPopup.isShowing()) {
            mVoteResultPopup.dismiss();
        }
        mVotePopup.show(vote, mRoot);
    }

    protected void dismissVote(String voteId) {
        if (mVotePopup.isShowing()) {
            mVotePopup.dismiss(voteId);
        }
    }

    protected void showVoteResult(VoteResult voteResult) {
        if (mVotePopup.isShowing()) {
            dismissVote(voteResult.getVoteId());
        }
        if (!mVoteResultPopup.isShowing()) {
            mVoteResultPopup.show(voteResult, mVotePopup.getResults(), mRoot);
        }
    }

    protected void dismissVoteResult() {
        if (mVoteResultPopup.isShowing()) {
            mVoteResultPopup.dismiss();
        }
    }

    protected void showNamed(int seconds) {
        // TODO: 2017/6/14  自行处理
        if (!mNamedPopup.isShowing()) {
            mNamedPopup.show(seconds, mRoot);
        }
    }

    protected void dismissNamed() {
        if (mNamedPopup.isShowing()) {
            mNamedPopup.dismiss();
        }
    }

    protected void showInvite() {
        if (!mInvitePopup.isShowing()) {
            mInvitePopup.show(mRoot);
        }
    }

    protected void dismissInvite() {
        if (mInvitePopup.isShowing()) {
            mInvitePopup.dismiss();
        }
    }

    protected void updateOrShowUserPopup(User user) {
        mCurUser = user;
        mActions.clear();
        mUserPopup.clear();
        int userPopupIndex = 0;
        mUserPopup.add(userPopupIndex, user.getUserSetting().isAllowDraw() ? "取消授权标注" : "授权标注");
        mActions.put(userPopupIndex++, FLAG_DRAW_DOC);
        if (user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_MAI_ING) {
            mUserPopup.add(userPopupIndex, "踢下麦");
            mActions.put(userPopupIndex++, FLAG_KICK_OUT_LIANMAI);
        } else {
            if (user.getLianmaiStatus() != CCInteractSession.LIANMAI_STATUS_UP_MAI &&
                    mInteractSession.getLianmaiMode() == CCInteractSession.LIANMAI_MODE_AUTO) {
                mUserPopup.add(userPopupIndex, "拉上麦");
                mActions.put(userPopupIndex++, FLAG_PULL_MAI);
            }
        }
        if (mInteractSession.getLianmaiMode() == CCInteractSession.LIANMAI_MODE_NAMED) {
            if (user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_IN_MAI) {
                mUserPopup.add(userPopupIndex, "同意上麦");
                mActions.put(userPopupIndex++, FLAG_ALLOW_MAI);
            }
            if (user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_IDLE) {
                mUserPopup.add(userPopupIndex, "邀请上麦");
                mActions.put(userPopupIndex++, FLAG_INVITE_MAI);
            }
            if (user.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_INVITE_MAI) {
                mUserPopup.add(userPopupIndex, "取消邀请");
                mActions.put(userPopupIndex++, FLAG_INVITE_CANCEL);
            }
        }
        if (user.getUserSetting().isAllowChat()) {
            mUserPopup.add(userPopupIndex, "禁言");
            mActions.put(userPopupIndex++, FLAG_GAG);
        } else {
            mUserPopup.add(userPopupIndex, "取消禁言");
            mActions.put(userPopupIndex++, FLAG_CANCEL_GAG);
        }
        mUserPopup.add(userPopupIndex, "踢出房间");
        mActions.put(userPopupIndex, FLAG_KICK_OUT);
        if (!mUserPopup.isShowing())
            mUserPopup.show(mRoot);
    }

    private void dealWithUserPopupAction(int action) {
        switch (action) {
            case FLAG_DRAW_DOC:
                if (mCurUser.getUserSetting().isAllowDraw()) {
                    mInteractSession.cancleAuthUserDraw(mCurUser.getUserId());
                } else {
                    mInteractSession.authUserDraw(mCurUser.getUserId());
                }
                break;
            case FLAG_KICK_OUT_LIANMAI:
                showLoading();
                mInteractSession.kickUserFromLianmai(mCurUser.getUserId(), new CCInteractSession.AtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(String err) {
                        dismissLoading();
                        toastOnUiThread(err);
                    }
                });
                break;
            case FLAG_TOGGLE_MIC:
                mInteractSession.toggleAudio(!mCurUser.getUserSetting().isAllowAudio(), mCurUser.getUserId());
                break;
            case FLAG_GAG:
                mInteractSession.gagOne(true, mCurUser.getUserId());
                break;
            case FLAG_CANCEL_GAG:
                mInteractSession.gagOne(false, mCurUser.getUserId());
                break;
            case FLAG_KICK_OUT:
                mInteractSession.kickUserFromRoom(mCurUser.getUserId());
                break;
            case FLAG_ALLOW_MAI:
                if (mCurUser.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_IN_MAI) {
                    showLoading();
                    mInteractSession.certainHandup(mCurUser.getUserId(), new CCInteractSession.AtlasCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissLoading();
                            toastOnUiThread(err);
                        }
                    });
                } else {
                    showToast("无效操作");
                }
                break;
            case FLAG_INVITE_MAI:
                if (mCurUser.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_IDLE) {
                    showLoading();
                    mInteractSession.inviteUserLianMai(mCurUser.getUserId(), new CCInteractSession.AtlasCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissLoading();
                            toastOnUiThread(err);
                        }
                    });
                } else if (mCurUser.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_IN_MAI) {
                    showLoading();
                    mInteractSession.certainHandup(mCurUser.getUserId(), new CCInteractSession.AtlasCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissLoading();
                            toastOnUiThread(err);
                        }
                    });
                }
                break;
            case FLAG_INVITE_CANCEL:
                if (mCurUser.getLianmaiStatus() == CCInteractSession.LIANMAI_STATUS_INVITE_MAI) {
                    showLoading();
                    mInteractSession.cancleInviteUserLianMai(mCurUser.getUserId(), new CCInteractSession.AtlasCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissLoading();
                            toastOnUiThread(err);
                        }
                    });
                }
                break;
            case FLAG_PULL_MAI:
                showLoading();
                mInteractSession.certainHandup(mCurUser.getUserId(), new CCInteractSession.AtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(String err) {
                        dismissLoading();
                        toastOnUiThread(err);
                    }
                });
                break;
        }
        mCurUser = null;
    }

    private void initUserPopup() {
        mUserPopup = new BottomCancelPopup(this);
        mUserPopup.setOutsideCancel(true);
        mUserPopup.setKeyBackCancel(true);
        ArrayList<String> datas = new ArrayList<>();
        mUserPopup.setChooseDatas(datas);
        mUserPopup.setOnChooseClickListener(new BottomCancelPopup.OnChooseClickListener() {
            @Override
            public void onClick(int index) {
                dealWithUserPopupAction(mActions.get(index));
            }
        });
    }

    protected void dismissProgress() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showLoading() {
        mLoadingPopup.show(mRoot);
    }

    protected void dismissLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingPopup.dismiss();
            }
        });
    }

    private void initNotifyPopup() {
        mNotifyPopup = new NotifyPopup(this);
        mNotifyPopup.setTip("当前用户掉线了");
        mNotifyPopup.setOKValue("确定");
        mNotifyPopup.setOutsideCancel(false);
        mNotifyPopup.setKeyBackCancel(false);
        mNotifyPopup.setOKClickListener(new NotifyPopup.OnOKClickListener() {
            @Override
            public void onClick() {
                isExit = true;
                cancelUserCount();
                mInteractSession.releaseAll();
                BaseActivity.this.finish();
//                Intent intent = new Intent(BaseActivity.this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            }
        });
    }

    /**
     * 应用被强杀 重启APP
     */
    protected void protectApp() {
        this.finish();
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.putExtra(Config.FORCE_KILL_ACTION, Config.FORCE_KILL_VALUE);
//        startActivity(intent);
    }

    /**
     * 进行吐司提示
     *
     * @param msg 提示内容
     */
    protected void showToast(String msg) {
        if (TextUtils.isEmpty(msg) || !isToast) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void toastOnUiThread(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(msg);
            }
        });
    }

    protected void go(Class clazz) {
        isGo = true;
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    protected void go(Class clazz, Bundle bundle) {
        isGo = true;
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void go(Class clazz, int requestCode) {
        isGo = true;
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    protected void go(Class clazz, int requestCode, Bundle bundle) {
        isGo = true;
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode, bundle);
    }


}
