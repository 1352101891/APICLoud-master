package com.videolib.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.videolib.android.R;
import com.videolib.android.app.AppContext;
import com.videolib.android.utils.Constant;
import com.worthcloud.avlib.basemedia.MediaControl;
import com.worthcloud.avlib.bean.EventMessage;
import com.worthcloud.avlib.bean.PlaybackProgress;
import com.worthcloud.avlib.bean.TFRemoteFile;
import com.worthcloud.avlib.bean.TFSearchPrm;
import com.worthcloud.avlib.event.eventcallback.BaseEventCallback;
import com.worthcloud.avlib.event.eventcallback.EventCallBack;
import com.worthcloud.avlib.event.eventcode.EventCode;
import com.worthcloud.avlib.event.eventcode.EventTypeCode;
import com.worthcloud.avlib.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * TF card recording list
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/7/15
 */
public class TFVideoListActivity extends Activity implements BaseEventCallback.OnEventListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<TFRemoteFile> tfList;
    private String deviceID = "";
    private long linkHandle = 0;
    private RecyclerView.Adapter adapter;
    private boolean isRefresh = false;
    private AppContext appContext;
    private int page = 2;
    public AppContext getAppContext(){
        return AppContext.initApp(getApplication());
    }
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tfvideolist);
        initView();
        appContext = (AppContext)getAppContext() ;
        linkHandle = getIntent().getLongExtra(Constant.INTENT_LINK_HANDLER, 0);
        deviceID = getIntent().getStringExtra(Constant.INTENT_STRING);
        if (linkHandle == 0 || TextUtils.isEmpty(deviceID)) {
            appContext.showToast("wrong parameter");
            finish();
        }
        tfList = new ArrayList<>();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
    }

    private void initView() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recycleLV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(TFVideoListActivity.this).inflate(R.layout.iteamlayout, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder,final int position) {
                holder.textView.setText(tfList.get(position).getFileName());
                holder.iteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        play(position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return tfList.size();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setRefreshing(true);
        EventCallBack.getInstance().addCallbackListener(this);
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate2();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventCallBack.getInstance().addCallbackListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventCallBack.getInstance().removeListener(this);
    }

    /*Play*/
    private void play(int position) {
        Intent intent = new Intent(this, TFVideoPlayActivity.class);
        intent.putExtra(Constant.INTENT_LINK_HANDLER, linkHandle);
        intent.putExtra(Constant.INTENT_DATABEAN, tfList.get(position));
        intent.putExtra(Constant.INTENT_STRING, deviceID);
        startActivity(intent);
    }


    /*Obtain TF card files*/
    private void getData() {
        swipeRefreshLayout.setRefreshing(true);
        isRefresh = true;
        //Files generated from 0:00-24:00 are set to obtained. It is recommended to use 4 hours as the time point in the actual project (too long time will lead to incomplete documents)。
        //MediaControl.getInstance().p2pSearchTFRemoteFile(new TFSearchPrm(deviceID, 0, getTodayTime(0), getTodayTime(24)));
        MediaControl.getInstance().p2pGetTFRemoteFile(new TFSearchPrm(deviceID, "123456", 0, 1550505652, 1550591940, 2, 15));
    }

    private void getDate2() {
        MediaControl.getInstance().p2pGetTFRemoteFile(new TFSearchPrm(deviceID, "123456", 0, 1550505652, 1550591940, page, 15));
        page++;
    }

    /*Get a timestamp of 0:00 - 24:00 today*/
    private long getTodayTime(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @Override
    public void onEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getMessageCode()) {
            //case EventCode.E_EVENT_CODE_MSG_REMOTE_FILE_SEARCH_SUCCESS:
            case EventCode.E_EVENT_CODE_MSG_REMOTEFILE_SEARCH_SUCCESS:
                List<TFRemoteFile> list = (List<TFRemoteFile>) eventMessage.getObject();
                if (list != null && !list.isEmpty()) {
                    for (TFRemoteFile t : list) {
                        LogUtils.debug("开始时间：" + t.getStartTime() + "----结束时间：" + t.getEndTime());
                    }
                }
                if (list == null || list.size() <= 0) {
                    appContext.showToast("Blank list");
                } else {
                    if (isRefresh) {
                        isRefresh = false;
                        tfList.clear();
                    }
                    tfList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
                break;
            case EventCode.E_EVENT_CODE_MSG_REMOTE_FILE_SEARCH_FAILURE:
                appContext.showToast("Failed");
                swipeRefreshLayout.setRefreshing(false);
                break;
            case EventCode.E_EVENT_CODE_MSG_P2P_ERROR://P2P-发生异常
                appContext.showToast("P2P Error");
                swipeRefreshLayout.setRefreshing(false);
                break;
            case EventCode.E_EVENT_CODE_CAMERAPWD_CHECK_FAILED:
                appContext.showToast("Wrong password");
                swipeRefreshLayout.setRefreshing(false);
                break;
            default:
                break;
        }
    }

    /*adapter ViewHolder*/
    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        View iteView;

        ViewHolder(View itemView) {
            super(itemView);
            this.iteView = itemView;
            textView = itemView.findViewById(R.id.TV_FileName);
        }
    }
}
