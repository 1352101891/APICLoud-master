package com.videolib.android.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.videolib.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * WIFI 列表对话框
 *
 * @author DZS dzsdevelop@163.com
 * @version V1.0
 * @date 2017/3/29
 */
public class WiFiListDialog extends Dialog {

    private ValueCallback<ScanResult> valueCallBack;
    private List<ScanResult> mScanResultList = new ArrayList<>();
    private Adapter adapter;
    private Activity activity;

    public WiFiListDialog(Activity context) {
        super(context);
        activity = context;
        setContentView(R.layout.dialog_wifi_list);
        this.setCanceledOnTouchOutside(true);
        ListView mListView = findViewById(R.id.lv_camera);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (valueCallBack != null && is24GHz((mScanResultList.get(position)).frequency)) {
                    valueCallBack.onReceiveValue(mScanResultList.get(position));
                    WiFiListDialog.this.dismiss();
                }
            }
        });
        findViewById(R.id.Dialog_Refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loadData();
            }
        });
        adapter = new Adapter(context);
        mListView.setAdapter(adapter);
        loadData();
    }

    /*加载数据*/
    private void loadData() {
        mScanResultList.clear();
        mScanResultList = getWifiList();
        if (mScanResultList == null || mScanResultList.isEmpty()) {
            new AlertDialog.Builder(activity).setMessage("Lack of location permissions for current applications").setNegativeButton("Set permissions",
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            return;
        }
        Collections.sort(mScanResultList, comparator);
        if (mScanResultList != null && mScanResultList.size() > 0) adapter.replaceAll(mScanResultList);
    }

    /*设置点击返回*/
    public void setItemClickBackListener(ValueCallback<ScanResult> itemClickBack) {
        valueCallBack = itemClickBack;
    }


    /*获取WiFi列表*/
    @SuppressLint("MissingPermission")
    private List<ScanResult> getWifiList() {
        WifiManager wm = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wm.startScan();
        return wm.getScanResults();
    }

    /*判断是否是5G网络*/
    private boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }

    /*判断是否是2.4G网络*/
    private boolean is24GHz(int freq) {
        return freq > 2400 && freq < 2500;
    }

    /*比较器*/
    private Comparator<ScanResult> comparator =new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult o1, ScanResult o2) {
            int flag = Integer.compare(o1.frequency, o2.frequency);
            if (flag == 0) {
                return -Integer.compare(o1.level, o2.level);
            }
            return flag;
        }
    };

    /*适配器*/
    private class Adapter extends BaseAdapter {
        private Context context;
        private List<ScanResult> list = new ArrayList<>();

        Adapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ScanResult getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.adapter_dialog_wifi_list, null);
                viewHolder = new ViewHolder();
                viewHolder.wifiTitle = view.findViewById(R.id.tv_wifi_name);
                viewHolder.wifiLeave = view.findViewById(R.id.iv_wifi_status);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            setData(viewHolder, list.get(i));
            return view;
        }

        public void replaceAll(List<ScanResult> mScanResultList) {
            list.clear();
            list.addAll(mScanResultList);
            notifyDataSetChanged();
        }

        private class ViewHolder {
            TextView wifiTitle, wifiLeave;
        }

        private void setData(ViewHolder viewHolder, ScanResult scanResult) {
            viewHolder.wifiTitle.setText(scanResult.SSID);
            viewHolder.wifiTitle.setTextColor(context.getResources().getColor(is24GHz(scanResult.frequency) ? android.R.color.black : android.R.color.darker_gray));
            int level = scanResult.level;
            if (TextUtils.isEmpty(scanResult.capabilities) || "[ESS]".equals(scanResult.capabilities)) {
                if (level < -100) {
                    viewHolder.wifiLeave.setText("强度-1");
                } else if (-100 <= level && level < -70) {
                    viewHolder.wifiLeave.setText("强度-2");
                } else if (-70 <= level && level < -40) {
                    viewHolder.wifiLeave.setText("强度-3");
                } else {
                    viewHolder.wifiLeave.setText("强度-4");
                }
            } else {
                if (level < -100) {
                    viewHolder.wifiLeave.setText("加密-强度-1");
                } else if (-100 <= level && level < -70) {
                    viewHolder.wifiLeave.setText("加密-强度-2");
                } else if (-70 <= level && level < -40) {
                    viewHolder.wifiLeave.setText("加密-强度-3");
                } else {
                    viewHolder.wifiLeave.setText("加密-强度-4");
                }
            }
        }
    }

}
