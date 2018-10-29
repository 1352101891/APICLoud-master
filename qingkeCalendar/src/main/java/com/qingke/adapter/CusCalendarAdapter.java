package com.qingke.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qingke.calendar.CaledarAdapter;
import com.qingke.calendar.CalendarBean;
import com.qingke.calendar.R;

import java.util.ArrayList;

/**
 * Created by lvqiu on 2018/10/28.
 */

public class CusCalendarAdapter implements CaledarAdapter {
    private String today="";
    private ArrayList<String> marks=new ArrayList<>();

    public CusCalendarAdapter(String today) {
        this.today = today;
    }

    @Override
    public View getView(View convertView, ViewGroup parentView, CalendarBean bean) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_xiaomi, null);
        }

        TextView chinaText = (TextView) convertView.findViewById(R.id.chinaText);
        TextView text = (TextView) convertView.findViewById(R.id.text);
        View view= convertView.findViewById(R.id.mark);
        if (isMark(bean)){
            view.setVisibility(View.VISIBLE);
        }else {
            view.setVisibility(View.GONE);
        }
        if (today.equals(bean.year+"-"+bean.moth+"-"+bean.day)){
            text.setText("今");
        }else {
            text.setText("" + bean.day);
        }
        if (bean.mothFlag != 0) {
            text.setTextColor(0xff9299a1);
        } else {
            text.setTextColor(0xff444444);
        }
        chinaText.setText(bean.chinaDay);

        return convertView;
    }

    @Override
    public void updateData(Object marks){
        this.marks= (ArrayList<String>) marks;

    }

    public boolean isMark(CalendarBean bean){
        if (marks!=null && marks.size()>0)
        for (String markdate: marks) {
            if (markdate.equals(bean.year+"-"+bean.moth+"-"+bean.day)){
                return true;
            }
        }
        return false;
    }

}
