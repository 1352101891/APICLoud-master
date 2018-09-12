package com.bokecc.ccsskt.example.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.R2;
import com.bokecc.ccsskt.example.activity.DocImgGridActivity;
import com.bokecc.ccsskt.example.adapter.ColorAdapter;
import com.bokecc.ccsskt.example.adapter.VideoAdapter;
import com.bokecc.ccsskt.example.base.BaseActivity;
import com.bokecc.ccsskt.example.bridge.OnDocInteractionListener;
import com.bokecc.ccsskt.example.bridge.OnTeacherLectureListener;
import com.bokecc.ccsskt.example.entity.ColorStatus;
import com.bokecc.ccsskt.example.entity.LayoutFixEntity;
import com.bokecc.ccsskt.example.entity.MyEBEvent;
import com.bokecc.ccsskt.example.entity.VideoStreamView;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.DividerGridItemDecoration;
import com.bokecc.ccsskt.example.recycle.MyLinearlayoutManager;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.ccsskt.example.recycle.RecycleViewDivider;
import com.bokecc.ccsskt.example.util.DensityUtil;
import com.bokecc.ccsskt.example.view.DocView;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.SubscribeRemoteStream;
import com.bokecc.sskt.base.exception.StreamException;
import com.bokecc.sskt.base.renderer.CCSurfaceRenderer;
import com.bokecc.sskt.doc.DocInfo;
import com.bumptech.glide.Glide;
import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleRelativeLayout;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.github.rongi.rotate_layout.layout.RotateLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.RendererCommon;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.bokecc.ccsskt.example.global.Config.KEY_DOC_POSITION;

public class LectureFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = LectureFragment.class.getSimpleName();
    private DocInfo mCurDocInfo, mPreDocInfo;

    private boolean isOnlyDoc = false;
    private boolean isDocFullScreen = false, isVideoFullScreen = false;
    private int mDocBottomDistance = -1;
    private boolean isCancelTask;

    private OnDocInteractionListener mOnDocInteractionListener;

    public LectureFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnDocInteractionListener) {
            mOnDocInteractionListener = (OnDocInteractionListener) context;
        }
        if (context instanceof OnTeacherLectureListener) {
            mOnTeacherLectureListener = (OnTeacherLectureListener) context;
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mRole == CCInteractSession.PRESENTER ) {
            mTeacherInteractionListener.dismissFollow();
            if(CCApplication.sClassDirection == 1){
                mTeacherInteractionListener.showVideoController();
            }
        }
    }

    @Override
    public void onStart() {
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
        super.onStart();
        executeDismissDocWidgets();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isOnlyDoc) {
            animateDocBottom(-mDocBottomDistance, false);
            mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
        } else {
            cancelDismissDocTask();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFullScreenRenderer = null;
        if (mDocView != null) {
            mDocView.recycle();
        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_lecture;
    }

    private int lastX;
    private int lastY;

    @Override
    protected void setUpView() {
        if (mRole == CCInteractSession.PRESENTER) {
            mDocProgress.setVisibility(View.GONE);
            mImgGrid.setVisibility(View.GONE);
            mDocView.setVisibility(View.VISIBLE);
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            if (isDocFullScreen || CCApplication.sClassDirection == 1) {
                mDocView.setTouchInterceptor(isAuth,mRole);
             
            }
            mInteractSession.setDocView(mDocView);
            mDocArea.setBackgroundColor(Color.parseColor("#ffffff"));
            if (CCApplication.sClassDirection == 0) {
                mFullScreen.setVisibility(View.VISIBLE);
                mVVideoLayout.setVisibility(View.VISIBLE);
                mHVideoLayout.setVisibility(View.GONE);
                mVideos = (RecyclerView) findViewById(R.id.id_doc_live_v_videos);
                mVideos.setLayoutManager(new MyLinearlayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, true));
                mVideos.addItemDecoration(new RecycleViewDivider(mActivity,
                        LinearLayoutManager.VERTICAL, DensityUtil.dp2px(mActivity, 4), Color.parseColor("#00000000"),
                        0, 0, RecycleViewDivider.TYPE_BETWEEN));
            } else {
                mFullScreen.setVisibility(View.GONE);
                mHVideoLayout.setVisibility(View.VISIBLE);
                mVVideoLayout.setVisibility(View.GONE);
                mVideos = (RecyclerView) findViewById(R.id.id_doc_live_h_videos);
                mVideos.setLayoutManager(new LinearLayoutManager(mActivity));
                mVideos.addItemDecoration(new RecycleViewDivider(mActivity,
                        LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(mActivity, 4), Color.parseColor("#00000000"),
                        0, 0, RecycleViewDivider.TYPE_BETWEEN));
            }
        } else { // 学生 进行横竖屏
            mDocProgress.setVisibility(View.GONE);
            mImgGrid.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            mDocView.setVisibility(View.VISIBLE);
            if (isDocFullScreen || CCApplication.sClassDirection == 1) {
                mDocView.setTouchInterceptor(isAuth,mRole);
              
            }
            mInteractSession.setDocView(mDocView);
            mDocArea.setBackgroundColor(Color.parseColor("#ffffff"));
            if (CCApplication.sClassDirection == 0) {
                mFullScreen.setVisibility(View.VISIBLE);
                mVVideoLayout.setVisibility(View.VISIBLE);
                mHVideoLayout.setVisibility(View.GONE);
                mVideos = (RecyclerView) findViewById(R.id.id_doc_live_v_videos);
                mVideos.setLayoutManager(new MyLinearlayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, true));
                mVideos.addItemDecoration(new RecycleViewDivider(mActivity,
                        LinearLayoutManager.VERTICAL, DensityUtil.dp2px(mActivity, 4), Color.parseColor("#00000000"),
                        0, 0, RecycleViewDivider.TYPE_BETWEEN));
            } else {
                mFullScreen.setVisibility(View.GONE);
                mHVideoLayout.setVisibility(View.VISIBLE);
                mVVideoLayout.setVisibility(View.GONE);
                mVideos = (RecyclerView) findViewById(R.id.id_doc_live_h_videos);
                mVideos.setLayoutManager(new LinearLayoutManager(mActivity));
                mVideos.addItemDecoration(new RecycleViewDivider(mActivity,
                        LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(mActivity, 4), Color.parseColor("#00000000"),
                        0, 0, RecycleViewDivider.TYPE_BETWEEN));
            }
        }

        initDrawPopup();

        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDrawLayout.getLayoutParams();
        mDrawLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取到手指处的横坐标和纵坐标
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://手指按下时
                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE://手指移动时
                        //计算移动的距离
                        int offX = x - lastX;
                        int offY = y - lastY;
                        params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                        params.topMargin = params.topMargin + offY;
                        params.leftMargin = params.leftMargin + offX;
                        if (params.topMargin < 0) {
                            params.topMargin = 0;
                        }
                        if (params.leftMargin < 0) {
                            params.leftMargin = 0;
                        }
                        if (params.topMargin > (DensityUtil.getHeight(mActivity) - mDrawLayout.getHeight())) {
                            params.topMargin = DensityUtil.getHeight(mActivity) - mDrawLayout.getHeight();
                        }
                        if (params.leftMargin > (DensityUtil.getWidth(mActivity) - mDrawLayout.getWidth())) {
                            params.leftMargin = DensityUtil.getWidth(mActivity) - mDrawLayout.getWidth();
                        }
                        mDrawLayout.setLayoutParams(params);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;

                }
                return true;
            }
        });

        calDocArea();
        mVideos.setAdapter(mVideoAdapter);
        mVideos.addOnItemTouchListener(new BaseOnItemTouch(mVideos,
                new OnClickListener() {
                    @Override
                    public void onClick(RecyclerView.ViewHolder viewHolder) {
                        int position = mVideos.getChildAdapterPosition(viewHolder.itemView);
                        VideoStreamView videoStreamView = mVideoStreamViews.get(position);
                        if (mRole == CCInteractSession.TALKER) {
                            if (isClickVideoFullScreen) {
                                return;
                            }
                            isClickVideoFullScreen = true;
                            videoFullScreen(position);
                        } else {
                            if (videoStreamView.getStream().getUserRole() == CCInteractSession.PRESENTER) {
                                if (isClickVideoFullScreen) {
                                    return;
                                }
                                isClickVideoFullScreen = true;
                                videoFullScreen(position);
                            } else {
                                if (mVideoClickListener != null) {
                                    mVideoClickListener.onVideoClick(position, videoStreamView);
                                }
                            }
                        }
                    }
                }));
        restoreDoc();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mVideos;
    }

    @Override
    public synchronized void notifyItemChanged(VideoStreamView videoStreamView, int position, boolean isAdd) {
        if (mVideoStreamViews == null) {
            return;
        }
        if (isAdd) {
            for (VideoStreamView temp :
                    mVideoStreamViews) {
                if (temp.getStream().getUserId().equals(videoStreamView.getStream().getUserId())) {
                    return;
                }
            }
            mVideoStreamViews.add(position, videoStreamView);
        } else {
            mVideoStreamViews.remove(videoStreamView);
        }
        if (!isViewInitialize) {
            return;
        }
        if (isAdd) {
            mVideoAdapter.notifyItemInserted(position);
        } else {
            mVideoAdapter.notifyItemRemoved(position);
        }
        if (position != mVideoStreamViews.size() - 1) {
            mVideoAdapter.notifyItemRangeChanged(position, mVideoStreamViews.size() - position);
        }
        fixVideolistheight();
        EventBus.getDefault().post(new LayoutFixEntity(mVideoAdapter.getItemCount()*VideoAdapter.itemHeight, LayoutFixEntity.Dir.BELOW));
    }

    public void fixVideolistheight(){
        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) mVideos.getLayoutParams();
        layoutParams.height= mVideoAdapter.getItemCount()* VideoAdapter.itemHeight;
        mVideos.setLayoutParams(layoutParams);
    }

    @Override
    public void notifyHandUp() {
        mDocArea.setEnabled(false);
        mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
        toggleDocBottomLayout();
    }

    public void videoFullScreen(int position) {
        try {
            if (isVideoFullScreen) {
                return;
            }
            if (!isOnlyDoc) {
                cancelDismissDocTask();
            }
            mOnDocInteractionListener.videoFullScreen();
            mVideoFullScreenLayout.setClickable(true);
            mVideoFullScreenLayout.setVisibility(View.VISIBLE);
            if (mFullScreenRenderer == null) {
                mFullScreenRenderer = new CCSurfaceRenderer(mActivity);
                RelativeLayout.LayoutParams params;
                if (CCApplication.sClassDirection == 0) {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                }
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mFullScreenRenderer.setLayoutParams(params);
                mFullScreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                mFullScreenRenderer.setZOrderOnTop(true);
                mFullScreenRenderer.setZOrderMediaOverlay(true);
                mSurfaceContainer.addView(mFullScreenRenderer);
            }
            mFullScreenRenderer.setVisibility(View.VISIBLE);
            mFullScreenRenderer.setBackgroundColor(Color.BLACK);
            mSurfaceContainer.setVisibility(View.VISIBLE);
            mInteractSession.initSurfaceContext(mFullScreenRenderer, mRendererEvents);
//            mVideoExit.bringToFront(); // 保证关闭全屏的控件始终在最上层
            mFullScreenStream = mVideoInteractionListener.getStream(position);
            mMicClose.setVisibility(mFullScreenStream.isAllowAudio() ? View.GONE : View.VISIBLE);
            // 学生仅音频模式
            if (mFullScreenStream.getUserRole() != CCInteractSession.PRESENTER &&
                    CCInteractSession.getInstance().getMediaMode() == CCInteractSession.MEDIA_MODE_AUDIO) {
                if (mFullScreenStream.getUserId().equals(CCInteractSession.SHARE_SCREEN_STREAM_ID)) {
                    mOtherLayout.setVisibility(View.GONE); // 显示音频贴图
                } else {
                    mOtherLayout.setVisibility(View.VISIBLE); // 显示音频贴图
                    if (CCApplication.sClassDirection == 0) {
                        mOtherLayout.setBackgroundResource(R.drawable.only_mic_bg);
                    } else {
                        mOtherLayout.setBackgroundResource(R.drawable.only_mic_bg_land);
                    }
                }
            } else {
                if (!mFullScreenStream.isAllowVideo()) { // 关闭视频
                    mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                    if (CCApplication.sClassDirection == 0) {
                        mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg);
                    } else {
                        mOtherLayout.setBackgroundResource(R.drawable.camera_close_bg_land);
                    }
                } else {
                    mOtherLayout.setVisibility(View.GONE);
                    if (mFullScreenStream.getRemoteStream() != null) {
                        if (!mFullScreenStream.getRemoteStream().hasAudio()) {
                            mMicClose.setVisibility(View.VISIBLE);
                            Glide.with(mActivity).load(R.drawable.no_mic_icon).into(mMicClose);
                        }
                        if (!mFullScreenStream.getRemoteStream().hasVideo()) {
                            mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                            if (CCApplication.sClassDirection == 0) {
                                mOtherLayout.setBackgroundResource(R.drawable.no_camera_icon);
                            } else {
                                mOtherLayout.setBackgroundResource(R.drawable.no_camera_icon_land);
                            }
                        }
                    }
                }
            }
            if (!mFullScreenStream.isLocalCameraStream()) {
                mFullScreenRenderer.setMirror(false);
                mFullScreenStream.attach(mFullScreenRenderer);
            } else {
                mInteractSession.attachLocalCameraStram(mFullScreenRenderer);
            }
            isVideoFullScreen = true;
        } catch (StreamException e) {
            mVideoFullScreenLayout.setVisibility(View.GONE);
            if (mFullScreenRenderer != null) {
                mFullScreenRenderer.setVisibility(View.GONE);
                mFullScreenRenderer.cleanFrame();
                mFullScreenRenderer = null;
            }

        }
    }

    private void exitVideoFullScreen() {
        if (!isVideoFullScreen) {
            return;
        }
        isClickVideoFullScreen = false;
        if (!isOnlyDoc) {
            executeDismissDocWidgets();
        }
        mOnDocInteractionListener.exitVideoFullScreen();
        mVideoFullScreenLayout.setVisibility(View.GONE);
        mFullScreenRenderer.setVisibility(View.GONE);
        try {
            if (mFullScreenStream.isLocalCameraStream()) {
                mInteractSession.detachLocalCameraStram(mFullScreenRenderer);
            } else {
                mFullScreenStream.getRemoteStream().detach(mFullScreenRenderer);
            }
            mFullScreenRenderer.cleanFrame();
            mFullScreenRenderer.release();
            isVideoFullScreen = false;
        } catch (StreamException ignored) {
            mFullScreenRenderer = null;
        }
    }

    public void authDraw(boolean isAuth) {
        this.isAuth = isAuth;
        if (isDocFullScreen || CCApplication.sClassDirection == 1) {
            if (isViewInitialize) {
            
                mDocView.setTouchInterceptor(isAuth,mRole);
                if (isDocFullScreen) {
                    mDrawLayout.setVisibility(isAuth ? View.VISIBLE : View.GONE);
                    if (mDrawLayout.getVisibility() == View.VISIBLE) {
//                        mEraser.setVisibility(View.GONE);
                        if(mRole == CCInteractSession.PRESENTER){
                            mPageChangeLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (!isAuth) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    mDocView.reset();
                }
            }
        }
    }

    public void setStrokeWidth(float width) {
        mDocView.setStrokeWidth(width);
    }

    public void setColor(int color, int colorStr) {
        mDocView.setColor(color, colorStr);
    }

    public void undo() {
        mDocView.undo();
    }

    public void clear() {
        mDocView.clear();
    }

    public boolean isWhitboard() {
        return mCurDocInfo == null;
    }

    public boolean dealWithFullscreen() {
        boolean flag = isVideoFullScreen || isDocFullScreen;
        exitVideoFullScreen();
        docExitFullScreen();
        mDocView.reset(); // 重置绘画板
        return flag;
    }

    public void updateFullScreenVideoIfShow(boolean flag, int changed) {
        if (!isVideoFullScreen)
            return;
        if (changed == 0) { // audio
            mMicClose.setVisibility(flag ? View.GONE : View.VISIBLE);
        } else if (changed == 1) {
            mOtherLayout.setVisibility(flag ? View.GONE : View.VISIBLE);
        }
    }

    public boolean isDocFullScreen() {
        return isDocFullScreen;
    }

    private void calDocArea() {
        ViewGroup.LayoutParams params = mDocArea.getLayoutParams();
        int width = DensityUtil.getWidth(mActivity);
        int height = width * 9 / 16;

        if (CCApplication.sClassDirection == 0) {
            mDocView.setWhiteboard(width, height);
        } else {
            int max = Math.max(DensityUtil.getHeight(mActivity), width);
            int min = Math.min(DensityUtil.getHeight(mActivity), width);
            mDocView.setWhiteboard(max, min);
        }
        if (CCApplication.sClassDirection == 0) {
            params.height = height;
        } else {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mDocArea.setLayoutParams(params);
    }

    public void showDocLoading(final long total) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPrepareLayout.setVisibility(View.VISIBLE);
                mUpdateTip.setVisibility(View.VISIBLE);
                mPrepareBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void dismissDocLoading(final String docId, final boolean isSuccess) {
        mPreDocInfo = mCurDocInfo;
        mCurDocInfo = null;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    // 进行恢复文档
                    mLocalDocId = docId;
                    mLocalPosition = 0;
                    mSPUtil.put(Config.KEY_DOC_ID + Config.mRoomId, mLocalDocId);
                    mSPUtil.put(KEY_DOC_POSITION, mLocalPosition);
                    fetchRoomDoc(1);
                } else {
                    mPrepareLayout.setVisibility(View.GONE);
                    mPrepareBar.setVisibility(View.GONE);
                    mUpdateTip.setVisibility(View.GONE);
                }
            }
        });
    }

    public void restoreNormal() {
//        if (mRole == CCInteractSession.TALKER) {
//            mDocView.clear();
//        }
        mDocArea.setEnabled(true);
        executeDismissDocWidgets();
    }

    @Override
    public void classStop() {
        mDocView.clearAll();
        dealWithFullscreen();
        mDocArea.setEnabled(false);
        if (isOnlyDoc) {
            animateDocBottom(-mDocBottomDistance, false);
            mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
        } else {
            cancelDismissDocTask();
        }
    }

    public void exitVideoFullScreenIfShow(SubscribeRemoteStream stream) {
        if (mVideoFullScreenLayout.getVisibility() == View.VISIBLE) {
            if (mFullScreenStream != null && mFullScreenStream.getUserId().equals(stream.getUserId())) {
                exitVideoFullScreen();
            }
        }
    }

    public void animateTop(int top) {
        if (mHVideoLayout != null) {
           //视频画面不参与动画
          // mHVideoLayout.animate().translationYBy(top).setDuration(100).start();
        }
    }

    public void setOnlyDoc(boolean onlyDoc) {
        isOnlyDoc = onlyDoc;
    }

    private Runnable dismissDocWidgetsTask = new Runnable() {
        @Override
        public void run() {
            if (mDocArea == null || mDocBottomLayout == null) {
                return;
            }
            mDocArea.setEnabled(false);
            if (mDocBottomDistance == -1) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDocBottomLayout.getLayoutParams();
                mDocBottomDistance = params.bottomMargin + mDocBottomLayout.getHeight();
            }
            if (isCancelTask) {
                return;
            }
            if (CCApplication.sClassDirection == 0) {
                mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
            } else {
                mDisplayInteractionListener.toggleTopAndBottom();
            }
            animateDocBottom(mDocBottomDistance, false);
        }
    };

    private void executeDismissDocWidgets() {
        if (mRole == CCInteractSession.TALKER && !mInteractSession.isRoomLive()) {
            mDocArea.setEnabled(false);
            return;
        }
        isCancelTask = false;
        mHandler.postDelayed(dismissDocWidgetsTask, 3000);
    }

    private void cancelDismissDocTask() {
        isCancelTask = true;
        mHandler.removeCallbacks(dismissDocWidgetsTask);
    }

    private void animateDocBottom(int bottom, final boolean isDoDismissTask) {
        if (mDocBottomLayout == null) {
            return;
        }
        mDocBottomLayout.animate().cancel();
        mDocBottomLayout.animate().translationYBy(bottom).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                isOnlyDoc = !isOnlyDoc;
                if (mDocArea != null && (
                        mRole == CCInteractSession.PRESENTER ||
                                (mRole == CCInteractSession.TALKER && mInteractSession.isRoomLive())
                )) {
                    mDocArea.setEnabled(true);
                }
                if (isDoDismissTask) {
                    executeDismissDocWidgets();
                }
            }
        }).start();
    }

    private void toggleDocBottomLayout() {
        if (mDocBottomDistance == -1) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDocBottomLayout.getLayoutParams();
            mDocBottomDistance = params.bottomMargin + mDocBottomLayout.getHeight();
        }
        if (isOnlyDoc) {
            animateDocBottom(-mDocBottomDistance, true);
        } else {
            cancelDismissDocTask();
            animateDocBottom(mDocBottomDistance, false);
        }
    }

    @OnClick(R2.id.id_lecture_doc_area)
    void toggleOperatingArea() {
        mDocArea.setEnabled(false);
        if (CCApplication.sClassDirection == 0) { // 竖屏
            mOnDocInteractionListener.toggleTopLayout(isOnlyDoc);
        } else { // 横屏
            mDisplayInteractionListener.toggleTopAndBottom();
        }
        toggleDocBottomLayout();
    }

    @OnClick(R2.id.id_lecture_doc_img_grid)
    void showDocImgGrid() {
        Bundle bundle = new Bundle();
        if (mCurDocInfo == null) {
            bundle.putStringArrayList("doc_img_list", null);
        } else {
            bundle.putStringArrayList("doc_img_list", mCurDocInfo.getAllImgUrls());
        }
        ((BaseActivity) mActivity).isGo = true;
        go(DocImgGridActivity.class, bundle, Config.LECTURE_REQUEST_CODE);
    }

    @OnClick(R2.id.id_lecture_doc_fullscreen)
    void docFullScreen() {
        if (isDocFullScreen) {
            return;
        }
        cancelDismissDocTask();
        mDocArea.setEnabled(false);
        mFullScreen.setVisibility(View.GONE);
        mExitFullScreen.setVisibility(View.VISIBLE);
        if (mRole == CCInteractSession.PRESENTER) {
            mImgGrid.setVisibility(View.GONE);
            mDocView.setTouchInterceptor(true,mRole);
            mDrawLayout.setVisibility(View.VISIBLE);
            mClear.setVisibility(View.VISIBLE);
            mDocForward.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            if (isWhitboard()) {
                mPageChangeLayout.setVisibility(View.GONE);
            } else {
                if(mCurDocInfo.getPageTotalNum() == 1) {
                    mPageChangeLayout.setVisibility(View.GONE);
                } else {
                    mPageChangeLayout.setVisibility(View.VISIBLE);
                }
            }
        }
        ViewGroup.LayoutParams params = mDocArea.getLayoutParams();
        params.height = DensityUtil.getWidth(mActivity);
        mDocArea.setLayoutParams(params);
//        mDocRotate.setAngle(270);
        mDocView.rotate(true);
        if (isAuth) { // 文档全屏如果被授权的时候 设置文档view可绘制
            mDocView.setTouchInterceptor(true,mRole);
            mDrawLayout.setVisibility(View.VISIBLE);
        } else {
            mDocView.setTouchInterceptor(false,mRole);
            mDrawLayout.setVisibility(View.GONE);
        }
        mOnDocInteractionListener.docFullScreen();
        isDocFullScreen = true;
    }

    @OnClick(R2.id.id_lecture_doc_exit_fullscreen)
    void docExitFullScreen() {
        if (!isDocFullScreen) {
            return;
        }
        mDocView.setTouchInterceptor(false,mRole);
        mDocView.rotate(false);
        mDrawLayout.setVisibility(View.GONE);
        executeDismissDocWidgets();
        mDocArea.setEnabled(true);
        mFullScreen.setVisibility(View.VISIBLE);
        mExitFullScreen.setVisibility(View.GONE);
        if (mRole == CCInteractSession.PRESENTER) {
            if (!isWhitboard()) {
                if(mCurDocInfo.getPageTotalNum() == 1){
                    mDocForward.setVisibility(View.GONE);
                    mDocBack.setVisibility(View.GONE);
                } else {
                    mImgGrid.setVisibility(View.VISIBLE);
                    if (mCurPosition == 0) {
                        mDocForward.setVisibility(View.VISIBLE);
                    } else if (mCurPosition == mCurDocInfo.getPageTotalNum() - 1) {
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.VISIBLE);
                    } else {
                        mDocForward.setVisibility(View.VISIBLE);
                        mDocBack.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        int width = DensityUtil.getHeight(mActivity);
        int height = width * 9 / 16;
        ViewGroup.LayoutParams params = mDocArea.getLayoutParams();
        params.height = height;
        mDocArea.setLayoutParams(params);
//        mDocRotate.setAngle(0);
        mOnDocInteractionListener.exitDocFullScreen();
        isDocFullScreen = false;
    }

    @OnClick(R2.id.id_lecture_video_exit)
    void doExitVideoFullScreen() {
        exitVideoFullScreen();
    }

    @OnClick(R2.id.id_lecture_draw_undo)
    void doUndo() {
        undo();
    }
    @OnClick(R2.id.id_lecture_draw_clear)
    void doClear() {
        clear();
    }
    @OnClick(R2.id.id_lecture_draw_paint)
    void showPaint() {
        if ((mDrawLayout.getY() + mDrawLayout.getHeight() + 20 + mPopupView.getHeight()) >
                DensityUtil.getHeight(mActivity)) {
            mPopupWindow.showArrowTo(mDrawPaint, BubbleStyle.ArrowDirection.Down, 20);
        } else {
            mPopupWindow.showArrowTo(mDrawPaint, BubbleStyle.ArrowDirection.Up, 20);
        }
    }

    @OnClick(R2.id.id_lecture_bar_doc_back)
    void barDocBack() {
        docBack();
    }

    @OnClick(R2.id.id_lecture_bar_doc_forward)
    void barDocForward() {
        docForward();
    }

    @OnClick(R2.id.id_lecture_doc_back)
    public void docBack() {
        mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(--mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
        sendPageChange(mCurPosition);
    }

    @OnClick(R2.id.id_lecture_doc_forward)
    public void docForward() {
        mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(++mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
        sendPageChange(mCurPosition);
    }

    @BindView(R2.id.id_lecture_doc_area)
    RelativeLayout mDocArea;
    @BindView(R2.id.id_lecture_doc_bottom_layout)
    RelativeLayout mDocBottomLayout;
    @BindView(R2.id.id_lecture_doc_progress)
    ProgressBar mDocProgress;
    @BindView(R2.id.id_lecture_doc_display)
    DocView mDocView;
    @BindView(R2.id.id_lecture_doc_back)
    ImageButton mDocBack;
    @BindView(R2.id.id_lecture_doc_forward)
    ImageButton mDocForward;
    @BindView(R2.id.id_lecture_doc_img_grid)
    ImageButton mImgGrid;
    @BindView(R2.id.id_lecture_doc_fullscreen)
    ImageButton mFullScreen;
    @BindView(R2.id.id_lecture_prepare_layout)
    RelativeLayout mPrepareLayout;
    @BindView(R2.id.id_lecture_pb)
    ProgressBar mPrepareBar;
    @BindView(R2.id.id_lecture_doc_update_tip)
    TextView mUpdateTip;
    @BindView(R2.id.id_lecture_doc_exit_fullscreen)
    ImageButton mExitFullScreen;
    @BindView(R2.id.id_lecture_v_video_layout)
    RelativeLayout mVVideoLayout;
    @BindView(R2.id.id_lecture_h_video_layout)
    RelativeLayout mHVideoLayout;
    RecyclerView mVideos;
    @BindView(R2.id.id_lecture_video_full_screen_layout)
    RelativeLayout mVideoFullScreenLayout;
    CCSurfaceRenderer mFullScreenRenderer;
    @BindView(R2.id.id_lecture_video_full_screen_container)
    RelativeLayout mSurfaceContainer;
    @BindView(R2.id.id_lecture_video_full_screen_other_layout)
    RelativeLayout mOtherLayout;
    @BindView(R2.id.id_lecture_video_full_screen_mic_close)
    ImageView mMicClose;
    @BindView(R2.id.id_lecture_doc_rotate)
    RotateLayout mDocRotate;
    @BindView(R2.id.id_lecture_drag_child)
    LinearLayout mDrawLayout;
    @BindView(R2.id.id_lecture_draw_clear)
    ImageButton mClear;
    @BindView(R2.id.id_lecture_page_change_layout)
    LinearLayout mPageChangeLayout;
    @BindView(R2.id.id_lecture_bar_doc_back)
    ImageButton mBarDocBack;
    @BindView(R2.id.id_lecture_bar_doc_forward)
    ImageButton mBarDocForward;
    @BindView(R2.id.id_lecture_draw_paint)
    ImageButton mDrawPaint;
    @BindView(R2.id.id_lecture_bar_doc_index)
    TextView mDocIndex;
    @BindView(R2.id.id_lecture_draw_undo)
    ImageButton mUndo;

    private RotateLayout mPopupRoot;
    private View mPopupView;
    private ImageButton mSmallSize, mMidSize, mLargeSize;
    private RecyclerView mColors;
    private BubbleRelativeLayout mBubbleLayout;
    private BubblePopupWindow mPopupWindow;
    private final int[] mColorResIds = new int[]{
            R.drawable.black_selector, R.drawable.orange_selector, R.drawable.green_selector,
            R.drawable.blue_selector, R.drawable.gray_selector, R.drawable.red_selector
    };
    private final int[] mColorValues = new int[]{
            Color.parseColor("#000000"), Color.parseColor("#f27c19"), Color.parseColor("#27c127"),
            Color.parseColor("#4a9fda"), Color.parseColor("#8b8b8b"), Color.parseColor("#ce2626")
    };
    private final String[] mColorStr = new String[]{
            "000000", "f27a1a", "70c75e", "78a7f5", "7b797a", "e33423"
    };
    private int mDrawColorPosition = 0;

    private SubscribeRemoteStream mFullScreenStream;
    private boolean isClickVideoFullScreen = false;
    private int mCurPosition = -1;

    private String mLocalDocId;
    private int mLocalPosition;

    private boolean isAuth = false;
    private OnTeacherLectureListener mOnTeacherLectureListener;

    private RendererCommon.RendererEvents mRendererEvents = new RendererCommon.RendererEvents() {
        @Override
        public void onFirstFrameRendered() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mFullScreenRenderer != null) {
                        mFullScreenRenderer.setBackgroundColor(Color.TRANSPARENT); // 加载到第一帧数据设置背景透明
                    }
                }
            });
        }

        @Override
        public void onFrameResolutionChanged(int i, int i1, int i2) {

        }
    };

    public static LectureFragment newInstance(int role) {
        Bundle args = new Bundle();
        args.putInt(KEY_PARAM_ROLE, role);
        LectureFragment fragment = new LectureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void initDrawPopup() {
        mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.draw_bubble_layout, null);
        mPopupRoot = (RotateLayout) mPopupView.findViewById(R.id.id_bubble_root);
//        mPopupRoot.setAngle(270);
        mBubbleLayout = (BubbleRelativeLayout) mPopupView.findViewById(R.id.id_bubble_layout);
        mPopupWindow = new BubblePopupWindow(mPopupView, mBubbleLayout);
        mPopupWindow.setCancelOnTouch(false);
        mPopupWindow.setCancelOnTouchOutside(true);
        mSmallSize = (ImageButton) mPopupView.findViewById(R.id.id_small_size);
        mMidSize = (ImageButton) mPopupView.findViewById(R.id.id_mid_size);
        mLargeSize = (ImageButton) mPopupView.findViewById(R.id.id_large_size);
        mColors = (RecyclerView) mPopupView.findViewById(R.id.id_draw_bubble_colors);
        mSmallSize.setOnClickListener(this);
        mMidSize.setOnClickListener(this);
        mLargeSize.setOnClickListener(this);
        mSmallSize.setSelected(true);
        mMidSize.setSelected(false);
        mLargeSize.setSelected(false);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        mColors.setLayoutManager(layoutManager);
        final ColorAdapter colorAdapter = new ColorAdapter(mActivity);
        ArrayList<ColorStatus> colorStatuses = new ArrayList<>();
        ColorStatus colorStatus;
        for (int i = 0; i < mColorResIds.length; i++) {
            colorStatus = new ColorStatus();
            if (i == 4) {
                colorStatus.setSelected(true);
            } else {
                colorStatus.setSelected(false);
            }
            colorStatus.setResId(mColorResIds[i]);
            colorStatuses.add(colorStatus);
        }
        colorAdapter.bindDatas(colorStatuses);
        mColors.addItemDecoration(new DividerGridItemDecoration(mActivity));
        mColors.setAdapter(colorAdapter);
        mColors.addOnItemTouchListener(new BaseOnItemTouch(mColors, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                final int position = mColors.getChildAdapterPosition(viewHolder.itemView);
                if (mDrawColorPosition == position) {
                    return;
                }
                ColorStatus pre = new ColorStatus();
                pre.setSelected(false);
                pre.setResId(colorAdapter.getDatas().get(mDrawColorPosition).getResId());
                colorAdapter.update(mDrawColorPosition, pre);
                ColorStatus cur = new ColorStatus();
                cur.setSelected(true);
                cur.setResId(colorAdapter.getDatas().get(position).getResId());
                colorAdapter.update(position, cur);
                mDrawColorPosition = position;
                // 设置颜色
                setColor(mColorValues[position], Integer.parseInt(mColorStr[position], 16));
            }
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Config.DOC_GRID_RESULT_CODE) {
            int position = data.getIntExtra("doc_img_grid_position", 0);
            setDocImg(position);
        }
    }

    private void setDocImg(int position) {
        mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(position), position, mCurDocInfo.getDocId(), mCurDocInfo.getName());
        sendPageChange(position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        if (mCurDocInfo != null && mCurDocInfo.getDocId().equals(event.obj)) {
            mDocProgress.setVisibility(View.GONE);
            mImgGrid.setVisibility(View.GONE);
            mFullScreen.setVisibility(View.GONE);
            mCurDocInfo = null;
            sendCurrentDocPage();
        }
    }

    /**
     * 根据本地信息回复文档
     */
    public void restoreDoc() {
        if (mCurDocInfo == null) {
            mLocalDocId = mSPUtil.getString(Config.KEY_DOC_ID + Config.mRoomId);
            mLocalPosition = mSPUtil.getInt(KEY_DOC_POSITION);
            if (TextUtils.isEmpty(mLocalDocId) || mLocalPosition == -1) {
                // TODO: 2017/10/13  具体实现待商榷
                if (mInteractSession.isRoomLive()) {
                    sendPageChange(-1);
                }
                return;
            }
            fetchRoomDoc(0);
        } else {
            setDocInfo(mCurDocInfo, mCurPosition, 0);
        }
    }

    @Override
    public void onClick(View v) {
        clearSizesStatus();
        v.setSelected(!v.isSelected());
        float size = 1.5f;
        int i = v.getId();
        if (i == R.id.id_small_size) {
            size = 1.5f;

        } else if (i == R.id.id_mid_size) {
            size = 4.5f;

        } else if (i == R.id.id_large_size) {
            size = 7.5f;

        }
        // 设置尺寸
        setStrokeWidth(size);
    }

    private void clearSizesStatus() {
        mSmallSize.setSelected(false);
        mMidSize.setSelected(false);
        mLargeSize.setSelected(false);
    }

    private final class RetryTask implements Runnable {

        private int where;

        RetryTask(int where) {
            this.where = where;
        }

        @Override
        public void run() {
            fetchRoomDoc(where);
        }
    }

    private class RoomCallBack implements CCInteractSession.AtlasCallBack<DocInfo> {

        private int where;

        RoomCallBack(int where) {
            this.where = where;
        }

        @Override
        public void onSuccess(DocInfo docInfo) {
            if (mCurDocInfo != null) {
                return;
            }
            if (docInfo == null) {
                // 表示当前文档正在转换格式
                mHandler.postDelayed(new RetryTask(where), 1500);
                return;
            }
            setDocInfo(docInfo, mLocalPosition, where);
            if (where == 1 && isViewInitialize) {
                mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(mLocalPosition), mLocalPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                mPrepareLayout.setVisibility(View.GONE);
                mPrepareBar.setVisibility(View.GONE);
                mUpdateTip.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFailure(String err) {
            // 做一次数据恢复
            mCurDocInfo = mPreDocInfo;
            mPreDocInfo = null;
            // 隐藏图片区
            if (where == 1 && isViewInitialize) {
                mPrepareLayout.setVisibility(View.GONE);
                mPrepareBar.setVisibility(View.GONE);
                mUpdateTip.setVisibility(View.GONE);
            }
            if (where == 1) {
                mPrepareLayout.setVisibility(View.GONE);
                mPrepareBar.setVisibility(View.GONE);
                mUpdateTip.setVisibility(View.GONE);
            }
        }
    }

    private void fetchRoomDoc(int where) {
        mInteractSession.getRoomDoc(null, mLocalDocId, new RoomCallBack(where));
    }

    public void sendCurrentDocPage() {
        if (mCurDocInfo == null) {
            mCurPosition = -1;
            mSPUtil.put(Config.KEY_DOC_ID + Config.mRoomId, "WhiteBorad");
            mSPUtil.put(KEY_DOC_POSITION, mCurPosition);
        }
        sendPageChange(mCurPosition);
    }

    public void setDocInfo(DocInfo docInfo, int position, int where) {
        mCurDocInfo = docInfo;
        mCurPosition = position;
        if (!isViewInitialize) {// 布局以及销毁
            return;
        }
        
        mDocProgress.setVisibility(View.GONE);
        mDocProgress.setMax(docInfo.getPageTotalNum());
        mDocProgress.setProgress(mCurPosition + 1);
        if(CCApplication.sClassDirection == 1){
            mFullScreen.setVisibility(View.GONE);
        } else {
            mFullScreen.setVisibility(View.VISIBLE);
        }
        if (where == 0) {
            if (docInfo.getAllImgUrls().size() > 1) {
                if (mRole == CCInteractSession.PRESENTER) {
                    if(CCApplication.sClassDirection == 1){
                        mImgGrid.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mImgGrid.setVisibility(View.VISIBLE);
                        if(isDocFullScreen) {
                            mDocForward.setVisibility(View.GONE);
                        } else {
                            mDocForward.setVisibility(View.VISIBLE);
                        }
                    }
                    mDocBack.setVisibility(View.GONE);
                }
            } else {
                mImgGrid.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
                mDocBack.setVisibility(View.GONE);
            }
            mPrepareLayout.setVisibility(View.GONE);
        } else {
            mPrepareLayout.setVisibility(View.VISIBLE);
            mImgGrid.setVisibility(View.GONE);
        }
        // 通知老师更新界面
        if (mOnTeacherLectureListener != null) {
            mOnTeacherLectureListener.onLecture(position+1, docInfo.getAllImgUrls().size());
        }

        mSPUtil.put(Config.KEY_DOC_ID + Config.mRoomId, mCurDocInfo.getDocId());
        mSPUtil.put(KEY_DOC_POSITION, mCurPosition);
        if (mInteractSession.isRoomLive() && mRole == CCInteractSession.PRESENTER) {
            sendPageChange(mCurPosition);
        }
    }

    private void sendPageChange(int position) {
        if (position == -1) { // 发送白板
            mInteractSession.docPageChange("WhiteBorad", "WhiteBorad", position, "#", position);
            return;
        }
        // 通知老师更新界面
        if (mOnTeacherLectureListener != null) {
            mOnTeacherLectureListener.onLecture(position+1, mCurDocInfo.getPageTotalNum());
        }
        mDocIndex.setText(position+1 + "/" + mCurDocInfo.getPageTotalNum());

        if (mCurDocInfo.getPageTotalNum() == 1) {
            if(isDocFullScreen){
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            } else {
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            }
            mBarDocBack.setEnabled(false);
            mBarDocForward.setEnabled(false);
        } else {
            if (position == 0) {
                if(CCApplication.sClassDirection == 0){
                    if(isDocFullScreen){
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                    mBarDocBack.setEnabled(false);
                    mBarDocForward.setEnabled(true);
                }
            } else if (position == mCurDocInfo.getPageTotalNum() - 1) {
                if(CCApplication.sClassDirection == 0){
                    if(isDocFullScreen){
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.VISIBLE);
                    }
                    mBarDocBack.setEnabled(true);
                    mBarDocForward.setEnabled(false);
                }
            } else {
                if(CCApplication.sClassDirection == 0){
                    if(isDocFullScreen) {
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                    mBarDocBack.setEnabled(true);
                    mBarDocForward.setEnabled(true);
                }
            }
        }
        String url = mCurDocInfo.getAllImgUrls().get(position);
        mInteractSession.docPageChange(mCurDocInfo.getDocId(), mCurDocInfo.getName(), mCurDocInfo.getPageTotalNum(),
                url, position);
    }

}
