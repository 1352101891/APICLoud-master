package com.example.a51425.mainuiframe.ui.ShareTask;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.cyxk.wrframelibrary.base.MyBaseFragment;
import com.cyxk.wrframelibrary.framework.CallBackListener;
import com.cyxk.wrframelibrary.utils.ToastUtil;
import com.example.a51425.mainuiframe.APP;
import com.example.a51425.mainuiframe.R;
import com.example.a51425.mainuiframe.ui.dialogFragment.ShareLoadingDialogFragment;
import com.example.a51425.mainuiframe.utils.ShareUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import static com.example.a51425.mainuiframe.utils.Constant.shareImgToSession;
import static com.example.a51425.mainuiframe.utils.Constant.shareImgToTimeline;
import static com.example.a51425.mainuiframe.utils.Constant.shareToSession;
import static com.example.a51425.mainuiframe.utils.Constant.shareToTimeline;


public class ShareFragment extends MyBaseFragment implements ShareContract.View{

    private ShareFragmentPresenter mShareFragmentPresenter;


    @Override
    protected void registerBind(MyBaseFragment myBaseFragment, View view) {

    }

    @Override
    protected void unRegister() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {
    }

    @Override
    public void initData() {
        //正常来说是再返回数据成功时根据数据来判断显示那个布局
        stateLayout.showContentView();
    }


    @Override
    public View getContentView() {
        View inflate = View.inflate(mActivity, R.layout.share_fragment, null);
        mShareFragmentPresenter = new ShareFragmentPresenter(this,new ShareLogic());
        return inflate;
    }




    @Override
    public void showLoadingView() {

    }

    @Override
    public void showEmptyView() {

    }

    @Override
    public void showFailView() {

    }

    @Override
    public void showContentView() {

    }


}
