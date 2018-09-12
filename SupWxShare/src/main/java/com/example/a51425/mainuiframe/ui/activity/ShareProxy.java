package com.example.a51425.mainuiframe.ui.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import com.cyxk.wrframelibrary.framework.CallBackListener;
import com.cyxk.wrframelibrary.utils.LogUtil;
import com.cyxk.wrframelibrary.utils.StringUtils;
import com.cyxk.wrframelibrary.utils.ToastUtil;
import com.example.a51425.mainuiframe.APP;
import com.example.a51425.mainuiframe.R;
import com.example.a51425.mainuiframe.constant.Constant;
import com.example.a51425.mainuiframe.ui.ShareTask.ShareFragment;
import com.example.a51425.mainuiframe.ui.ShareTask.ShareFragmentPresenter;
import com.example.a51425.mainuiframe.ui.ShareTask.ShareLogic;
import com.example.a51425.mainuiframe.utils.ShareUtils;
import com.uzmap.pkg.uzcore.UZAppActivity;

import java.util.List;

import static com.example.a51425.mainuiframe.utils.Constant.shareImgToSession;
import static com.example.a51425.mainuiframe.utils.Constant.shareImgToTimeline;
import static com.example.a51425.mainuiframe.utils.Constant.shareToSession;
import static com.example.a51425.mainuiframe.utils.Constant.shareToTimeline;


public class ShareProxy  {
    private ShareFragmentPresenter mShareFragmentPresenter;
    private UZAppActivity mAppActivity;

    private String shareTitle = "空";
    private String shareImageUrl = "null";
    private String shareContent = "空";
    private String jumpUrl = "null";

    private String Imageurl = "http://bo.5173cdn.com/5173_2/data/201705/02/36/RQKowFknx1cAAAAAAACtQXcf_5g23.jpg";

    public  ShareProxy( UZAppActivity appActivity){
        mAppActivity=appActivity;
        mShareFragmentPresenter=new ShareFragmentPresenter(new ShareFragment(),new ShareLogic());
    }

    public  ShareProxy( UZAppActivity appActivity,String appid,String packagename){
        mAppActivity=appActivity;
        mShareFragmentPresenter=new ShareFragmentPresenter(new ShareFragment(),new ShareLogic(),appid,packagename);
    }

    public void initImage(int type,String url) {
        Imageurl=url;
        if (type==shareImgToSession|| type==shareImgToTimeline){
            ImageShare(type);
        }
        LogUtil.e(getClass().getName()+"_________initData");
    }
    public void initContent(int type,String title,String image,String content,String url) {
        shareTitle=title;
        shareImageUrl=image;
        shareContent=content;
        jumpUrl=url;
        if (type==shareToTimeline||type==shareToSession){
            ContentShare(type);
        }
        LogUtil.e(getClass().getName()+"_________initData");
    }



    public void onClick(int i){
        if (i == R.id.btn_shareWX1) {//分享到微信好友

            mShareFragmentPresenter.throughSdkShareWXFriends(mAppActivity, shareTitle, shareContent, shareImageUrl, jumpUrl, 0);

        } else if (i == R.id.btn_shareWX2) {//分享到微信朋友圈

            mShareFragmentPresenter.throughSdkShareWXFriends(mAppActivity, shareTitle, shareContent, shareImageUrl, jumpUrl, 1);


        } else if (i == R.id.btn_shareWX3) {//通过intent 分享到微信好友（只分享图片）

            String filePath = ShareUtils.createPhotoFile();
            if (TextUtils.isEmpty(filePath)) {
                ToastUtil.showToast(APP.getContext(),
                        "请检查是否插入SD卡");
                return;
            }
            final String imagePath = filePath + "/"
                    + "shareWx.png";

            ShareUtils.saveFile(Imageurl, imagePath, new CallBackListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ShareUtils.throughIntentShareWXImage(uri);
                }

                @Override
                public void onFailure() {

                }

            });

        } else if (i == R.id.btn_shareWX4) {
            ShareUtils.throughIntentShareWXdesc("hello");

        } else if (i == R.id.btn_shareWX5) {

            String path = ShareUtils.createPhotoFile();
            if (TextUtils.isEmpty(path)) {
                ToastUtil.showToast(APP.getContext(),
                        "请检查是否插入SD卡");
                return;
            }
            final String imagePath2 = path + "/"
                    + "shareWx.png";

            ShareUtils.saveFile(Imageurl, imagePath2, new CallBackListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ShareUtils.throughIntentShareWXCircle("hello", uri);
                }

                @Override
                public void onFailure() {

                }

            });
        }
    }

    public void ImageShare(int type){
        if (type==shareImgToTimeline){
            onClick(R.id.btn_shareWX5);
        }else if (type==shareImgToSession){
            onClick(R.id.btn_shareWX3);
        }
    }

    public void ContentShare(int type){
        if (type==shareToSession){
            onClick(R.id.btn_shareWX1);
        }else if (type==shareToTimeline){
            onClick(R.id.btn_shareWX2);
        }
    }

}
