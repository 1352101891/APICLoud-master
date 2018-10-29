package com.qingke.JS_Bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import com.google.gson.Gson;
import com.qingke.DateBean;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import static com.qingke.JS_Bridge.util.getJO;


/**
 * Created by Administrator on 2018/4/9.
 */

public class CloudClassModule extends UZModule {
    static String success= "{key:成功}";
    static String error= "{key:内部错误}";
    static String error1= "{key:测试超时，请重新安装！}";
    int REQUEST_ACTIVITY=111;
    private AlertDialog.Builder mAlert;
    private UZModuleContext mJsCallback;
    private static final String RESULT="RESULT";
    private CalendarProxy calendarProxy;
    public CloudClassModule(UZWebView webView) {
        super(webView);
        calendarProxy=new CalendarProxy(this);
    }


    /**打开日历页面
     * @param moduleContext
     */
    public void jsmethod_qingkeCalendarCreatView(final UZModuleContext moduleContext){
        String framename=moduleContext.optString("framename");
        int x = moduleContext.optInt("x");//JS传入的x
        int y = moduleContext.optInt("y");//JS传入的y
        int w = moduleContext.optInt("w");//JS传入的w
        int h = moduleContext.optInt("h");//JS传入的h
//        x= util.dp2px(mContext,x);
//        y= util.dp2px(mContext,y);
//        w= util.dp2px(mContext,w);
//        h= util.dp2px(mContext,h);

        ArrayList<DateBean> dateBeans=getList("data",moduleContext);
        calendarProxy.createFrame(x,y,w,h,framename,true);
        this.mJsCallback=moduleContext;
    }


    /**刷新数据
     * @param moduleContext
     */
    public void jsmethod_qingkeCalendarReloadView(final UZModuleContext moduleContext){
        ArrayList<DateBean> dateBeans=getList("data",moduleContext);
        calendarProxy.reloadData(dateBeans);
    }

    /**特殊标记的日子
     添加有任务的时间
     var selectData = ["2018-10-23","2018-10-24","2018-10-25",];
     * @param moduleContext
     */
    public void jsmethod_qingkeCalendarSelectData(final UZModuleContext moduleContext){
        ArrayList<String> strings=getStringList("data",moduleContext);
        calendarProxy.updateDate(strings);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null || data.getExtras()==null){
            return;
        }

        if (requestCode==REQUEST_ACTIVITY && resultCode== Activity.RESULT_OK){
            mJsCallback.success(getJO(success), true);
            Log.e("CloudClassModule",""+success);
        }else {
            returnError(error);
        }
    }

    public void returnError(String es){
        JSONObject object=null;
        try {
            object=new JSONObject(es);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsCallback.error(object,object,true);
    }

    public JSONObject getJo(String es){
        JSONObject object=null;
        try {
            object=new JSONObject(es);
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    protected void onClean() {
        if(null != mAlert){
            mAlert = null;
        }
        if(null != mJsCallback){
            mJsCallback = null;
        }
        calendarProxy.clear();
    }

    public ArrayList<DateBean> getList(String key,UZModuleContext moduleContext){
        ArrayList<DateBean> dateBeans=new ArrayList<>();
        JSONArray array=moduleContext.optJSONArray("data");
        try {
            if (array!=null && array.length()>0)
                for (int i=0;i<array.length();i++){
                    Gson gson=new Gson();
                    DateBean dateBean=gson.fromJson(array.get(i).toString(),DateBean.class);
                    dateBeans.add(dateBean);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dateBeans;
    }


    public ArrayList<String> getStringList(String key,UZModuleContext moduleContext){
        ArrayList<String> dateBeans=new ArrayList<>();
        JSONArray array=moduleContext.optJSONArray("data");
        try {
            if (array!=null && array.length()>0)
                for (int i=0;i<array.length();i++){
                    Gson gson=new Gson();
                    String dateBean=gson.fromJson(array.get(i).toString(),String.class);
                    dateBeans.add(dateBean);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dateBeans;
    }

    //{'eventType':'time','time':'2019-10-29'}
    public final static String ADD="ADD";
    public final static String RIJI="RIJI";
    public final static String DATE="DATE";
    public void resultCallback(Object o,String type){
        HashMap<String,String> map=new HashMap<>();
        switch (type){
            case ADD:
                map.put("eventType","addEvent");
                break;
            case RIJI:
                map.put("eventType","clickEvent");
                map.put("content", new Gson().toJson(o));
                break;
            case DATE:
                map.put("eventType","time");
                map.put("time", (String) o);
                break;
        }
        JSONObject jsonObject=null;
        String result= new Gson().toJson(map);
        try {
            jsonObject=new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsCallback.success(jsonObject,false);
    }


}
