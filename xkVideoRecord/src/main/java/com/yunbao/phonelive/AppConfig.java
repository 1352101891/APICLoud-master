package com.yunbao.phonelive;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;

/**
 * Created by cxf on 2017/8/4.
 */

public class AppConfig {
    //域名
    public static final String HOST = "http://dsp.yunbaozhibo.com";
    public static final String URI = "/api/public";
    //sd卡路径
    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    //保存视频的时候，在sd卡存储短视频的路径
    public static final String VIDEO_PATH = SD_PATH + "/phoneLive/video/";
    //下载音乐时候，在sd卡存储音乐的路径
    public static final String MUSIC_PATH = SD_PATH + "/phoneLive/music/";
    //支付宝回调地址
    public static final String ALI_PAY_NOTIFY_URL = HOST + "/Appapi/Pay/notify_ali";
    public static final String VISITOR = "-9999";//游客id
    private static AppConfig sInstance;
    private AppConfig() {
    }
    public static AppConfig getInstance() {
        if (sInstance == null) {
            synchronized (AppConfig.class) {
                if (sInstance == null) {
                    sInstance = new AppConfig();
                }
            }
        }
        return sInstance;
    }

    private ConfigBean mConfigBean;

    //socket服务器
    private String mSocketServer;


    //这个值就是getBaseUserInfo接口里返回的info[0]
    private String mUserInfo;

    //app是否启动的标识,极光推送用到
    private boolean mLaunched;

    private String mUid = "";
    private String mToken = "";
    //用户当前所在的经度
    private String mLng = "";
    //用户当前所在的纬度
    private String mLat = "";
    //当前所在的省
    private String mProvince = "";
    //当前所在的城市
    private String mCity = "";
    //是否忽略未读消息
    private boolean mIgnoreUnReadMessage;
    //腾讯云存储远程上传的图片文件路径
    private String folderimg = "test1";
    //腾讯云存储远程上传的视频文件路径
    private String foldervideo = "test1";

    //钻石的名字
    private String name_coin;
    //映票的名字
    private String name_votes;

    public void reset() {
        mConfigBean = null;
        mUid = VISITOR;
        mToken = "";
        mLng = "";
        mLat = "";
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public String getVersion() {
        try {
            PackageManager manager = AppContext.mApplication.getPackageManager();
            PackageInfo info = manager.getPackageInfo(AppContext.mApplication.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }




    public ConfigBean getConfig() {
        if (mConfigBean == null) {
            String s = SharedPreferencesUtil.getInstance().readConfig();
            if (!"".equals(s)) {
                mConfigBean = JSON.parseObject(s, ConfigBean.class);
            }
        }
        return mConfigBean;
    }

    public void setConfig(ConfigBean bean) {
        mConfigBean = bean;
    }

    public boolean isLaunched() {
        return mLaunched;
    }

    public void setLaunched(boolean launched) {
        mLaunched = launched;
    }


    public String getSocketServer() {
        return mSocketServer;
    }

    public void setSocketServer(String socketServer) {
        mSocketServer = socketServer;
    }

    public String getUid() {
        if (mUid.equals(VISITOR)) {
          /*  Intent intent = new Intent(AppContext.sInstance, LoginInvalidActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("msg", "");
            AppContext.sInstance.startActivity(intent);*/
        }
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getLng() {
        return mLng;
    }

    public void setLng(String lng) {
        mLng = lng;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        mProvince = province;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getFolderimg() {
        return folderimg;
    }

    public void setFolderimg(String folderimg) {
        this.folderimg = folderimg;
    }

    public String getFoldervideo() {
        return foldervideo;
    }

    public void setFoldervideo(String foldervideo) {
        this.foldervideo = foldervideo;
    }

    public String getName_coin() {
        return name_coin;
    }

    public void setName_coin(String name_coin) {
        this.name_coin = name_coin;
    }

    public String getName_votes() {
        return name_votes;
    }

    public void setName_votes(String name_votes) {
        this.name_votes = name_votes;
    }
}
