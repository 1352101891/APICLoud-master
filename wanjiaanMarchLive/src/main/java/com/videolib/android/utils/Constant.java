package com.videolib.android.utils;

import java.io.File;

/**
 * 静态常量
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/4/24
 */
public class Constant {
    //推流地址
    public static final String PUSHURL = "rtmp://ali-zdk-test.oss-cn-beijing.aliyuncs.com/live/2e70943cec1ddd6d994a46f609ddd19c?OSSAccessKeyId=STS.NHZUERJZ1WP5Bvw7CpqAibbkV&Expires=1526631165&Signature=X%2BMyTwmLZ6TaxASTqLZOogjb3zc%3D";
    //推流播放地址
    public static final String PLAYURL = "http://rtmp.zhiboyun.net/zhiboyun/zhidekan_72992010_Slx2.flv";
    //视频地址
    public static final String VIDEOURL = "http://jingchangkan.tv/201704241408/ailuosihuayi.mp4.m3u8";

    public static final String KEY_QRSCAN = "QRSCAN";
    public static final String INTENT_STRING = "intent_string";
    public static final String INTENT_DATABEAN = "intent_data_bean";
    public static final String INTENT_LINK_HANDLER = "intent_link_handler";
    public static final String INTENT_USERID = "intent_user_id";

    public static final String SP_ACCESSKEY = "SP_ACCESSKEY";
    public static final String SP_SECRETKEY = "SP_SECRETKEY";
    public static final String SP_TOKEN = "SP_TOKEN";
    public static final String SP_DEV_CODE = "SP_DEV_CODE";
    public static final String SP_USERID = "SP_USERID";
    public static final String SP_DEV_PASSWORD = "SP_DEV_PASSWORD";

    //验证类型
    public static final String SP_LINK_STATUS = "SP_LINK_STATUS";
    //服务器类型
    public static final String SP_SERVER_STATUS = "SP_SERVER_STATUS";

    public static final String SP_PASS_SERVER = "SP_PASS_SERVER";
    public static final String SP_PASS_SERVER_PROT = "SP_PASS_SERVER_PROT";
    public static final String SP_TURN_SERVER = "SP_TURN_SERVER";
    public static final String SP_TURN_SERVER_PROT = "SP_TURN_SERVER_PROT";
    public static final String SP_AUTH_URL = "SP_AUTH_URL";


    //公司测试设备
    //public static final String TEST_UUID = "1501862170000040";
    //public static final String TEST_UUID = "2000413890007590";
    //public static final String TEST_UUID = "2000199260023327";
    //public static final String TEST_UUID = "2000210660023335";
    public static final String TEST_UUID = "2000240460023397";
    //public static final String ACCESSKEY = "cfcf48dc6a19561e905c6836c92e238e";
    public static final String ACCESSKEY = "4c0d13d3ad6cc317017872e51d01b238";
    public static final String SECRETKEY = "6131745947ed1f8c31069c2e8b3342a2";
    public static final String TOKEN = "51a3f940787ab5321f2682289e618eb7";
    public static final String APPID = "2328";
    public static final String USERID = "329";

    public static final String PASSSERVER = "wcpaas.cppluscloud.com";
    public static final String PASSSERVER_PROT = "4755";
    public static final String TURNSERVER = "wcpaas.cppluscloud.com";
    public static final String TURNSERVER_PROT = "3478";
    public static final String AUTH_URL = "http://" +
            "/test.cgi";

    //Private Cloud
//    public static final String ACCESSKEY = "a71eb082dfd71e8a8d8a26a64f8d3f85";
//    public static final String SECRETKEY = "f93b8bbbac89ea22bac0bf188ba49a61";
//    public static final String TOKEN = "1a9d3b3bee42b8d4d3a8cd7944b52e33";
//    public static final String APPID = "1004";
//    public static final String USERID = "8888";
//    public static final String TEST_UUID = "2000760760005730";


    public static final String APP_ROOT_DIRECTORY = "WorthCloud";
    public static final String SCREENSHOTFOLDER = File.separator + APP_ROOT_DIRECTORY + File.separator + "ScreenShot";
}
