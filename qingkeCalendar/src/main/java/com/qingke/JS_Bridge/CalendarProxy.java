package com.qingke.JS_Bridge;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qingke.DateBean;
import com.qingke.adapter.CusCalendarAdapter;
import com.qingke.adapter.ListviewAdapter;
import com.qingke.calendar.CaledarAdapter;
import com.qingke.calendar.CalendarBean;
import com.qingke.calendar.CalendarDateView;
import com.qingke.calendar.CalendarUtil;
import com.qingke.calendar.CalendarView;
import com.qingke.calendar.R;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lvqiu on 2018/10/28.
 */

public class CalendarProxy {
    private UZModule uzModule;
    private Context mActivity;
    private int x,y,w,h;
    private View floatview;
    private Handler handler;
    private TextView left;
    private TextView right;
    private String today="";

    TextView mTitle;
    CalendarDateView mCalendarDateView;
    ListView mList;
    ListviewAdapter adapter;
    CaledarAdapter caledarAdapter;
    Button addbutton;

    public CalendarProxy(UZModule uzModule) {
        this.uzModule = uzModule;
        this.mActivity=uzModule.getContext();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        today=df.format(new Date());
        handler=new Handler(mActivity.getMainLooper());
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
    public void createFrame(int x,int y,int w,int h,String fixon,boolean fixed){
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
        if(floatview==null) {
            floatview = LayoutInflater.from(mActivity).inflate(R.layout.activity_xiaomi,null);
        }
        if (null==floatview.getParent()){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
            lp.setMargins(x,y,0,0);
            //UI类模块都应该实现fixedOn和fixed，标识该UI模块是挂在window还是frame上，是跟随网页滚动还是不跟随滚动
            //fixedOn为frame的name值。
            //通常，fixedOn为空或者不传时，UI模块默认挂在window上，如果有值，则挂在名为fixedOn传入的值的frame上
            uzModule.insertViewToCurWindow(floatview, lp, fixon, true);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
                right.setText("当前时间："+df.format(new Date()));
                handler.postDelayed(this,1000*60);
            }
        },1000*60);
        initView();
    }


    private void initView() {
        mTitle=(TextView) floatview.findViewById(R.id.title);
        addbutton=(Button) floatview.findViewById(R.id.addbutton);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CloudClassModule)uzModule).resultCallback(null,CloudClassModule.ADD);
            }
        });
        //日记列表
        mList=(ListView) floatview.findViewById(R.id.list);
        if (adapter==null){
            adapter=new ListviewAdapter(mActivity);
        }
        View view= LayoutInflater.from(mActivity).inflate(R.layout.item_header,null);
        left= (TextView) view.findViewById(R.id.left);
        left.setText("今天");
        right=(TextView) view.findViewById(R.id.right);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        right.setText("当前时间："+df.format(new Date()));
        mList.addHeaderView(view);
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    return;
                }
                ((CloudClassModule)uzModule).resultCallback(adapter.getItem(position-1),CloudClassModule.RIJI);
            }
        });

        //日历控件
        mCalendarDateView=(CalendarDateView) floatview.findViewById(R.id.calendarDateView);
        if (caledarAdapter==null){
            caledarAdapter=new CusCalendarAdapter(today);
        }
        mCalendarDateView.setAdapter(caledarAdapter);
        mCalendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, CalendarBean bean) {
                mTitle.setText(bean.year + "-" + bean.moth + "-" + bean.day);
                ((CloudClassModule)uzModule).resultCallback(bean.year + "-" + bean.moth + "-" + bean.day,CloudClassModule.DATE);
            }
        });

        int[] data = CalendarUtil.getYMD(new Date());
        mTitle.setText(data[0] + "/" + data[1] + "/" + data[2]);
    }


    public void reloadData(ArrayList<DateBean> dateBeans){
        this.adapter.updateData(dateBeans);
    }

    public void updateDate(ArrayList<String> marks){
        this.mCalendarDateView.updateData(marks);
    }

    public void hidenFrame(){

    }


    public void showFrame(){

    }

    public void clear(){
        mActivity=null;
        uzModule=null;
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
    }
}
