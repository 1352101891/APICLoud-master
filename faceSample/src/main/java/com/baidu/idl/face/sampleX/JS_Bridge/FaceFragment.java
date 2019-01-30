package com.baidu.idl.face.sampleX.JS_Bridge;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.idl.face.sampleX.R;
import com.baidu.idl.face.sample.callback.ILivenessCallBack;
import com.baidu.idl.face.sample.common.GlobalSet;
import com.baidu.idl.face.sample.manager.FaceSDKManager;
import com.baidu.idl.face.sample.model.LivenessModel;
import com.baidu.idl.face.sample.utils.DensityUtil;
import com.baidu.idl.face.sample.utils.FileUtils;
import com.baidu.idl.face.sample.utils.Utils;
import com.baidu.idl.face.sample.view.BinocularView;
import com.baidu.idl.face.sample.view.CircleImageView;
import com.baidu.idl.face.sample.view.CirclePercentView;
import com.baidu.idl.face.sample.view.MonocularView;
import com.baidu.idl.facesdk.model.Feature;

import java.util.ArrayList;

/**
 * Created by lvqiu on 2019/1/30.
 */

@SuppressLint("ValidFragment")
public class FaceFragment extends Fragment implements ILivenessCallBack, View.OnClickListener{

    private Context mContext;

    private RelativeLayout mCameraView;
    private BinocularView mBinocularView;
    private MonocularView mMonocularView;

    private CircleImageView mImage;
    private TextView mNickNameTv;
    private TextView mSimilariryTv;
    private TextView mNumTv;
    private TextView mDetectTv;
    private TextView mFeatureTv;
    private TextView mLiveTv;
    private TextView mAllTv;

    private Bitmap mBitmap;
    private String mUserName;

    private CirclePercentView mRgbCircleView;
    private CirclePercentView mNirCircleView;
    private CirclePercentView mDepthCircleView;

    private RelativeLayout mLayoutInfo;
    private LinearLayout mLinearTime;
    private LinearLayout mLinearUp;
    private ImageView mImageTrack;
    private View rootView;
    protected TextView mLableTxt;
    private FaceProxy prox;
    @SuppressLint("ValidFragment")
    public FaceFragment(FaceProxy proxy) {
        this.prox=proxy;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(99);
        if (rootView==null){
            mContext=inflater.getContext();
            rootView= inflater.inflate(R.layout.activity_pass,null);
            initView(rootView);
            initData();
        }
        return rootView;
    }

    private void initView(View view) {
        mLableTxt = view.findViewById(R.id.title);
        mLableTxt.setText(R.string.pass_1_n);
        mCameraView = view.findViewById(R.id.layout_camera);
        mImageTrack = view.findViewById(R.id.image_track);
        // 计算并适配显示图像容器的宽高
        calculateCameraView();
        mImage = view.findViewById(R.id.image);
        mNickNameTv = view.findViewById(R.id.tv_nick_name);
        mSimilariryTv = view.findViewById(R.id.tv_similarity);
        mNumTv = view.findViewById(R.id.tv_num);
        mDetectTv = view.findViewById(R.id.tv_detect);
        mFeatureTv = view.findViewById(R.id.tv_feature);
        mLiveTv = view.findViewById(R.id.tv_live);
        mAllTv = view.findViewById(R.id.tv_all);

        mRgbCircleView = view.findViewById(R.id.circle_rgb_live);
        mNirCircleView = view.findViewById(R.id.circle_nir_live);
        mDepthCircleView = view.findViewById(R.id.circle_depth_live);

        mLayoutInfo = view.findViewById(R.id.layout_info);
        mLinearTime = view.findViewById(R.id.linear_time);
        mLinearUp = view.findViewById(R.id.linear_up);
        RelativeLayout relativeDown = view.findViewById(R.id.relative_down);
        RelativeLayout relativeUp = view.findViewById(R.id.relative_up);
        relativeDown.setOnClickListener(this);
        relativeUp.setOnClickListener(this);
    }

    /**
     * 计算并适配显示图像容器的宽高
     */
    private void calculateCameraView() {
        String newPix;
        if (GlobalSet.getLiveStatusValue() == GlobalSet.LIVE_STATUS.RGN_NIR) {
            newPix = DensityUtil.calculateCameraOrbView(mContext);
        } else {
            newPix = DensityUtil.calculateCameraView(mContext);
        }
        String[] newPixs = newPix.split(" ");
        int newWidth = Integer.parseInt(newPixs[0]);
        int newHeight = Integer.parseInt(newPixs[1]);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(newWidth, newHeight);

        if (GlobalSet.getLiveStatusValue() == GlobalSet.LIVE_STATUS.RGN_NIR) {
            mBinocularView = new BinocularView(mContext);
            mBinocularView.setImageView(mImageTrack);
            mBinocularView.setLivenessCallBack(this);
            mCameraView.addView(mBinocularView, layoutParams);
        } else {
            mMonocularView = new MonocularView(mContext);
            mMonocularView.setImageView(mImageTrack);
            mMonocularView.setLivenessCallBack(this);
            mCameraView.addView(mMonocularView, layoutParams);
        }
    }

    private void initData() {
        FaceSDKManager faceSDKManager = FaceSDKManager.getInstance();
        int num =faceSDKManager.setFeature();
        mNumTv.setText(String.format("底库人脸数: %s 个", num));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GlobalSet.getLiveStatusValue() == GlobalSet.LIVE_STATUS.RGN_NIR) {
            mBinocularView.onResume();
        } else {
            mMonocularView.onResume();
        }
    }

    @Override
    public void onStop() {
        if (GlobalSet.getLiveStatusValue() == GlobalSet.LIVE_STATUS.RGN_NIR) {
            mBinocularView.onPause();
        } else {
            mMonocularView.onPause();
        }
        super.onStop();
    }

    @Override
    public void onTip(int code, String msg) {

    }

    @Override
    public void onCanvasRectCallback(LivenessModel livenessModel) {

    }

    @Override
    public void onCallback(final int code, final LivenessModel livenessModel) {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDetectTv.setText(String.format("人脸检测耗时: %s ms", livenessModel == null
                        ? 0 : livenessModel.getRgbDetectDuration()));
                mFeatureTv.setText(String.format("特征提取耗时: %s ms", livenessModel == null
                        ? 0 : livenessModel.getFeatureDuration()));
                mLiveTv.setText(String.format("活体检测耗时: %s ms", livenessModel == null
                        ? 0 : livenessModel.getLiveDuration()));
                mAllTv.setText(String.format("1:N人脸检索耗时: %s ms", livenessModel == null
                        ? 0 : livenessModel.getCheckDuration()));

                mRgbCircleView.setCurPercent(livenessModel == null
                        ? 0 : livenessModel.getRgbLivenessScore());

                mNirCircleView.setCurPercent(livenessModel == null
                        ? 0 : livenessModel.getIrLivenessScore());

                mDepthCircleView.setCurPercent(livenessModel == null
                        ? 0 : livenessModel.getDepthLivenessScore());

                if (livenessModel == null) {
                    mLayoutInfo.setVisibility(View.INVISIBLE);

                } else {
                    mLayoutInfo.setVisibility(View.VISIBLE);
                    if (code == 0) {
                        Feature feature = livenessModel.getFeature();
                        mSimilariryTv.setText(String.format("相似度: %s", livenessModel.getFeatureScore()));
                        mNickNameTv.setText(String.format("%s，你好!", feature.getUserName()));

                        if (!TextUtils.isEmpty(mUserName) && feature.getUserName().equals(mUserName)) {
                            mImage.setImageBitmap(mBitmap);
                        } else {
                            String imgPath = FileUtils.getFaceCropPicDirectory().getAbsolutePath()
                                    + "/" + feature.getCropImageName();
                            Bitmap bitmap = Utils.getBitmapFromFile(imgPath);
                            mImage.setImageBitmap(bitmap);
                            mBitmap = bitmap;
                            mUserName = feature.getUserName();
                        }
                    } else {
                        mSimilariryTv.setText("未匹配到相似人脸");
                        mNickNameTv.setText("陌生访客");
                        mImage.setImageResource(R.mipmap.preview_image_angle);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.relative_down) {
            mLinearTime.setVisibility(View.GONE);
            mLinearUp.setVisibility(View.VISIBLE);
        }

        if (view.getId() == R.id.relative_up) {
            mLinearTime.setVisibility(View.VISIBLE);
            mLinearUp.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    // 请求权限
    public void requestPermissions(int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                ArrayList<String> requestPerssionArr = new ArrayList<>();
                int hasCamrea = getContext().checkSelfPermission(Manifest.permission.CAMERA);
                if (hasCamrea != PackageManager.PERMISSION_GRANTED) {
                    requestPerssionArr.add(Manifest.permission.CAMERA);
                }

                int hasSdcardRead = getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                if (hasSdcardRead != PackageManager.PERMISSION_GRANTED) {
                    requestPerssionArr.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                int hasSdcardWrite = getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasSdcardWrite != PackageManager.PERMISSION_GRANTED) {
                    requestPerssionArr.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                // 是否应该显示权限请求
                if (!requestPerssionArr.isEmpty()) {
                    String[] requestArray = new String[requestPerssionArr.size()];
                    for (int i = 0; i < requestArray.length; i++) {
                        requestArray[i] = requestPerssionArr.get(i);
                    }
                    requestPermissions(requestArray, requestCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                flag = true;
            }
        }
        if (!flag) {
            Log.i("BaseActivity", "权限未申请");
        }
    }

    public void onBackClick(View view) {
        this.prox.closeFace();
    }

}
