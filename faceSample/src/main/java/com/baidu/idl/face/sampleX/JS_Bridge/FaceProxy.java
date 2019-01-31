package com.baidu.idl.face.sampleX.JS_Bridge;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.idl.face.sampleX.R;
import com.baidu.idl.face.sample.common.GlobalSet;
import com.baidu.idl.face.sample.db.DBManager;
import com.baidu.idl.face.sample.listener.OnImportListener;
import com.baidu.idl.face.sample.manager.FaceSDKManager;
import com.baidu.idl.face.sample.manager.ImportFileManager;
import com.baidu.idl.face.sample.utils.FileUtils;
import com.baidu.idl.face.sample.utils.ToastUtils;
import com.baidu.idl.face.sampleX.view.MyDIaLog;
import com.baidu.idl.facesdk.FaceAuth;
import com.baidu.idl.facesdk.callback.AuthCallback;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;

import static com.baidu.idl.face.sample.common.GlobalSet.LICENSE_ONLINE;

/**
 * Created by lvqiu on 2019/1/30.
 */

public class FaceProxy implements AuthCallback,OnImportListener {
    private UZModule uzModule;
    private Context context;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private ImportFileManager fileManager;
    private FaceAuth faceAuth;
    private String keyCode="";
    private int x,y,w,h;
    private ViewGroup floatview;
    private int status=-1;

    public FaceProxy(UZModule uzModule) {
        this.uzModule = uzModule;
        this.context=uzModule.getContext();
        this.status=-1;
    }

    public void initSDK(){
        //初始化导入管理器
        fileManager= ImportFileManager.getInstance();
        fileManager.setOnImportListener(this);
        //sdk校验初始化
        faceAuth = new FaceAuth();
        // 建议3288板子flagsThreads设置2,3399板子设置4
        faceAuth.setAnakinThreadsConfigure(2, 0);
        showMyDialog(0);
    }

    public void registerFace(){
        showMyDialog(1);
    }


    /**
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param fixon 依附的 h5 页面，为空就是独立页面
     * @param fixed 是否依附页面，依附的话会随着页面滑动而滑动
     */
    public void openFaceRecognition(int x,int y,int w,int h,String fixon,boolean fixed){
        if (status!=1){
            ((FaceSDKModule)uzModule).alertMess("还没有进行SDK初始化操作！");
            return;
        }else {
            this.x=x;
            this.y=y;
            this.w=w;
            this.h=h;
            if(floatview==null) {
                floatview = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.null_layout,null);
            }
            if (null==floatview.getParent()){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w/3, h/3);
                lp.setMargins(x,y,0,0);
                //UI类模块都应该实现fixedOn和fixed，标识该UI模块是挂在window还是frame上，是跟随网页滚动还是不跟随滚动
                //fixedOn为frame的name值。
                //通常，fixedOn为空或者不传时，UI模块默认挂在window上，如果有值，则挂在名为fixedOn传入的值的frame上
                uzModule.insertViewToCurWindow(floatview, lp, fixon, fixed);
                if (fragment==null){
                    fragment=new FaceFragment(this);
                }
                if (fragmentManager==null){
                    fragmentManager= uzModule.getContext().getFragmentManager();
                }
                FrameLayout.LayoutParams containerLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                FrameLayout frameLayout= floatview.findViewById(R.id.rootview_facesdk);
                frameLayout.setLayoutParams(containerLP);

                //通过回调设置fragment，在floatview没有添加到窗口之前是不能添加fragment的
                floatview.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        for (int i=0;i<floatview.getChildCount();i++){
                            if (floatview.getChildAt(i).getId()==R.id.rootview_facesdk){
                                FragmentTransaction transaction= fragmentManager.beginTransaction();
                                transaction.add(R.id.rootview_facesdk, fragment);
                                transaction.commit();
                            }
                        }
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {

                    }
                });
            }
        }
    }

    /**
     * 关闭视图
     */
    public void closeFace(){
        if ( fragment!=null && floatview!=null){
            for (int i=0;i<floatview.getChildCount();i++){
                if (floatview.getChildAt(i).getId()==R.id.rootview_facesdk){
                    FragmentTransaction transaction= fragmentManager.beginTransaction();
                    transaction.remove(fragment);
                    transaction.commit();
                }
            }
            uzModule.removeViewFromCurWindow(floatview);
        }
        fragment=null;
        floatview.removeAllViews();
        floatview=null;
    }


    public void destroy(){
        uzModule=null;
        context=null;
        if (fileManager!=null)
            fileManager.release();
    }

    @Override
    public void startImport() {

    }

    @Override
    public void onImporting(float v) {

    }

    @Override
    public void endImport(int i, int i1, int i2) {

    }

    @Override
    public void showToastMessage(String s) {

    }

    /**
     * sdk在线认证之后的回调
     */
    @Override
    public void onResponse(final int code, final String response, String licenseKey) {
        if (code == 0) {
            GlobalSet.FACE_AUTH_STATUS = 0;
            // 初始化人脸
            FaceSDKManager.getInstance().initModel(context);
            // 初始化数据库
            DBManager.getInstance().init(context);
            // 加载feature 内存
            FaceSDKManager.getInstance().setFeature();
            GlobalSet.setLicenseOnLineKey(keyCode);
            GlobalSet.setLicenseStatus(LICENSE_ONLINE);
            status=1;
            ToastUtils.toast(context, "校验成功，可以进行其他操作!");
        } else {
            ToastUtils.toast(context, code + "  " + response);
        }
    }

    /**
     * @param type 0代表注册用户，1代表初始化sdk
     */
    public void showMyDialog(int type){
        if (type==1){
            View view= uzModule.getContext().getLayoutInflater().inflate(R.layout.register_layout,null);
            final MyDIaLog myDIaLog=new MyDIaLog(context,view);

            final EditText path= view.findViewById(R.id.path);
            final EditText name=view.findViewById(R.id.name);
            Button button= view.findViewById(R.id.submit);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerPerson(path.getEditableText().toString(),name.getEditableText().toString());
                    myDIaLog.dismiss();
                }
            });
            myDIaLog.show();

        }else if (type==0){

            View view= uzModule.getContext().getLayoutInflater().inflate(R.layout.setting_layout,null);
            final MyDIaLog myDIaLog=new MyDIaLog(context,view);
            final EditText code=view.findViewById(R.id.code);
            Button button= view.findViewById(R.id.submit);
            final String key=  "DLIX-1WPH-HKD7-0VHX" ;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initLicenseOnLine(code.getEditableText().toString());
                    myDIaLog.dismiss();
                }
            });
            myDIaLog.show();
        }
    }



    /**
     * 在线校验sdk
     * @param key
     */
    private void initLicenseOnLine(final String key) {
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(context, "序列号不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        faceAuth.initLicenseOnLine(context, key,this);
        keyCode=key;
    }


    /**
     * 注册人员信息
     */
    public void registerPerson(String path,String name){
        // 复制assets下文件到sdcard目录
        if (!GlobalSet.getIsImportSample()) {
            FileUtils.copyAssetsFiles2SDCard(context, "ImportSrc",
                    Environment.getExternalStorageDirectory().getPath());
            GlobalSet.setIsImportSample(true);
        }
        fileManager.singleImport(path,name);
   }
}
