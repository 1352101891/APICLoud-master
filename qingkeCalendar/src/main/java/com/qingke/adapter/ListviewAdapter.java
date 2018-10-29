package com.qingke.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qingke.DateBean;
import com.qingke.calendar.R;

import java.util.ArrayList;

/**
 * Created by lvqiu on 2018/10/28.
 */

public class ListviewAdapter  extends BaseAdapter {
    public Context mContext;
    private ArrayList<DateBean> dateBeans=new ArrayList<>();

    public ListviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if (dateBeans==null && dateBeans.size()==0){
            return 0;
        }
        return dateBeans.size();
    }

    @Override
    public Object getItem(int position) {
        if (dateBeans==null && dateBeans.size()==0){
            return null;
        }
        return dateBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_riji, null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        int color=Color.parseColor("#"+dateBeans.get(position).color);
        viewHolder.title.setText(dateBeans.get(position).title+"");
        viewHolder.line.setBackgroundColor(color);
        viewHolder.time.setText(dateBeans.get(position).endtime+"截止");
        viewHolder.statue.setText(dateBeans.get(position).status+"");

        return convertView;
    }

    public void updateData(ArrayList<DateBean> dateBeans){
        if (dateBeans==null || dateBeans.size()==0){
            this.dateBeans.clear();
        }else {
            this.dateBeans=dateBeans;
        }
        notifyDataSetChanged();
    }


    public static class ViewHolder{
        private TextView title;
        private TextView time;
        private View line;
        private TextView statue;

        public ViewHolder(View view) {
            title= (TextView) view.findViewById(R.id.title);
            time=(TextView) view.findViewById(R.id.time);
            line=(View) view.findViewById(R.id.line);
            statue=(TextView) view.findViewById(R.id.statue);
        }
    }
}
